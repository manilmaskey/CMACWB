package edu.uah.itsc.radar.config;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RadarConfig {

	//Radius of Earth
	public static final float RADIUS_OF_EARTH = 6378100000.0f; // Millimeters
	
	//Server configurations
	public static String RADAR_IP = "vchill.chill.colostate.edu";
	public static int RADAR_PORT = 2511;
	
	//General constants
	public static final int HELLO = 0xF0F00F0F;
	public static final int DATA_CHANNEL = 15;
	
	//Type of feeds that can be requested
	public static final long FEED_ALL = Long.decode("0xFFFFFFFF");
	public static final long FEED_REFLECTIVITY = Long.decode("0x00000001");
	
	//Vaious header type
	public static final int HEADER_DATA =0x9090;
	public static final int HEADER_EXTENDED_TRACKING= 0x9494;
	public static final int HEADER_FIELD_INFO_TYPE = 0x9292;
	public static final int HEADER_HOUSEKEEPING = 0x9191;
	public static final int HEADER_POWER_METERS_UPDATE = 0x5aa50004;
	public static final int HEADER_PROCESSOR_INFO = 0x5aa50003;
	public static final int HEADER_RADAR_INFO = 0x5aa50001;
	public static final int HEADER_SCAN_SEGMENT = 0x5aa50002;
	public static final int HEADER_SWEEP_NOTICE = 0x5aa50005;
	public static final int HEADER_TRACKING = 0x9393;
	public static final int HEADER_TRANSMITTER_INFO = 0x5aa50008;
	
	//Various header size
	public static final int SIZE_FIELD_TYPE_INFO_HEADER = 224;
	public static final int SIZE_DATA_HEADER = 52;
	public static final int SIZE_SCAN_SEGMENT = 112;
	
	//Location to where NETCDF File will be published
	public static String NETCDF_PUBLISH_FILE = "/tmp/test3.nc";
	
	//Load config from file
	@SuppressWarnings("deprecation")
	public static void loadConfig(){
		
		
		try {
			//Open the config file
			DataInputStream dis = new DataInputStream(new FileInputStream("radar.config"));
			
			//Read the contents of the file and load the configurations
			String line = null, s[] = null;
			while( (line = dis.readLine()) != null){
				
				//Ignore comment or blank lines
				if(line.trim().compareTo("") == 0 || line.charAt(0) == '#') continue;
				
				//Split the line
				s = line.split("=");
				
				//Ignore illegally formed configs
				if(s.length < 2) continue;
				
				//Assign the config
				s[0] = s[0].trim();
				if(s[0].compareTo("RADAR_IP") == 0){
					RADAR_IP = s[1];
				}
				else if(s[0].compareTo("RADAR_PORT") == 0){
					RADAR_PORT = Integer.parseInt(s[1]);
				}
				else if(s[0].compareTo("NETCDF_PUBLISH_FILE") == 0){
					NETCDF_PUBLISH_FILE = s[1];
				}
				else{
					System.out.println("Unknown Configuration!");
				}
				
			}
			dis.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}
