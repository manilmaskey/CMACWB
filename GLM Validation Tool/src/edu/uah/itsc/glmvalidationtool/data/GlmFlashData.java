package edu.uah.itsc.glmvalidationtool.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import edu.uah.itsc.glmvalidationtool.config.Config;

public class GlmFlashData {

	ArrayList <GlmFlashEntry> data = new ArrayList<GlmFlashEntry>();
	Connection con;
	Config conf = new Config();
	
	public GlmFlashData()
	{
		
	}

	public ArrayList<GlmFlashEntry> getData()
	{
		return data;
	}
	public void ReadFile(String filename) throws IOException 
	{
		String text = new String ("");
		
		System.out.println("reading file " + filename);
//		InputStream file = getClass().getResourceAsStream(filename);
		InputStream file = new FileInputStream(filename);
		if (file==null) {
			System.out.println("cannot read " + filename);
			//System.exit(0);
			throw new IOException();
		}
		BufferedReader input = new BufferedReader(new InputStreamReader(file));

		String line;
		// read file
		int lineCnt=0;
		while ((line = input.readLine()) != null) {
			GlmFlashEntry entry = new GlmFlashEntry(line);
			
			if (entry!=null)
				data.add(entry);
			lineCnt++;
//			if (lineCnt>1000) break; // only read 1000 entries for testing
		}
		System.out.println("GLM Flash Data file " + filename + " read " + lineCnt + " entries");
		
		input.close();
	}
	public void writeToDatabase() throws SQLException
	{

//		glm_vv=# \d flash_proxy;
//        Table "public.flash_proxy"
//Column     |            Type             |                        Modifiers
//----------------+-----------------------------+-------------------------
//----------------+-----------------------------+-------------------------
//----------------+-----------------------------+--------
//id             | integer                     | not null default nextval('flash_proxy_id_seq'::regclass)
//number         | integer                     |
//start_datetime | timestamp without time zone |
//end_datetime   | timestamp without time zone |
//energy         | real                        |
//footprint      | real                        |
//childcount     | integer                     |
//the_geom       | geometry(Point,4326)        |
//
		
   		String url = conf.getProtocolJdbcPostgresql() + conf.getServerIP() + "/" + conf.getDatabaseName(); 
		con = DataUtil.establishConnection(url, conf.getServerUname(), conf.getServerPw());
		Statement st = con.createStatement();
		String dateFormat = "yyyy-mm-dd hh24:mi:ss.MS";
//		String dateFormat = "yyyy-mm-dd hh24:mi:ss.fffffffff";
		int count=0;
		for (GlmFlashEntry entry:data) {
			count++;	
			float fval = entry.getEnergy();	
			float fLat = entry.getLat();
			float fLon = entry.getLon();
			String sDate = new Timestamp(entry.getMillisecondsStart()).toString();
			String eDate = new Timestamp(entry.getMillisecondsEnd()).toString();
			st.addBatch("INSERT INTO " + conf.getGlmFlashTable() + "(number,start_datetime,end_datetime,energy,footprint,childcount,the_geom) VALUES(" + entry.getNumber() +",to_timestamp('"+sDate+"','" + dateFormat + "'),to_timestamp('"+eDate+"','" + dateFormat + "')," + fval +"," + entry.getFootprint()+ ","+ entry.getChildCount() + ",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326))");		      	    
			if (count % 100000 == 0){
				long before = System.currentTimeMillis();
		        st.executeBatch(); 	
				con.commit();
				System.out.println("time for commit: " + (System.currentTimeMillis() - before) + " ms");
				st.clearBatch();
				count = 0;
			}
		}
        st.executeBatch(); 	
        con.commit();
		con.close();

	}
		
}
