package edu.uah.itsc.radar.services;

public class HousekeepingHeader {

	//Variables
	String radarID;
	int radarLatitude;
	int radarLongitude;
	int radarAltitude;
	int antennaMode;
	int nyquistVel;
	int gateWidth;
	int pulses;
	int polarizationMode;
	int sweepNumber;
	int saveSweep;
	int angleScale;
	int sweepStartTime;
	public String getRadarID() {
		return radarID;
	}
	public void setRadarID(String radarID) {
		this.radarID = radarID;
	}
	public int getRadarLatitude() {
		return radarLatitude;
	}
	public void setRadarLatitude(int radarLatitude) {
		this.radarLatitude = radarLatitude;
	}
	public int getRadarLongitude() {
		return radarLongitude;
	}
	public void setRadarLongitude(int radarLongitude) {
		this.radarLongitude = radarLongitude;
	}
	public int getRadarAltitude() {
		return radarAltitude;
	}
	public void setRadarAltitude(int radarAltitude) {
		this.radarAltitude = radarAltitude;
	}
	public int getAntennaMode() {
		return antennaMode;
	}
	public void setAntennaMode(int antennaMode) {
		this.antennaMode = antennaMode;
	}
	public int getNyquistVel() {
		return nyquistVel;
	}
	public void setNyquistVel(int nyquistVel) {
		this.nyquistVel = nyquistVel;
	}
	public int getGateWidth() {
		return gateWidth;
	}
	public void setGateWidth(int gateWidth) {
		this.gateWidth = gateWidth;
	}
	public int getPulses() {
		return pulses;
	}
	public void setPulses(int pulses) {
		this.pulses = pulses;
	}
	public int getPolarizationMode() {
		return polarizationMode;
	}
	public void setPolarizationMode(int polarizationMode) {
		this.polarizationMode = polarizationMode;
	}
	public int getSweepNumber() {
		return sweepNumber;
	}
	public void setSweepNumber(int sweepNumber) {
		this.sweepNumber = sweepNumber;
	}
	public int getSaveSweep() {
		return saveSweep;
	}
	public void setSaveSweep(int saveSweep) {
		this.saveSweep = saveSweep;
	}
	public int getAngleScale() {
		return angleScale;
	}
	public void setAngleScale(int angleScale) {
		this.angleScale = angleScale;
	}
	public int getSweepStartTime() {
		return sweepStartTime;
	}
	public void setSweepStartTime(int sweepStartTime) {
		this.sweepStartTime = sweepStartTime;
	}
	@Override
	public String toString() {
		return "HousekeepingHeader [radarID=" + radarID + ", radarLatitude="
				+ radarLatitude + ", radarLongitude=" + radarLongitude
				+ ", radarAltitude=" + radarAltitude + ", antennaMode="
				+ antennaMode + ", nyquistVel=" + nyquistVel + ", gateWidth="
				+ gateWidth + ", pulses=" + pulses + ", polarizationMode="
				+ polarizationMode + ", sweepNumber=" + sweepNumber
				+ ", saveSweep=" + saveSweep + ", angleScale=" + angleScale
				+ ", sweepStartTime=" + sweepStartTime + "]";
	}
}
