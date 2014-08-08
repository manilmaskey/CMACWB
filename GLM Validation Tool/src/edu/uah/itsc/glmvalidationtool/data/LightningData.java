package edu.uah.itsc.glmvalidationtool.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

import edu.uah.itsc.glmvalidationtool.config.Config;

public class LightningData {

//	public final static int ENTLN_FLASH =1;
//	public final static int ENTLN_STROKE =2;
//	public final static int NLDN_FLASH =3;
//	public final static int NLDN_STROKE =4;
//	public final static int GLD360 =5;
    private Connection con;
    private Config conf = new Config();
	
	ArrayList <LightningEntry> data = new ArrayList<>();
	private Config.DataType dataType;
	
//	public LightningData(int type, String filename) 
//	{
//		dataType = type;
//		// temporarily read testing file
//		try {
//			this.ReadFile(filename);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	public LightningData(Config.DataType type)
	{
		dataType = type;
	}
	public ArrayList<LightningEntry> getData()
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
		// skip first line
//		String line = input.readLine();
		String line;

		// read file
		int lineCnt=0;
		while ((line = input.readLine()) != null) {
			if (line.startsWith("#")) continue;  // skip # commented lines
			LightningEntry entry = new LightningEntry();
			switch (dataType) 
			{
				case ENTLN_FLASH:
				case ENTLN_STROKE:
					entry.parseEntln(line);
					break;
				case NLDN_FLASH:
				case NLDN_STROKE:
					entry.parseNldn(line);
					break;
				case GLD360:
					entry.parseGld360(line);
					break;
			}
			
			if (entry!=null)
				data.add(entry);
			lineCnt++;
//			if (lineCnt>1000) break; // only read 1000 entries for testing
		}
		System.out.println("Lightning Data file " + filename + " read " + lineCnt + " entries");
		
		input.close();
	}
	public void writeToDatabase() throws SQLException
	{
   		String url = conf.getProtocolJdbcPostgresql() + conf.getServerIP() + "/" + conf.getDatabaseName(); 
		con = DataUtil.establishConnection(url, conf.getServerUname(), conf.getServerPw());
		Statement st = con.createStatement();
		int count=0;
		long looptime = System.currentTimeMillis();
		for (LightningEntry entry:data) {
			count++;	
			float fval = entry.getValue();	
			float fLat = entry.getLat();
			float fLon = entry.getLon();
// use these instead of current method
			String dateFormat = "yyyy-mm-dd hh24:mi:ss.MS";
			String sDate = new Timestamp(entry.getMilliseconds()).toString();
			
			switch (dataType) 
			{
			//INSERT INTO etln_flash(datetime,measured_value,the_geom) VALUES(to_timestamp('2013-08-03 19:00:43.622.0022','yyyy-mm-dd hh24:mi:ss.MS.US'),0.003371,ST_GeomFromText('POINT(-100.62314 35.729755)',4326) );

			//						st.addBatch("INSERT INTO etln_flash(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+rundatetime+"','YYYY-MM-DD HH24:MI'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
				case ENTLN_FLASH:
					//('01-JAN-1970','DD-MON-YYYY HH24:MI:SS') + ( 1284622826913/ (1000*60 * 60 * 24)
//					st.addBatch("INSERT INTO etln_flash(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					st.addBatch("INSERT INTO "+ conf.getEntlnFlashTable() + "(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					break;
				case ENTLN_STROKE:
//					st.addBatch("INSERT INTO etln_stroke(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					st.addBatch("INSERT INTO " + conf.getEntlnStrokeTable() + "(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					break;
				case NLDN_FLASH:
//					st.addBatch("INSERT INTO nldn_flash(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					st.addBatch("INSERT INTO " + conf.getNldnFlashTable() + "(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					break;
				case NLDN_STROKE:
//					st.addBatch("INSERT INTO nldn_stroke(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					st.addBatch("INSERT INTO " + conf.getNldnStrokeTable() + "(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					break;
				case GLD360:
//					st.addBatch("INSERT INTO gld360(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					st.addBatch("INSERT INTO " + conf.getGld360Table() + "(datetime,measured_value,the_geom) VALUES"+"(to_timestamp('"+sDate+"','" + dateFormat + "'),"+fval+",ST_GeomFromText('POINT("+fLon+" "+fLat+")',4326) )");		      	    
					break;
			}
			if (count % 100000 == 0){
				long before = System.currentTimeMillis();
//				System.out.println("loop time: " + (before - looptime) + " ms");
		        st.executeBatch(); 	
				con.commit();
				System.out.println("time for commit: " + (System.currentTimeMillis() - before) + " ms");
				st.clearBatch();
				count = 0;
				looptime = System.currentTimeMillis();
			}
		}
        st.executeBatch(); 	
        con.commit();
		con.close();

	}
 
//    server: 54.83.58.23
//    database: glm_vv
//    ussername: postgres
//    password: password
//
//    Here are the table definitions:
//
//    glm_vv=# \d etln_flash;
//                                           Table "public.etln_flash"
//         Column     |            Type             |                        Modifiers
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------
//     id             | integer                     | not null default nextval('etln_flash_id_seq'::regclass)
//     datetime       | timestamp without time zone | not null
//     measured_value | real                        | not null
//     the_geom       | geometry(Point,4326)        |
//
//
//    glm_vv=# \d nldn_flash;
//                                           Table "public.nldn_flash"
//         Column     |            Type             |                        Modifiers
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------
//     id             | integer                     | not null default nextval('nldn_flash_id_seq'::regclass)
//     datetime       | timestamp without time zone | not null
//     measured_value | real                        | not null
//     the_geom       | geometry(Point,4326)        |
//
//    glm_vv=# \d etln_stroke;
//                                           Table "public.etln_stroke"
//         Column     |            Type             |                        Modifiers
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+--------
//     id             | integer                     | not null default nextval('etln_stroke_id_seq'::regclass)
//     datetime       | timestamp without time zone | not null
//     measured_value | real                        | not null
//     the_geom       | geometry(Point,4326)        |
//
//
//     glm_vv=# \d nldn_stroke
//                                           Table "public.nldn_stroke"
//         Column     |            Type             |                        Modifiers
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+--------
//     id             | integer                     | not null default nextval('nldn_stroke_id_seq'::regclass)
//     datetime       | timestamp without time zone | not null
//     measured_value | real                        | not null
//     the_geom       | geometry(Point,4326)        |
//
//
//    glm_vv=# \d gld360;
//                                           Table "public.gld360"
//         Column     |            Type             |                      Modifiers
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+---
//     id             | integer                     | not null default nextval('gld360_id_seq'::regclass)
//     datetime       | timestamp without time zone | not null
//     measured_value | real                        | not null
//     the_geom       | geometry(Point,4326)        |
//
//    glm_vv=# \d event_proxy;
//                                          Table "public.event_proxy"
//       Column    |            Type             |                        Modifiers
//    -------------+-----------------------------+----------------------------
//    -------------+-----------------------------+----------------------------
//    -------------+-----------------------------+--
//     id          | integer                     | not null default nextval('event_proxy_id_seq'::regclass)
//     number      | integer                     |
//     groupcount  | integer                     |
//     datetime    | timestamp without time zone | not null
//     x_pixel     | integer                     |
//     y_pixel     | integer                     |
//     energy      | real                        |
//     childnumber | integer                     |
//     the_geom    | geometry(Point,4326)        |
//
//
//    glm_vv=# \d flash_proxy;
//                                           Table "public.flash_proxy"
//         Column     |            Type             |                        Modifiers
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+-------------------------
//    ----------------+-----------------------------+--------
//     id             | integer                     | not null default nextval('flash_proxy_id_seq'::regclass)
//     number         | integer                     |
//     start_datetime | timestamp without time zone |
//     end_datetime   | timestamp without time zone |
//     energy         | real                        |
//     footprint      | real                        |
//     childcount     | integer                     |
//     the_geom       | geometry(Point,4326)        |
//


		
	}
