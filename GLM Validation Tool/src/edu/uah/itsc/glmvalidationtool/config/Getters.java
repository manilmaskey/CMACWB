package edu.uah.itsc.glmvalidationtool.config;

public interface Getters {
	public double getInitialLongitude();
	public String getServiceString();
	public String getServiceStringCsv();
	public String getDatabaseName();
	public String getServerIP();
	public String getProtocolHttp();
	public String getProtocolJdbcPostgresql();
	public String getServerPort();
	public String getServerUname();
	public String getServerPw();
	public String getEntlnFlashTable();
	public String getEntlnStrokeTable();
	public String getNldnFlashTable();
	public String getNldnStrokeTable();
	public String getGld360Table();
	public String getGlmFlashTable();
	public String getGlmEventTable();
	public String getEntlnFlashGlmIntersectionTable();
	public String getNldnFlashGlmIntersectionTable();
	public String getGld360GlmIntersectionTable();
	
	public String getEntlnFlashRateLayer();
	public String getNldnFlashRateLayer();
	public String getGld360FlashRateLayer();
	public String getGlmFlashRateLayer();

}
