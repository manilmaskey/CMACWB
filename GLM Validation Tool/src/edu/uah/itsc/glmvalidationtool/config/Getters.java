package edu.uah.itsc.glmvalidationtool.config;

import java.awt.Color;

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
	
	public String getEntlnFlashLayer();
	public String getNldnFlashLayer();
	public String getGld360Layer();
	public String getGlmFlashLayer();
	
	public String getGlmIntersectionLayer();
	public String getGroundIntersectionLayer();
//	public String getEntlnFlashGlmIntersectionLayer();
//	public String getNldnFlashGlmIntersectionLayer();
//	public String getGld360GlmIntersectionLayer();
	
	public String getEntlnFlashRateLayer();
	public String getNldnFlashRateLayer();
	public String getGld360FlashRateLayer();
	public String getGlmFlashRateLayer();

	public String getEntlnDateRangeLayer();
	public String getNldnDateRangeLayer();
	public String getGld360DateRangeLayer();
	public String getGlmDateRangeLayer();

	public String getEntlnColorString();
	public String getNldnColorString();
	public String getGld360ColorString();
	public String getGlmColorString();
	
	public Color getEntlnColor();
	public Color getNldnColor();
	public Color getGld360Color();
	public Color getGlmColor();

}
