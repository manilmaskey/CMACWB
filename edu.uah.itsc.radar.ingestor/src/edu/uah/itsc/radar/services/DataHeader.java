package edu.uah.itsc.radar.services;


public class DataHeader {

	long requestedFields;
	long availableFields;
	int startAz;
	int startEl;
	int endAz;
	int endEl;
	int numGates;
	int startRange;
	int  dataTimeSecs;
	int dataTimeNSecs;
	int rayNumber;
	
	//Constructor
	public DataHeader(){
	}

	//Getters and Setters
	public long getRequestedFields() {
		return requestedFields;
	}

	public void setRequestedFields(long requestedFields) {
		this.requestedFields = requestedFields;
	}

	public long getAvailableFields() {
		return availableFields;
	}

	public void setAvailableFields(long availableFields) {
		this.availableFields = availableFields;
	}

	public int getStartAz() {
		return startAz;
	}

	public void setStartAz(int startAz) {
		this.startAz = startAz;
	}

	public int getStartEl() {
		return startEl;
	}

	public void setStartEl(int startEl) {
		this.startEl = startEl;
	}

	public int getEndAz() {
		return endAz;
	}

	public void setEndAz(int endAz) {
		this.endAz = endAz;
	}

	public int getEndEl() {
		return endEl;
	}

	public void setEndEl(int endEl) {
		this.endEl = endEl;
	}

	public int getNumGates() {
		return numGates;
	}

	public void setNumGates(int numGates) {
		this.numGates = numGates;
	}

	public int getStartRange() {
		return startRange;
	}

	public void setStartRange(int startRange) {
		this.startRange = startRange;
	}

	public int getDataTimeSecs() {
		return dataTimeSecs;
	}

	public void setDataTimeSecs(int dataTimeSecs) {
		this.dataTimeSecs = dataTimeSecs;
	}

	public int getDataTimeNSecs() {
		return dataTimeNSecs;
	}

	public void setDataTimeNSecs(int dataTimeNSecs) {
		this.dataTimeNSecs = dataTimeNSecs;
	}

	public int getRayNumber() {
		return rayNumber;
	}

	public void setRayNumber(int rayNumber) {
		this.rayNumber = rayNumber;
	}

	@Override
	public String toString() {
		return "DataHeader [requestedFields=" + Long.toHexString(requestedFields)
				+ ", availableFields=" + Long.toHexString(availableFields) + ", startAz="
				+ startAz + ", startEl=" + startEl + ", endAz=" + endAz
				+ ", endEl=" + endEl + ", numGates=" + numGates
				+ ", startRange=" + startRange + ", dataTimeSecs="
				+ dataTimeSecs + ", dataTimeNSecs=" + dataTimeNSecs
				+ ", rayNumber=" + rayNumber + "]";
	}
	
}
