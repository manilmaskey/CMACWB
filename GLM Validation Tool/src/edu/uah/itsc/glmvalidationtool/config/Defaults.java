package edu.uah.itsc.glmvalidationtool.config;

public class Defaults implements Getters{
	
//	private static String url = "jdbc:postgresql://54.83.58.23/glm_vv"; 
	private static String protocolHttp = "http://"; 
	private static String protocolJdbcPostgresql = "jdbc:postgresql://"; 
	private static String databaseName = "glm_vv";

	private static String ipAddress = "54.83.58.23"; 
//	private static String url = "http://54.83.58.23"; 
	private static String port = "8080"; 
	private static String postgressUname = "postgres";
	private static String postgressPw = "password";
	private static String initialLongitude = "-86.5" ; // Huntsville, AL	public Defaults ()
	private static String entlnFlashTable = "etln_flash_view";
	private static String entlnStrokeTable = "etln_stroke";
	private static String nldnFlashTable = "nldn_flash_view";
	private static String nldnStrokeTable = "nldn_stroke";
	private static String gld360Table = "gld360_view";
	private static String glmFlashTable = "flash_proxy_view";
	private static String glmEventTable = "event_proxy";
	
	private static String nldnFlashRateLayer = "nldn_flash_count";
	private static String glmFlashRateLayer = "glm_count";
	private static String entlnFlashRateLayer = "etln_flash_count";
	private static String gld360FlashRateLayer = "gld360_count";

	// date range geoserver layers
	private static String nldnDateRangeLayer = "nldn_flash_date_range";
	private static String glmDateRangeLayer = "glm_date_range";
	private static String entlnDateRangeLayer = "etln_flash_date_range";
	private static String gld360DateRangeLayer = "gld360_date_range";

	// intersection SQL Geoserver layers
	private static String entlnFlashGlmIntersectionTable = "glm_intersection";
//	private static String entlnFlashGlmIntersectionTable = "glm_flash_entln_intersection";
	private static String nldnFlashGlmIntersectionTable = "glm_flash_nldn_intersection";
	private static String gld360FlashGlmIntersectionTable = "glm_flash_gld360_intersection";
	
	private static String serviceString = "/geoserver/GLM/ows?service=WFS&version=1.0.0&outputFormat=application/json&request=GetFeature&typeName=GLM:";
	private static String serviceStringCsv = "/geoserver/GLM/ows?service=WFS&version=1.0.0&outputFormat=csv&request=GetFeature&typeName=GLM:";
	
	public Defaults()
	{
		
	}
	
	@Override
	public double getInitialLongitude() {
		// TODO Auto-generated method stub
		return Double.parseDouble(initialLongitude);
	}

	@Override
	public String getServerUname() {
		// TODO Auto-generated method stub
		return postgressUname;
	}

	@Override
	public String getServerPw() {
		// TODO Auto-generated method stub
		return postgressPw;
	}
	
	@Override
	public String getEntlnFlashTable() {
		// TODO Auto-generated method stub
		return entlnFlashTable;
	}

	@Override
	public String getEntlnStrokeTable() {
		// TODO Auto-generated method stub
		return entlnStrokeTable;
	}

	@Override
	public String getNldnFlashTable() {
		// TODO Auto-generated method stub
		return nldnFlashTable;
	}

	@Override
	public String getNldnStrokeTable() {
		// TODO Auto-generated method stub
		return nldnStrokeTable;
	}

	@Override
	public String getGld360Table() {
		// TODO Auto-generated method stub
		return gld360Table;
	}

	@Override
	public String getGlmFlashTable() {
		// TODO Auto-generated method stub
		return glmFlashTable;
	}

	@Override
	public String getGlmEventTable() {
		// TODO Auto-generated method stub
		return glmEventTable;
	}

	@Override
	public String getServerPort() {
		// TODO Auto-generated method stub
		return port;
	}

	@Override
	public String getServiceString() {
		// TODO Auto-generated method stub
		return serviceString;	
	}

	@Override
	public String getEntlnFlashGlmIntersectionTable() {
		// TODO Auto-generated method stub
		return entlnFlashGlmIntersectionTable;
	}

	@Override
	public String getServiceStringCsv() {
		// TODO Auto-generated method stub
		return serviceStringCsv;
	}

	@Override
	public String getNldnFlashGlmIntersectionTable() {
		// TODO Auto-generated method stub
		return nldnFlashGlmIntersectionTable;
	}

	@Override
	public String getGld360GlmIntersectionTable() {
		// TODO Auto-generated method stub
		return gld360FlashGlmIntersectionTable;
	}

	@Override
	public String getServerIP() {
		// TODO Auto-generated method stub
		return ipAddress;
	}

	@Override
	public String getProtocolHttp() {
		// TODO Auto-generated method stub
		return protocolHttp;
	}

	@Override
	public String getProtocolJdbcPostgresql() {
		// TODO Auto-generated method stub
		return protocolJdbcPostgresql;
	}

	@Override
	public String getDatabaseName() {
		// TODO Auto-generated method stub
		return databaseName;
	}
	
	@Override
	public String getEntlnFlashRateLayer() {
		// TODO Auto-generated method stub
		return entlnFlashRateLayer;
	}

	@Override
	public String getNldnFlashRateLayer() {
		// TODO Auto-generated method stub
		return nldnFlashRateLayer;
	}

	@Override
	public String getGld360FlashRateLayer() {
		// TODO Auto-generated method stub
		return gld360FlashRateLayer;
	}

	@Override
	public String getGlmFlashRateLayer() {
		// TODO Auto-generated method stub
		return glmFlashRateLayer;
	}

	@Override
	public String getEntlnDateRangeLayer() {
		// TODO Auto-generated method stub
		return entlnDateRangeLayer;
	}

	@Override
	public String getNldnDateRangeLayer() {
		// TODO Auto-generated method stub
		return nldnDateRangeLayer;
	}

	@Override
	public String getGld360DateRangeLayer() {
		// TODO Auto-generated method stub
		return gld360DateRangeLayer;
	}

	@Override
	public String getGlmDateRangeLayer() {
		// TODO Auto-generated method stub
		return glmDateRangeLayer;
	}

}
