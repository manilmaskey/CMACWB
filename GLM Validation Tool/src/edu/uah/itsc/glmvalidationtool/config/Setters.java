package edu.uah.itsc.glmvalidationtool.config;

public interface Setters {
	public void setInitialLongitude(double lon);
	public void setServiceString(String serviceString);
	public void setServiceStringCsv(String serviceString);
	public void setDatabaseName(String databaseName);
	public void setServerIP(String serverIPString);
	public void setProtocolHttp(String protocolHttpString);
	public void setProtocolJdbcPostgresql(String protocolJdbcPostgresqlString);	
	public void setServerPort(String url);
	public void setServerUname(String uname);
	public void setServerPw(String pw);
	
	public void setEntlnFlashTable(String entlnFlashTable);
	public void setEntlnStrokeTable(String entlnStrokeTable);
	public void setNldnFlashTable(String nldnFlashTable);
	public void setNldnStrokeTable(String nldnStrokeTable);
	public void setGld360Table(String gld360Table);
	public void setGlmFlashTable(String glmFlashTable);
	public void setGlmEventTable(String glmEventTable);

	public void setEntlnFlashLayer(String entlnFlashLayer);
	public void setNldnFlashLayer(String nldnFlashLayer);
	public void setGld360Layer(String gld360Layer);
	public void setGlmFlashLayer(String glmFlashLayer);

	public void setGlmIntersectionLayer(String glmIntersectionLayer);
	public void setGroundIntersectionLayer(String groundIntersectionLayer);
//	public void setEntlnFlashGlmIntersectionLayer(String intersectionLayer);
//	public void setNldnFlashGlmIntersectionLayer(String intersectionLayer);
//	public void setGld360GlmIntersectionLayer(String intersectionLayer);

	public void setEntlnFlashRateLayer(String entlnFlashRateLayer);
	public void setNldnFlashRateLayer(String nldnFlashRateLayer);
	public void setGld360FlashRateLayer(String gld360FlashRateLayer);
	public void setGlmFlashRateLayer(String glmFlashRateLayer);
	
	public void setEntlnDateRangeLayer(String entlnDateRangeayer);
	public void setNldnDateRangeLayer(String nldnDateRangeLayer);
	public void setGld360DateRangeLayer(String gld360DateRangeLayer);
	public void setGlmDateRangeLayer(String glmDateRangeLayer);

}
