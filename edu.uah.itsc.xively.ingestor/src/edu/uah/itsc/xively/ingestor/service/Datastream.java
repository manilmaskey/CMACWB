package edu.uah.itsc.xively.ingestor.service;

public class Datastream {
	
	//Variables
	private String streamId;
	private String updated;
	private String unitSymbol;
	private String unitLabel;
	private String maxValue;
	private String minValue;
	private String currentValue;

//
	public String getStreamId() {
		return streamId;
	}
	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}
	public String getUpdated() {
		return updated;
	}
	public void setUpdated(String updated) {
		this.updated = updated;
	}
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public String getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}
	public String getUnitSymbol() {
		return unitSymbol;
	}
	public void setUnitSymbol(String unitSymbol) {
		this.unitSymbol = unitSymbol;
	}
	public String getUnitLabel() {
		return unitLabel;
	}
	public void setUnitLabel(String unitLabel) {
		this.unitLabel = unitLabel;
	}

}
