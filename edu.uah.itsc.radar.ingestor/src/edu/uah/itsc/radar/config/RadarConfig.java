package edu.uah.itsc.radar.config;

public class RadarConfig {

	//Radius of Earth
	public static final long RADIUS_OF_EARTH = 6371000 * 1000; // Mili meters
	//Server configurations
	public static final String RADAR_IP = "vchill.chill.colostate.edu";
	public static final int RADAR_PORT = 2511;
	
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
}
