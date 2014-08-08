package edu.uah.itsc.glmvalidationtool.data;

import java.util.Calendar;
import java.util.TimeZone;

public class LightningEntry {
	private long milliseconds;
//	private String date, time;
	private float lat,lon,value;
	
	public String getDate() {
//		// convert seconds since 1970 to date/time
//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//		cal.setTimeInMillis(milliseconds);
//		// add 1 for stupid way Calendar does 0 indexed months
//		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-"+ cal.get(Calendar.DAY_OF_MONTH);
		return DataUtil.millisecondsToGmtDateString(milliseconds);
	}

	public String getTime() {
//		// convert seconds since 1970 to date/time
//		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//		cal.setTimeInMillis(milliseconds);
//		return cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"."+cal.get(Calendar.MILLISECOND);
		return DataUtil.millisecondsToGmtTimeString(milliseconds);
	}
	public long getMilliseconds()
	{
		return milliseconds;
	}
//	public String getDate() {
//		return date;
//	}
//
//	public String getTime() {
//		return time;
//	}

	public float getLat() {
		return lat;
	}

	public float getLon() {
		return lon;
	}

	public float getValue() {
		return value;
	}

	public LightningEntry() 
	{
		
	}
	public void parseEntln(String line)
	{
		String [] tokens = line.split("\\s+");  // all whitespace is a delimiter
		String date = tokens[0];
		String time = tokens[1];
//		// set up milliseconds, use full integer seconds and fraction, otherwise parseFloat
//		// converts to exponential notation and looses precision
//		String secondTokens[] = tokens[2].split("\\.");
//		long second = Long.parseLong(secondTokens[0]);
//		milliseconds = second * 1000 + Integer.parseInt(secondTokens[1].substring(0, 3));
		milliseconds = DataUtil.secondsStringToMilliseconds(tokens[2]);
		
		lat = Float.parseFloat(tokens[3]);
		lon = Float.parseFloat(tokens[4]);
//		value = Float.parseFloat(tokens[6])/(float)1000.0; // convert to kA
		value = Float.parseFloat(tokens[6]); // convert to kA
		
	}
	private long parseDateTime(String date, String time)
	{
		// date time format
		// 03/27/11 00:00:07.692
		int year,month,day, hour,minute,second, milisecond;
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		String [] dateTokens = date.split("/");
		// check month
		month = Integer.parseInt(dateTokens[0])-1; // subtract 1 for stupid way Calendar does 0 indexed months
		day = Integer.parseInt(dateTokens[1]);
		year = Integer.parseInt(dateTokens[2]);
		if (year<2000) year+=2000;
		if (year>cal.get(Calendar.YEAR)) year-=100; // if year is greater than current year, asuume 1900
		
		String [] timeTokens = time.split(":");
		hour = Integer.parseInt(timeTokens[0]);
		minute = Integer.parseInt(timeTokens[1]);
		
		String secondTokens[] = timeTokens[2].split("\\.");
		second = Integer.parseInt(secondTokens[0]);
		milisecond = Integer.parseInt(secondTokens[1]);
		
		cal.set(year, month, day, hour, minute, second);
		cal.set(Calendar.MILLISECOND, milisecond);
		return cal.getTimeInMillis();
	}
	public void parseNldn(String line)
	{
		String tokens[] = line.split("\\s+");  // all whitespace is a delimiter
		String date = tokens[0];
		String time = tokens[1];
		milliseconds = parseDateTime(date,time);
		
		lat = Float.parseFloat(tokens[2]);
		lon = Float.parseFloat(tokens[3]);
		value = Float.parseFloat(tokens[4]);
		
	}
	public void parseGld360(String line)
	{
		String tokens[] = line.split("\\s+");  // all whitespace is a delimiter
		String date = tokens[0];
		String time = tokens[1];
		milliseconds = parseDateTime(date,time);
		
		lat = Float.parseFloat(tokens[2]);
		lon = Float.parseFloat(tokens[3]);
		value = Float.parseFloat(tokens[4]);
		
	}
}
