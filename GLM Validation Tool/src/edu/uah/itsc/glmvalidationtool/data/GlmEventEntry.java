package edu.uah.itsc.glmvalidationtool.data;

import java.util.Calendar;
import java.util.TimeZone;

public class GlmEventEntry implements GlmEntry{
	private long milliseconds;
	private float lat,lon,energy;
	private int xpixel, ypixel;
	private int number, groupCount, childNumber;
	
	@Override
	public String getDate() {
		return DataUtil.millisecondsToGmtDateString(milliseconds);
	}

	@Override
	public String getTime() {
		return DataUtil.millisecondsToGmtTimeString(milliseconds);
	}

	@Override
	public float getLat() {
		return lat;
	}

	@Override
	public float getLon() {
		return lon;
	}

	@Override
	public float getEnergy() {
		return energy;
	}
	public long getMilliseconds()
	{
		return milliseconds;
	}
	public float getXpixel() {
		return xpixel;
	}
	public float getYpixel() {
		return ypixel;
	}
	public float getNumber() {
		return number;
	}
	public float getGroupCount() {
		return groupCount;
	}
	public float getChildNumber() {
		return childNumber;
	}

	public GlmEventEntry(String line) 
	{
		parse(line);
	}
	public void parse(String line)
	{
		String tokens[] = line.split("\\s+");  // all whitespace is a delimiter
		
		number = Integer.parseInt(tokens[0]);
		groupCount = Integer.parseInt(tokens[1]);
		
		milliseconds = DataUtil.secondsStringToMilliseconds(tokens[2]);
		
		xpixel = Integer.parseInt(tokens[3]);
		ypixel = Integer.parseInt(tokens[4]);
		
		lat = Float.parseFloat(tokens[5]);
		lon = Float.parseFloat(tokens[6]);
		energy = Float.parseFloat(tokens[7]);
		childNumber = Integer.parseInt(tokens[8]);
	}
}
