package edu.uah.itsc.glmvalidationtool.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DataFilter {
	
	private static double MinLon =-180.0, MaxLon=180.0, MinLat=-90.0, MaxLat=90.0;
	// Time ranges are set up to return CurrentTime - DisplayInterval for currently displayed data
	private static long CurrentTime, DisplayInterval = 1000;
	private static ArrayList<Object> updateObjects = new ArrayList<Object>();
	private static long DataStartTime, DataEndTime;
	
	public DataFilter()
	{
		
	}
	public void registerObject(Object obj) 
	{
		updateObjects.add(obj);
	}
	public void unregisterObject(Object obj) 
	{
		updateObjects.remove(obj);
	}
	public void refreshObjects()
	{
		System.out.println("RefreshObjects");
		for (Object obj:updateObjects) {
			System.out.println("object " + obj.toString());
			((DataFilterUpdate) obj).refresh();
		}
	}
	public void clearCache()
	{
		System.out.println("clearCache");
		for (Object obj:updateObjects) {
			System.out.println("object " + obj.toString());
			((DataFilterUpdate) obj).clearCache();
		}
	}
	// need methods to query database layers for bounding box and set as default
	
	public void setBoundingBox(double minLon, double maxLon, double minLat, double maxLat)
	{
		MinLon = minLon;
		MaxLon = maxLon;
		MinLat = minLat;
		MaxLat = maxLat;
		
	}
	public static double getMinLon() {
		return MinLon;
	}
	public static double getMaxLon() {
		return MaxLon;
	}
	public static double getMinLat() {
		return MinLat;
	}
	public static double getMaxLat() {
		return MaxLat;
	}
	
	// methods for database time range
	public void setDataStartTime(long millisecs)
	{
		DataStartTime = millisecs;
	
	}
	public void setDataEndTime(long millisecs)
	{
		DataEndTime = millisecs;
		
	}
	public void setDataStartTime(int year, int month, int day, int hour, int minute, int second, int milli) 
	{
		DataStartTime = DataUtil.dateToMilliseconds(year, month, day, hour, minute, second, milli);
		
	}
	public void setDataEndTime(int year, int month, int day, int hour, int minute, int second, int milli) 
	{
		DataEndTime = DataUtil.dateToMilliseconds(year, month, day, hour, minute, second, milli);
		
	}
	public long getDataStartTimeMilli()
	{
		return DataStartTime;
	}
	public long getDataEndTimeMilli()
	{
		return DataEndTime;
	}
	public void setCurrentTime(long millisecs)
	{
		CurrentTime = millisecs;
	}
	public void setCurrentTime(int year, int month, int day, int hour, int minute, int second, int milli) 
	{
		CurrentTime = DataUtil.dateToMilliseconds(year, month, day, hour, minute, second, milli);
		
	}
	public long getCurrentTimeMilli()
	{
		return CurrentTime;
	}
	public long getDisplayIntervalMilli()
	{
		return DisplayInterval;
	}
	public void setDisplayInterval(long millisecs)
	{
		DisplayInterval = millisecs;
	}
	public void setDisplayInterval(int hour, int minute, int second) 
	{
		DisplayInterval = 1000 * (second + 60*minute + 3600*hour); // milliseconds
		
	}
	
	
	
	
	
	
	
	
	
	// these are for currently displayed time interval
	public String getCqlString()
	{
		String startDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime - DisplayInterval);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime);
		
		
		String queryString;
		try {
//			queryString = URLEncoder.encode("cql_filter=datetime between '"+ startDate + "' and '"+ endDate + "'", "UTF-8");
//			queryString = queryString + URLEncoder.encode(" AND BBOX(thegeometry, " + MinLon + "," + MaxLon + "," + MinLat + "," + MaxLat + ")","UTF-8" );

//			queryString = "cql_filter=datetime+between+'"+ startDate + "'+and+'"+ endDate + "'";
//			queryString = queryString + "+AND+BBOX(the_geom," + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat + ")";
//			System.out.println("datetime between '"+ startDate + "' and '"+ endDate + "'");
//			System.out.println(" AND BBOX(the_geom," + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat + ")");
			queryString = "cql_filter=" + URLEncoder.encode("datetime between '"+ startDate + "' and '"+ endDate + "'", "UTF-8");
			queryString = queryString + URLEncoder.encode(" AND BBOX(the_geom," + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat + ")", "UTF-8");

			//			queryString = queryString + " AND BBOX(the_geom, " + MinLon + "," + MaxLon + "," + MinLat + "," + MaxLat + ")";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return queryString;
	}
	public String getBoundingBoxString()
	{
		String queryString = "bbox=" + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat ;

		return queryString;
	}
	public String getEnvelopeString()
	{
		String queryString = "minlon=" + MinLon + ",minlat=" + MinLat + ",maxlon=" + MaxLon + ",maxlat=" + MaxLat ;

		return queryString;
	}
	public String getViewParamString()
	{
//		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
//		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);
		String startDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime - DisplayInterval);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime);
		
		
		String queryString;
		try {
//			queryString = URLEncoder.encode("cql_filter=datetime between '"+ startDate + "' and '"+ endDate + "'", "UTF-8");
//			queryString = queryString + URLEncoder.encode(" AND BBOX(thegeometry, " + MinLon + "," + MaxLon + "," + MinLat + "," + MaxLat + ")","UTF-8" );

//			queryString = "cql_filter=datetime+between+'"+ startDate + "'+and+'"+ endDate + "'";
//			queryString = queryString + "+AND+BBOX(the_geom," + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat + ")";
			System.out.println("starttime:'"+ startDate + "';endtime:'"+ endDate + "'");
			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'", "UTF-8");

			//			queryString = queryString + " AND BBOX(the_geom, " + MinLon + "," + MaxLon + "," + MinLat + "," + MaxLat + ")";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return queryString;
	}
	public String getValidationParamString()
	{
//		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
//		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);
		String startDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime - DisplayInterval);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime);
		
		
		String queryString;
		try {
			System.out.println("viewparams=" + "starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ MinLon + ";maxlon:"+ MaxLon + ";minlat:"+ MinLat + ";maxlat:"+ MaxLat);
			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ MinLon + ";maxlon:"+ MaxLon + ";minlat:"+ MinLat + ";maxlat:"+ MaxLat , "UTF-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return queryString;
	}
	
	// BBOX(the_geom, -90, 40, -60, 45)
//	SELECT *
//	FROM mytable
//	WHERE mytable.geom && ST_MakeEnvelope(minLon, minLat, maxLon, maxLat, 4326);
}
