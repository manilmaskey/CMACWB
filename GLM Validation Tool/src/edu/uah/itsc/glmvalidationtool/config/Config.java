package edu.uah.itsc.glmvalidationtool.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Config implements Getters,Setters{
	private static Properties keyValuePair = new Properties();
	private static Defaults defaults = new Defaults();
    public static enum DataType {ENTLN_FLASH, ENTLN_STROKE, NLDN_FLASH, NLDN_STROKE, GLD360, GLM_EVENT, GLM_FLASH, OTHER};

	public Config()
	{
	}

	@Override
	public void setInitialLongitude(double lon) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("InitialLongitude", Double.toString(lon));
	}

	@Override
	public void setServerUname(String uname) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ServerUname", uname);		
	}

	@Override
	public void setServerPw(String pw) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ServerPw", pw);
	}

	@Override
	public void setEntlnFlashTable(String entlnFlashTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("EntlnFlashTable", entlnFlashTable);
	}

	@Override
	public void setEntlnStrokeTable(String entlnStrokeTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("EntlnStrokeTable", entlnStrokeTable);
		
	}

	@Override
	public void setNldnFlashTable(String nldnFlashTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("NldnFlashTable", nldnFlashTable);
		
	}

	@Override
	public void setNldnStrokeTable(String nldnStrokeTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("NldnStrokeTable", nldnStrokeTable);
		
	}

	@Override
	public void setGld360Table(String gld360Table) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("Gld360FlashTable", gld360Table);
		
	}

	@Override
	public void setGlmFlashTable(String glmFlashTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("GlmFlashTable", glmFlashTable);
		
	}

	@Override
	public void setGlmEventTable(String glmEventTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("GlmEventTable", glmEventTable);
		
	}


	@Override
	public double getInitialLongitude() {
		// TODO Auto-generated method stub
		return Double.parseDouble(keyValuePair.getProperty("InitialLongitude", Double.toString(defaults.getInitialLongitude())));
	}

	@Override
	public String getServerUname() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ServerUname",defaults.getServerUname());
	}

	@Override
	public String getServerPw() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ServerPw",defaults.getServerPw());
	}
	@Override
	public String getEntlnFlashTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("EntlnFlashTable", defaults.getEntlnFlashTable());
	}

	@Override
	public String getEntlnStrokeTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("EntlnStrokeTable", defaults.getEntlnStrokeTable());
	}

	@Override
	public String getNldnFlashTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("NldnFlashTable", defaults.getNldnFlashTable());
	}

	@Override
	public String getNldnStrokeTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("NldnStrokeTable", defaults.getNldnStrokeTable());
	}

	@Override
	public String getGld360Table() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("Gld360FlashTable", defaults.getGld360Table());
	}

	@Override
	public String getGlmFlashTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("GlmFlashTable", defaults.getGlmFlashTable());
	}

	@Override
	public String getGlmEventTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("GlmEventTable", defaults.getGlmEventTable());
	}

	@Override
	public void setServerPort(String port) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ServerPort", port);
	}

	@Override
	public String getServerPort() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ServerPort", defaults.getServerPort());
	}

	@Override
	public void setServiceString(String serviceString) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ServiceString", serviceString);
	}

	@Override
	public String getServiceString() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ServiceString", defaults.getServiceString());
	}

	@Override
	public void setIntersectionTable(
			String intersectionTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("IntersectionTable", intersectionTable);
	}

	@Override
	public String getIntersectionTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("IntersectionTable", defaults.getIntersectionTable());
	}

	@Override
	public void setEntlnFlashGlmIntersectionTable(
			String intersectionTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("EntlnFlashGlmIntersectionTable", intersectionTable);
	}

	@Override
	public String getEntlnFlashGlmIntersectionTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("EntlnFlashGlmIntersectionTable", defaults.getEntlnFlashGlmIntersectionTable());
	}
	@Override
	public void setNldnFlashGlmIntersectionTable(
			String intersectionTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("NldnFlashGlmIntersectionTable", intersectionTable);
	}

	@Override
	public String getNldnFlashGlmIntersectionTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("NldnFlashGlmIntersectionTable", defaults.getNldnFlashGlmIntersectionTable());
	}

	@Override
	public void setGld360GlmIntersectionTable(
			String intersectionTable) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("Gld360GlmIntersectionTable", intersectionTable);
	}

	@Override
	public String getGld360GlmIntersectionTable() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("Gld360GlmIntersectionTable", defaults.getGld360GlmIntersectionTable());
	}

	@Override
	public void setServiceStringCsv(String serviceString) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ServiceStringCsv", serviceString);
	}

	@Override
	public String getServiceStringCsv() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ServiceStringCsv", defaults.getServiceStringCsv());
	}

	@Override
	public void setServerIP(String serverIPString) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ServerIP", serverIPString);
	}
	@Override
	public String getServerIP() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ServerIP", defaults.getServerIP());
	}

	@Override
	public void setProtocolHttp(String protocolHttpString) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ProtocolHttp", protocolHttpString);
		
	}
	@Override
	public String getProtocolHttp() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ProtocolHttp", defaults.getProtocolHttp());
	}

	@Override
	public void setProtocolJdbcPostgresql(String protocolJdbcPostgresqlString) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("ProtocolJdbcPostgresql", protocolJdbcPostgresqlString);
	}

	@Override
	public String getProtocolJdbcPostgresql() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("ProtocolJdbcPostgresql", defaults.getProtocolJdbcPostgresql());
	}

	@Override
	public void setDatabaseName(String databaseName) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("DatabaseName", databaseName);
		
	}

	@Override
	public String getDatabaseName() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("DatabaseName", defaults.getDatabaseName());
	}

	@Override
	public void setEntlnFlashRateLayer(String entlnFlashRateLayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("EntlnFlashRateLayer", entlnFlashRateLayer);
		
	}

	@Override
	public void setNldnFlashRateLayer(String nldnFlashRateLayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("NldnFlashRateLayer", nldnFlashRateLayer);
		
	}

	@Override
	public void setGld360FlashRateLayer(String gld360FlashRateLayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("Gld360FlashRateLayer", gld360FlashRateLayer);
		
	}

	@Override
	public void setGlmFlashRateLayer(String glmFlashRateLayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("GlmFlashRateLayer", glmFlashRateLayer);
		
	}

	@Override
	public String getEntlnFlashRateLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("EntlnFlashRateLayer", defaults.getEntlnFlashRateLayer());
	}

	@Override
	public String getNldnFlashRateLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("NldnFlashRateLayer", defaults.getNldnFlashRateLayer());
	}

	@Override
	public String getGld360FlashRateLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("Gld360FlashRateLayer", defaults.getGld360FlashRateLayer());
	}

	@Override
	public String getGlmFlashRateLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("GlmFlashRateLayer", defaults.getGlmFlashRateLayer());
	}

	@Override
	public void setEntlnDateRangeLayer(String entlnDateRangeayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("EntlnDateRangeLayer", entlnDateRangeayer);
	}

	@Override
	public void setNldnDateRangeLayer(String nldnDateRangeLayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("NldnDateRangeLayer", nldnDateRangeLayer);		
	}

	@Override
	public void setGld360DateRangeLayer(String gld360DateRangeLayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("Gld360DateRangeLayer", gld360DateRangeLayer);
		
	}

	@Override
	public void setGlmDateRangeLayer(String glmDateRangeLayer) {
		// TODO Auto-generated method stub
		keyValuePair.setProperty("GlmDateRangeLayer", glmDateRangeLayer);		
		
	}

	@Override
	public String getEntlnDateRangeLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("EntlnDateRangeLayer", defaults.getEntlnDateRangeLayer());
	}

	@Override
	public String getNldnDateRangeLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("NldnDateRangeLayer", defaults.getNldnDateRangeLayer());
	}

	@Override
	public String getGld360DateRangeLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("Gld360DateRangeLayer", defaults.getGld360DateRangeLayer());
	}

	@Override
	public String getGlmDateRangeLayer() {
		// TODO Auto-generated method stub
		return keyValuePair.getProperty("GlmDateRangeLayer", defaults.getGlmDateRangeLayer());
	}
}
