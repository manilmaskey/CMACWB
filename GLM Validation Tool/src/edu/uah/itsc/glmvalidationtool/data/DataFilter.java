package edu.uah.itsc.glmvalidationtool.data;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import edu.uah.itsc.glmvalidationtool.config.Config;

public class DataFilter {
	
//	private static double MinLon =-180.0, MaxLon=180.0, MinLat=-90.0, MaxLat=90.0;
	// Time ranges are set up to return CurrentTime - DisplayInterval for currently displayed data
	private static long CurrentTime;
	
//	 DisplayInterval = 1000;
	private static ArrayList<Object> updateObjects = new ArrayList<Object>();
	private static long DataStartTime, DataEndTime;
	
	// need to add start and end time to this (controlled by widgets in timelineview)
	private static long AnimationStartTime, AnimationEndTime;
	
	private static Config conf=new Config();
	
	public DataFilter()
	{
		
	}
	public Config getConfig()
	{
		return conf;
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
//		System.out.println("RefreshObjects");
		for (Object obj:updateObjects) {
//			System.out.println("object " + obj.toString());
			((DataFilterUpdate) obj).refresh();
		}
	}
	public void clearCache()
	{
//		System.out.println("clearCache");
		for (Object obj:updateObjects) {
//			System.out.println("object " + obj.toString());
			((DataFilterUpdate) obj).clearCache();
		}
	}
	public void reset()
	{
//		System.out.println("reset");
		for (Object obj:updateObjects) {
//			System.out.println("object " + obj.toString());
			((DataFilterUpdate) obj).reset();
		}
	}
	// need methods to query database layers for bounding box and set as default
	
	public void setBoundingBox(double minLon, double maxLon, double minLat, double maxLat)
	{
//		MinLon = minLon;
//		MaxLon = maxLon;
//		MinLat = minLat;
//		MaxLat = maxLat;
		conf.setMinLon(Double.toString(minLon));
		conf.setMaxLon(Double.toString(maxLon));
		conf.setMinLat(Double.toString(minLat));
		conf.setMaxLat(Double.toString(maxLat));
		
	}
	public static double getMinLon() {
//		return MinLon;
		return Double.parseDouble(conf.getMinLon());
	}
	public static double getMaxLon() {
//		return MaxLon;
		return Double.parseDouble(conf.getMaxLon());
	}
	public static double getMinLat() {
//		return MinLat;
		return Double.parseDouble(conf.getMinLat());
	}
	public static double getMaxLat() {
//		return MaxLat;
		return Double.parseDouble(conf.getMaxLat());
	}
	// methods for animation time range
	public void setAnimationStartTime(long millisecs)
	{
		AnimationStartTime = millisecs;
	
	}
	public void setAnimationEndTime(long millisecs)
	{
		AnimationEndTime = millisecs;
		
	}
	public void setAnimationStartTime(int year, int month, int day, int hour, int minute, int second, int milli) 
	{
		AnimationStartTime = DataUtil.dateToMilliseconds(year, month, day, hour, minute, second, milli);
		
	}
	public void setAnimationEndTime(int year, int month, int day, int hour, int minute, int second, int milli) 
	{
		AnimationEndTime = DataUtil.dateToMilliseconds(year, month, day, hour, minute, second, milli);
		
	}
	public long getAnimationStartTimeMilli()
	{
		return AnimationStartTime;
	}
	public long getAnimationEndTimeMilli()
	{
		return AnimationEndTime;
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
//		return DisplayInterval;
		return Long.parseLong(conf.getAnimationDisplayInterval());
	}
	public void setDisplayInterval(long millisecs)
	{
//		DisplayInterval = millisecs;
		conf.setAnimationDisplayInterval(Long.toString(millisecs));
	}
	public void setDisplayInterval(int hour, int minute, int second) 
	{
//		DisplayInterval = 1000 * (second + 60*minute + 3600*hour); // milliseconds
		long DisplayInterval = 1000 * (second + 60*minute + 3600*hour); // milliseconds
		conf.setAnimationDisplayInterval(Long.toString(DisplayInterval));	
	}

	// these are for currently displayed time interval
	public String getCqlString()
	{
		Long DisplayInterval = Long.parseLong(conf.getAnimationDisplayInterval());
		String startDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime - DisplayInterval);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime);
		
		
		String queryString;
		try {
//			System.out.println("datetime between '"+ startDate + "' and '"+ endDate + "'");
//			System.out.println(" AND BBOX(the_geom," + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat + ")");
			queryString = "cql_filter=" + URLEncoder.encode("datetime between '"+ startDate + "' and '"+ endDate + "'", "UTF-8");
//			queryString = queryString + URLEncoder.encode(" AND BBOX(the_geom," + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat + ")", "UTF-8");
			queryString = queryString + URLEncoder.encode(" AND BBOX(the_geom," + conf.getMinLon() + "," + conf.getMinLat() + "," + conf.getMaxLon() + "," + conf.getMaxLat() + ")", "UTF-8");

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
//		String queryString = "bbox=" + MinLon + "," + MinLat + "," + MaxLon + "," + MaxLat ;
		String queryString = "bbox=" + conf.getMinLon() + "," + conf.getMinLat() + "," + conf.getMaxLon() + "," + conf.getMaxLat() ;

		return queryString;
	}
	public String getEnvelopeString()
	{
//		String queryString = "minlon=" + MinLon + ",minlat=" + MinLat + ",maxlon=" + MaxLon + ",maxlat=" + MaxLat ;
		String queryString = "minlon=" + conf.getMinLon() + ",minlat=" + conf.getMinLat() + ",maxlon=" + conf.getMaxLon() + ",maxlat=" + conf.getMaxLat() ;

		return queryString;
	}
	public String getViewParamString()
	{
//		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
//		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);

		Long DisplayInterval = Long.parseLong(conf.getAnimationDisplayInterval());
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
		Long DisplayInterval = Long.parseLong(conf.getAnimationDisplayInterval());
		return getValidationParamString(CurrentTime - DisplayInterval, CurrentTime);
	}
	public String getValidationParamString(long startTime, long endTime)
	{
//		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
//		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);

		
//		String startDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime - DisplayInterval);
//		String endDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime);

		String startDate = DataUtil.millisecondsToSQLTimeStampString(startTime);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(endTime);	
		
		String queryString;
		try {
//			System.out.println("viewparams=" + "starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ MinLon + ";maxlon:"+ MaxLon + ";minlat:"+ MinLat + ";maxlat:"+ MaxLat);
//			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ MinLon + ";maxlon:"+ MaxLon + ";minlat:"+ MinLat + ";maxlat:"+ MaxLat , "UTF-8");

//			System.out.println("viewparams=" + "starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ conf.getMinLon() + ";maxlon:"+ conf.getMaxLon() + ";minlat:"+ conf.getMinLat() + ";maxlat:"+ conf.getMaxLat());
//			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ conf.getMinLon() + ";maxlon:"+ conf.getMaxLon() + ";minlat:"+ conf.getMinLat() + ";maxlat:"+ conf.getMaxLat() , "UTF-8");

			System.out.println("viewparams=" + "starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ conf.getMinLon() + ";maxlon:"+ conf.getMaxLon() + ";minlat:"+ conf.getMinLat() + ";maxlat:"+ conf.getMaxLat() + ";time_interval:'" + conf.getMilliTimeWindow() + " millisecond'" + ";range:" + conf.getDegreeRadius());
			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ conf.getMinLon() + ";maxlon:"+ conf.getMaxLon() + ";minlat:"+ conf.getMinLat() + ";maxlat:"+ conf.getMaxLat()  + ";time_interval:'" + conf.getMilliTimeWindow() + " millisecond'" + ";range:" + conf.getDegreeRadius(), "UTF-8");

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return queryString;
	}
	public String getFrequencyParamString()
	{
		Long DisplayInterval = Long.parseLong(conf.getAnimationDisplayInterval());
		return getFrequencyParamString(CurrentTime - DisplayInterval, CurrentTime);
	}
	public String getFrequencyParamString(long startTime, long endTime)
	{
//		String startDate = DataUtil.millisecondsToSQLTimeStampString(StartMilli);
//		String endDate = DataUtil.millisecondsToSQLTimeStampString(EndMilli);

		
//		String startDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime - DisplayInterval);
//		String endDate = DataUtil.millisecondsToSQLTimeStampString(CurrentTime);

		String startDate = DataUtil.millisecondsToSQLTimeStampString(startTime);
		String endDate = DataUtil.millisecondsToSQLTimeStampString(endTime);	
		
		String queryString;
		try {
//			System.out.println("viewparams=" + "starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ MinLon + ";maxlon:"+ MaxLon + ";minlat:"+ MinLat + ";maxlat:"+ MaxLat);
//			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ MinLon + ";maxlon:"+ MaxLon + ";minlat:"+ MinLat + ";maxlat:"+ MaxLat , "UTF-8");

			System.out.println("viewparams=" + "starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ conf.getMinLon() + ";maxlon:"+ conf.getMaxLon() + ";minlat:"+ conf.getMinLat() + ";maxlat:"+ conf.getMaxLat());
			queryString = "viewparams=" + URLEncoder.encode("starttime:'"+ startDate + "';endtime:'"+ endDate + "'"+ ";minlon:"+ conf.getMinLon() + ";maxlon:"+ conf.getMaxLon() + ";minlat:"+ conf.getMinLat() + ";maxlat:"+ conf.getMaxLat() , "UTF-8");


		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return queryString;
	}
	
}
