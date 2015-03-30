package edu.uah.itsc.xively.ingestor.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import edu.uah.itsc.xively.ingestor.config.XivelyConfig;
import edu.uah.itsc.xively.ingestor.service.Datastream;
import edu.uah.itsc.xively.ingestor.service.Feed;

public class XivelyPOSTGRESClient {

	public static void pushToDatabase(Feed feed){

		String dateFormat = "yyyy-mm-dd hh24:mi:ss.MS";
		
		//Connection to database
		Connection conn = null;
		Statement stmnt = null;

		try {
			Class.forName("org.postgresql.Driver");

			System.out.print("Connecting to dbserver: " + XivelyConfig.getDBServer() + " at port " + XivelyConfig.getDBPort());
			conn = DriverManager.getConnection(XivelyConfig.getDBProtocol() + XivelyConfig.getDBServer() + ":" + XivelyConfig.getDBPort() + "/" + XivelyConfig.getDBName(),
					XivelyConfig.getDBUsername(), XivelyConfig.getDBPassword());
			stmnt = conn.createStatement();

			System.out.print(" [CONNECTED]");
			System.out.println();

			//Step 1: Check if the device already exists in database... if not enter information for new device
			boolean deviceExists = false;
			String getDeviceSerial = "SELECT DISTINCT device_serial FROM devices;";
			ResultSet rs = stmnt.executeQuery(getDeviceSerial);

			while(rs.next()){
				if(rs.getString("device_serial").compareTo(feed.getDeviceSerial()) == 0){
					deviceExists = true;
					break;
				}
			}

			if(!deviceExists){
				System.out.println("New device feed detected: " + feed.getDeviceSerial());
				//Device doesnt exist in database so push the information for new deivce
				

				String newDeviceInsert = "INSERT INTO devices (device_serial,product_id,feed_id,title,private,feed_url,status,updated,created,creator,version,location) VALUES (";
				newDeviceInsert +=  "'" + feed.getDeviceSerial() + "'";
				newDeviceInsert +=  ",'" + feed.getProductId() + "'";
				newDeviceInsert +=  "," + Integer.parseInt(feed.getId());
				newDeviceInsert +=  ",'" + feed.getTitle() + "'";
				newDeviceInsert +=  "," + feed.getPrivate() ;			
				newDeviceInsert +=  ",'" + feed.getFeedUrl() + "'";
				newDeviceInsert +=  ",'" + feed.getStatus() + "'";
				newDeviceInsert +=  ",to_timestamp('" + feed.getUpdated() + "','" + dateFormat + "')";
				newDeviceInsert +=  ",to_timestamp('" + feed.getCreated() + "','" + dateFormat + "')";
				newDeviceInsert +=  ",'" + feed.getCreator() + "'";
				newDeviceInsert +=  ",'" + feed.getVersion() + "'";
				newDeviceInsert +=  ",ST_GeomFromText('POINT("+feed.getLon()+" "+feed.getLat()+")',4326)";
				newDeviceInsert += ");";

				stmnt.executeUpdate(newDeviceInsert);
				System.out.println("Inserted information for new device: " + feed.getDeviceSerial());
			}

			//Step 2: Check for new variables... insert if found
			String getVariablesList = "SELECT DISTINCT variable_id FROM variables;";
			rs = stmnt.executeQuery(getVariablesList);

			Vector<String> variableList = new Vector<String>();
			while(rs.next()){
				variableList.add(rs.getString("variable_id"));
			}

			Vector<String> feedVariableList = feed.getDatastreamIds();
			for(String pv : variableList){
				feedVariableList.remove(pv.toLowerCase());
			}

			String insertNewVariable = "INSERT INTO variables(variable_id,unit_symbol,unit_label) VALUES(";
			if(feedVariableList.size() > 0){
				System.out.println("New variables detected!");
				System.out.println("Variable List: " + feedVariableList.toString());

				for(String s : feedVariableList){
					stmnt.addBatch(insertNewVariable + "'" + s +"','" + feed.getUnitSymbolFor(s) + "','" + feed.getUnitLabelFor(s) + "');");
				}

				stmnt.executeBatch();
				System.out.println("Inserted new variables!");
			}
			
			
			//Step 3: Insert new reading for the variables from the device feed
			String insertIntoReadings = "";
			String deviceSerial = "";
			int count = 0;
			for(int i=0; i < feed.getDatastreamLength(); ++i){
				Datastream ds = feed.getDatastream(i);
				
				deviceSerial = feed.getDeviceSerial();
				
				insertIntoReadings = "INSERT INTO readings (device_serial,variable_id,current_value,taken_at,max_value,min_value) VALUES (";
				insertIntoReadings += "'" + feed.getDeviceSerial() + "'";
				insertIntoReadings += ",'" + ds.getStreamId() + "'";
				insertIntoReadings += "," + ds.getCurrentValue();
				insertIntoReadings += ", to_timestamp('"+ ds.getUpdated() +"','" + dateFormat +"')";
				insertIntoReadings += "," + ds.getMaxValue();
				insertIntoReadings += "," + ds.getMinValue() + ");";
				
				stmnt.addBatch(insertIntoReadings);
				count++;
			}
			System.out.println("Pushed "+ count +" reading(s) to database...");
			stmnt.executeBatch();
			
			//Insert into latest readings
			stmnt.execute("delete from latest_readings where device_serial='"+deviceSerial+"';");
			insertIntoReadings = "";
			count = 0;
			for(int i=0; i < feed.getDatastreamLength(); ++i){
				Datastream ds = feed.getDatastream(i);
				
				insertIntoReadings = "INSERT INTO latest_readings (device_serial,variable_id,current_value,taken_at,max_value,min_value) VALUES (";
				insertIntoReadings += "'" + feed.getDeviceSerial() + "'";
				insertIntoReadings += ",'" + ds.getStreamId() + "'";
				insertIntoReadings += "," + ds.getCurrentValue();
				insertIntoReadings += ", to_timestamp('"+ ds.getUpdated() +"','" + dateFormat +"')";
				insertIntoReadings += "," + ds.getMaxValue();
				insertIntoReadings += "," + ds.getMinValue() + ");";
				
				stmnt.addBatch(insertIntoReadings);
				count++;
			}
			//System.out.println("Pushed "+ count +" reading(s) to database...");
			stmnt.executeBatch();
			
			
			System.out.println("Closing connection to dbserver: " + XivelyConfig.getDBServer() + " at port " + XivelyConfig.getDBPort());
			stmnt.close();
			conn.close();

		} catch (ClassNotFoundException e) {
			System.out.println("Error: Unable to register postgres driver for java");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: Unable to connect to dbserver: "  + XivelyConfig.getDBServer() + " at port " + XivelyConfig.getDBPort());
			e.printStackTrace();
		} 
	}

}
