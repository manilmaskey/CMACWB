package edu.uah.itsc.glmvalidationtool.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class DataFilter {
	
	private static double MinLon =-180.0, MaxLon=180.0, MinLat=-90.0, MaxLat=90.0;
	private static long StartMilli, EndMilli, IncrementMilli;
	private static ArrayList<Object> updateObjects = new ArrayList<>();
	
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
	
	public void setStartTime(long millisecs)
	{
		StartMilli = millisecs;
	
	}
	public void setEndTime(long millisecs)
	{
		EndMilli = millisecs;
		
	}
	public void setTimeIncrement(long millisecs)
	{
		IncrementMilli = millisecs;
		
	}
	public void setStartTime(int year, int month, int day, int hour, int minute, int second, int milli) 
	{
		StartMilli = DataUtil.dateToMilliseconds(year, month, day, hour, minute, second, milli);
		
	}
	public void setEndTime(int year, int month, int day, int hour, int minute, int second, int milli) 
	{
		EndMilli = DataUtil.dateToMilliseconds(year, month, day, hour, minute, second, milli);
		
	}
	public long getStartTimeMilli()
	{
		return StartMilli;
	}
	public long getEndTimeMilli()
	{
		return EndMilli;
	}
	public long getIncrementMilli()
	{
		return IncrementMilli;
	}
	public String getCqlString()
	{
		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);
		
		
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
		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);
		
		
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
		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);
		
		
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
