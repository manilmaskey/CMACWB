package edu.uah.itsc.glmvalidationtool.config;

import java.awt.Color;

public interface Setters {
//	public void setInitialLongitude(double lon);
	public void setInitialLongitude(String lon);
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

	public void setEntlnMaxFlashRateLayer(String entlnMaxFlashRateLayer);
	public void setNldnMaxFlashRateLayer(String nldnMaxFlashRateLayer);
	public void setGld360MaxFlashRateLayer(String gld360MaxFlashRateLayer);
	public void setGlmMaxFlashRateLayer(String glmMaxFlashRateLayer);

	public void setEntlnDateRangeLayer(String entlnDateRangeayer);
	public void setNldnDateRangeLayer(String nldnDateRangeLayer);
	public void setGld360DateRangeLayer(String gld360DateRangeLayer);
	public void setGlmDateRangeLayer(String glmDateRangeLayer);

	public void setEntlnColor(Color color);
	public void setEntlnColor(int R, int G, int B, int Alpha);
	public void setNldnColor(Color color);
	public void setNldnColor(int R, int G, int B, int Alpha);
	public void setGld360Color(Color color);
	public void setGld360Color(int R, int G, int B, int Alpha);
	public void setGlmColor(Color color);
	public void setGlmColor(int R, int G, int B, int Alpha);

	public void setMilliTimeWindow(String milliTimeWindow);
	public void setDegreeRadius(String degreeRadius);
	public void setAnimationTimePeriod(String animationTimePeriod);
	public void setAnimationDisplayInterval(String animationDisplayInterval);
	public void setMinLat(String minLat);
	public void setMinLon(String minLon);
	public void setMaxLat(String maxLat);
	public void setMaxLon(String maxLon);

	
}
