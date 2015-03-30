package edu.uah.itsc.radar.services;

public class ScanSegmentHeader {

	//Variables
	float manualAz;
	float manualEl;
	float startAz;
	float startEl;
	float scanRate;
	String segmentName;
	float rangeMax;
	float heightMax;
	float resolution;
	int followMode;
	int scanMode;
	int scanFlags;
	int volumeNum;
	int segmentNum;
	int timeLimit;
	int saveSegment;
	float leftLimit;
	float rightLimit;
	float upLimit;
	float downLimit;
	float stepSize;
	int maxSegments;
	int clutterFilterBreakSegment;
	int clutterFilter1;
	int clutterFilter2;
	String projectName;
	float currentFixedAngle;
	
	public float getManualAz() {
		return manualAz;
	}
	public void setManualAz(float manualAz) {
		this.manualAz = manualAz;
	}
	public float getManualEl() {
		return manualEl;
	}
	public void setManualEl(float manualEl) {
		this.manualEl = manualEl;
	}
	public float getStartAz() {
		return startAz;
	}
	public void setStartAz(float startAz) {
		this.startAz = startAz;
	}
	public float getStartEl() {
		return startEl;
	}
	public void setStartEl(float startEl) {
		this.startEl = startEl;
	}
	public float getScanRate() {
		return scanRate;
	}
	public void setScanRate(float scanRate) {
		this.scanRate = scanRate;
	}
	public String getSegmentName() {
		return segmentName;
	}
	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}
	public float getRangeMax() {
		return rangeMax;
	}
	public void setRangeMax(float rangeMax) {
		this.rangeMax = rangeMax;
	}
	public float getHeightMax() {
		return heightMax;
	}
	public void setHeightMax(float heightMax) {
		this.heightMax = heightMax;
	}
	public float getResolution() {
		return resolution;
	}
	public void setResolution(float resolution) {
		this.resolution = resolution;
	}
	public int getFollowMode() {
		return followMode;
	}
	public void setFollowMode(int followMode) {
		this.followMode = followMode;
	}
	public int getScanMode() {
		return scanMode;
	}
	public void setScanMode(int scanMode) {
		this.scanMode = scanMode;
	}
	public int getScanFlags() {
		return scanFlags;
	}
	public void setScanFlags(int scanFlags) {
		this.scanFlags = scanFlags;
	}
	public int getVolumeNum() {
		return volumeNum;
	}
	public void setVolumeNum(int volumeNum) {
		this.volumeNum = volumeNum;
	}
	public int getSegmentNum() {
		return segmentNum;
	}
	public void setSegmentNum(int segmentNum) {
		this.segmentNum = segmentNum;
	}
	public int getTimeLimit() {
		return timeLimit;
	}
	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}
	public int getSaveSegment() {
		return saveSegment;
	}
	public void setSaveSegment(int saveSegment) {
		this.saveSegment = saveSegment;
	}
	public float getLeftLimit() {
		return leftLimit;
	}
	public void setLeftLimit(float leftLimit) {
		this.leftLimit = leftLimit;
	}
	public float getRightLimit() {
		return rightLimit;
	}
	public void setRightLimit(float rightLimit) {
		this.rightLimit = rightLimit;
	}
	public float getUpLimit() {
		return upLimit;
	}
	public void setUpLimit(float upLimit) {
		this.upLimit = upLimit;
	}
	public float getDownLimit() {
		return downLimit;
	}
	public void setDownLimit(float downLimit) {
		this.downLimit = downLimit;
	}
	public float getStepSize() {
		return stepSize;
	}
	public void setStepSize(float stepSize) {
		this.stepSize = stepSize;
	}
	public int getMaxSegments() {
		return maxSegments;
	}
	public void setMaxSegments(int maxSegments) {
		this.maxSegments = maxSegments;
	}
	public int getClutterFilterBreakSegment() {
		return clutterFilterBreakSegment;
	}
	public void setClutterFilterBreakSegment(int clutterFilterBreakSegment) {
		this.clutterFilterBreakSegment = clutterFilterBreakSegment;
	}
	public int getClutterFilter1() {
		return clutterFilter1;
	}
	public void setClutterFilter1(int clutterFilter1) {
		this.clutterFilter1 = clutterFilter1;
	}
	public int getClutterFilter2() {
		return clutterFilter2;
	}
	public void setClutterFilter2(int clutterFilter2) {
		this.clutterFilter2 = clutterFilter2;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public float getCurrentFixedAngle() {
		return currentFixedAngle;
	}
	public void setCurrentFixedAngle(float currentFixedAngle) {
		this.currentFixedAngle = currentFixedAngle;
	}
	@Override
	public String toString() {
		return "ScanSegmentHeader [manualAz=" + manualAz + ", manualEl="
				+ manualEl + ", startAz=" + startAz + ", startEl=" + startEl
				+ ", scanRate=" + scanRate //+ ", segmentName=" + segmentName
				+ ", rangeMax=" + rangeMax + ", heightMax=" + heightMax
				+ ", resolution=" + resolution + ", followMode=" + followMode
				+ ", scanMode=" + scanMode + ", scanFlags=" + scanFlags
				+ ", volumeNum=" + volumeNum + ", segmentNum=" + segmentNum
				+ ", timeLimit=" + timeLimit + ", saveSegment=" + saveSegment
				+ ", leftLimit=" + leftLimit + ", rightLimit=" + rightLimit
				+ ", upLimit=" + upLimit + ", downLimit=" + downLimit
				+ ", stepSize=" + stepSize + ", maxSegments=" + maxSegments
				+ ", clutterFilterBreakSegment=" + clutterFilterBreakSegment
				+ ", clutterFilter1=" + clutterFilter1 + ", clutterFilter2="
				+ clutterFilter2 //+ ", projectName=" + projectName
				+ ", currentFixedAngle=" + currentFixedAngle + "]";
	}
	
	
	
}
