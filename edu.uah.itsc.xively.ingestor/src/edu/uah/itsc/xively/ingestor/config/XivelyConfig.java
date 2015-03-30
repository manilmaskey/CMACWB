package edu.uah.itsc.xively.ingestor.config;

public class XivelyConfig {

	//Parameters to access feeds
	//static int FEED_ID = 643150575;
	//static String XIVELY_API_KEY = "0K4mhTWfaO6T7oO9F6oInk8WHpmx57PUs4WPllIA0QTbbpcB";
	
	//DB parameters
	static String dbProtocol = "jdbc:postgresql://";
	static String dbServer = "localhost";
	static int dbPort = 5432;
	static String dbName = "xively";
	static String dbUsername = "postgres";
	static String dbPassword = "postgres";
	
	//static 
	
	//Get methods
//	public static int feedId() { return FEED_ID; }
//	public static String APIKey() { return XIVELY_API_KEY; }
	public static String getDBProtocol() { return dbProtocol; }
	public static String getDBServer() { return dbServer; }
	public static int getDBPort() { return dbPort; }
	public static String getDBName() { return dbName; }
	public static String getDBUsername() { return dbUsername; }
	public static String getDBPassword() { return dbPassword; }
	
	public static void setDBProtocol(String dbProtocol) {
		XivelyConfig.dbProtocol = dbProtocol;
	}
	public static void setDBServer(String dbServer) {
		XivelyConfig.dbServer = dbServer;
	}
	public static void setDBPort(int dbPort) {
		XivelyConfig.dbPort = dbPort;
	}
	public static void setDBName(String dbName) {
		XivelyConfig.dbName = dbName;
	}
	public static void setDBUsername(String dbUsername) {
		XivelyConfig.dbUsername = dbUsername;
	}
	public static void setDBPassword(String dbPassword) {
		XivelyConfig.dbPassword = dbPassword;
	}
	
	
}
