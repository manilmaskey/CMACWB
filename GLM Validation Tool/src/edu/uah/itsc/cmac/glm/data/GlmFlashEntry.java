package edu.uah.itsc.cmac.glm.data;

import java.util.Calendar;
import java.util.TimeZone;

public class GlmFlashEntry implements GlmEntry{
	private long millisecondsStart;
	private long millisecondsEnd;
	private float lat,lon,energy,footprint;
	private int number, childCount;
	
	@Override
	public String getDate() {
		return DataUtil.millisecondsToGmtDateString(millisecondsStart);
	}

	@Override
	public String getTime() {
		return DataUtil.millisecondsToGmtTimeString(millisecondsStart);
	}

	@Override
	public float getLat() {
		return lat;
	}
	@Override
	public float getEnergy() {
		return energy;
	}

	@Override
	public float getLon() {
		return lon;
	}

	public float getFootprint() {
		return footprint;
	}
	public float getNumber() {
		return number;
	}
	public int getChildCount() {
		return childCount;
	}
	public String getStartDate() {
		return DataUtil.millisecondsToGmtDateString(millisecondsStart);
	}
	public String getEndDate() {
		return DataUtil.millisecondsToGmtDateString(millisecondsEnd);
	}
	public String getStartTime() {
		return DataUtil.millisecondsToGmtTimeString(millisecondsStart);
	}
	public String getEndTime() {
		return DataUtil.millisecondsToGmtTimeString(millisecondsEnd);
	}
	public long getMillisecondsStart()
	{
		return millisecondsStart;
	}
	public long getMillisecondsEnd()
	{
		return millisecondsEnd;
	}

	public GlmFlashEntry(String line) 
	{
		parse(line);
	}
	public void parse(String line)
	{
		String tokens[] = line.split("\\s+");  // all whitespace is a delimiter
		
		number = Integer.parseInt(tokens[1]);	
		millisecondsStart = DataUtil.secondsStringToMilliseconds(tokens[2]);
		millisecondsEnd = DataUtil.secondsStringToMilliseconds(tokens[3]);
		
		lat = Float.parseFloat(tokens[4]);
		lon = Float.parseFloat(tokens[5]);
		energy = Float.parseFloat(tokens[6]);
		footprint = Float.parseFloat(tokens[7]);
		childCount = Integer.parseInt(tokens[8]);
	}

}
