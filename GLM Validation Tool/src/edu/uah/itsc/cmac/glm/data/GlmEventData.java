package edu.uah.itsc.cmac.glm.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import edu.uah.itsc.cmac.glm.config.Config;

public class GlmEventData {

	ArrayList <GlmEventEntry> data = new ArrayList<GlmEventEntry>();
	Connection con;
	Config conf = new Config();
	
	public GlmEventData()
	{
		
	}
	public ArrayList<GlmEventEntry> getData()
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
			GlmEventEntry entry = new GlmEventEntry(line);
			
			if (entry!=null)
				data.add(entry);
			lineCnt++;
//			if (lineCnt>1000) break; // only read 1000 entries for testing
		}
		System.out.println("GLM Event Data file " + filename + " read " + lineCnt + " entries");
		
		input.close();
	}
	public void writeToDatabase() throws SQLException
	{
		
//		glm_vv=# \d event_proxy;
//        Table "public.event_proxy"
//Column    |            Type             |                        Modifiers
//-------------+-----------------------------+----------------------------
//-------------+-----------------------------+----------------------------
//-------------+-----------------------------+--
//id          | integer                     | not null default nextval('event_proxy_id_seq'::regclass)
//number      | integer                     |
//groupcount  | integer                     |
//datetime    | timestamp without time zone | not null
//x_pixel     | integer                     |
//y_pixel     | integer                     |
//energy      | real                        |
//childnumber | integer                     |
//the_geom    | geometry(Point,4326)        |
		
		
   		String url = conf.getProtocolJdbcPostgresql() + conf.getServerIP() + "/" + conf.getDatabaseName(); 
		con = DataUtil.establishConnection(url, conf.getServerUname(), conf.getServerPw());
		Statement st = con.createStatement();
		int count=0;
		String dateFormat = "yyyy-mm-dd hh24:mi:ss.MS";
//		String dateFormat = "yyyy-mm-dd hh24:mi:ss.fffffffff";
		for (GlmEventEntry entry:data) {
			count++;	
			float fval = entry.getEnergy();	
			float fLat = entry.getLat();
			float fLon = entry.getLon();
			String sDate = new Timestamp(entry.getMilliseconds()).toString();
			st.addBatch("INSERT INTO " + conf.getGlmEventTable() + "(number,groupcount,datetime,x_pixel,y_pixel,energy, childnumber,the_geom) VALUES(" + entry.getNumber() +"," + entry.getGroupCount() + ",to_timestamp('"+sDate+"','" + dateFormat + "')," + entry.getXpixel() +"," + entry.getYpixel()+ ","+ fval + "," + entry.getChildNumber() +",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326))");		      	    
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
