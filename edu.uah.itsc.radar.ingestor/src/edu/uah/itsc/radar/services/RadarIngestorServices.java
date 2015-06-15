package edu.uah.itsc.radar.services;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;


import org.apache.commons.io.FileUtils;

import edu.uah.itsc.radar.config.RadarConfig;

public class RadarIngestorServices {

	//Record the data from Data header for the PPI Scan
	public static void recordData_PPI(BufferedWriter dataFile, DataInputStream dis, DataHeader dh, HousekeepingHeader hk, FieldTypeInfoHeader fti) throws IOException{

		//Variables used while processing data during PPI scan
		float startAngle, endAngle, startRange, endRange;
		float temp;
		//Determine parameters
		startRange = 0; 
		endRange = dh.getStartRange();
		startAngle = (dh.getStartAz() * 360.0f) / hk.getAngleScale();
		endAngle = (dh.getEndAz() * 360.0f) / hk.getAngleScale();

		//Generate temporary CSV file for post processing to NetCDF file 
		for(int i = 0; i < dh.getNumGates(); ++i){

			//Compute parameters
			startRange = endRange;
			endRange += hk.getGateWidth();

			//Write to file (startAngle, endAngle, startRange, endRange, value)
			temp = fti.getValue(dis.readByte());
			if(temp > -10.0f){
				dataFile.write(startAngle+","+endAngle+","+startRange+","+endRange+","+temp);
				dataFile.newLine();
				dataFile.flush();	
			}
		}
	}

	public static void publishNetCDF(String fileName){
		try {
			FileUtils.copyFile(new File(fileName), new File(RadarConfig.NETCDF_PUBLISH_FILE));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ScanSegmentHeader parseScanSegmentHeader(DataInputStream dis){
		ScanSegmentHeader sgh = new ScanSegmentHeader();
		byte buffer[] = new byte[32];	

		try {
			sgh.setManualAz(dis.readFloat());
			sgh.setManualEl(dis.readFloat());
			sgh.setStartAz(dis.readFloat());
			sgh.setStartEl(dis.readFloat());
			sgh.setScanRate(dis.readFloat());

			dis.read(buffer, 0, 16);
			sgh.setSegmentName(new String(buffer,0,16,"UTF-8").trim());

			sgh.setRangeMax(dis.readFloat());
			sgh.setHeightMax(dis.readFloat());
			sgh.setResolution(dis.readFloat());
			sgh.setFollowMode(dis.readInt());
			sgh.setScanMode(dis.readInt());
			sgh.setScanFlags(dis.readInt());
			sgh.setVolumeNum(dis.readInt());
			sgh.setSegmentNum(dis.readInt());
			sgh.setTimeLimit(dis.readInt());
			sgh.setSaveSegment(dis.readInt());
			sgh.setLeftLimit(dis.readFloat());
			sgh.setRightLimit(dis.readFloat());
			sgh.setUpLimit(dis.readFloat());
			sgh.setDownLimit(dis.readFloat());
			sgh.setStepSize(dis.readFloat());
			sgh.setMaxSegments(dis.readInt());
			sgh.setClutterFilterBreakSegment(dis.readInt());
			sgh.setClutterFilter1(dis.readInt());
			sgh.setClutterFilter2(dis.readInt());

			dis.read(buffer,0,16);
			sgh.setProjectName(new String(buffer,0,16,"UTF-8").trim());

			sgh.setCurrentFixedAngle(dis.readFloat());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		return sgh;
	}
	public static HousekeepingHeader parseHousekeepingHeader(DataInputStream dis){
		HousekeepingHeader hk = new HousekeepingHeader();
		byte buffer[] = new byte[127];

		try {
			dis.read(buffer, 0, 32);
			hk.setRadarID(new String(buffer,0, 32,"UTF-8").trim());

			hk.setRadarLatitude(dis.readInt());
			hk.setRadarLongitude(dis.readInt());
			hk.setRadarAltitude(dis.readInt());
			hk.setAntennaMode(dis.readInt());
			hk.setNyquistVel(dis.readInt());
			hk.setGateWidth(dis.readInt());
			hk.setPulses(dis.readInt());
			hk.setPolarizationMode(dis.readInt());
			hk.setSweepNumber(dis.readInt());
			hk.setSaveSweep(dis.readInt());
			hk.setAngleScale(dis.readInt());
			hk.setSweepStartTime(dis.readInt());


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hk;
	}
	public static DataHeader parseDataHeader(DataInputStream is) throws IOException{
		DataHeader dh = new DataHeader();

		dh.setRequestedFields(is.readLong());
		dh.setAvailableFields(is.readLong());
		dh.setStartAz(is.readInt());
		dh.setStartEl(is.readInt());
		dh.setEndAz(is.readInt());
		dh.setEndEl(is.readInt());
		dh.setNumGates(is.readInt());
		dh.setStartRange(is.readInt());
		dh.setDataTimeSecs(is.readInt());
		dh.setDataTimeNSecs(is.readInt());
		dh.setRayNumber(is.readInt());

		return dh;
	}
	public static FieldTypeInfoHeader parseFieldTypeInfoHeader(DataInputStream dis){

		FieldTypeInfoHeader fit = new FieldTypeInfoHeader();
		byte buffer[] = new byte[256];

		try {

			dis.read(buffer, 0, 32);
			fit.setFieldName(new String(buffer,0,32, "UTF-8").trim());

			dis.read(buffer, 0, 128);
			fit.setFieldDescription(new String(buffer,0,128, "UTF-8").trim());

			fit.setKeyboardAccelerator(dis.readInt());

			dis.read(buffer, 0, 32);
			fit.setUnits(new String(buffer,0,32, "UTF-8").trim());

			fit.setFieldNumber(dis.readInt());
			fit.setFactor(dis.readInt());
			fit.setScale(dis.readInt());
			fit.setBias(dis.readInt());
			fit.setMaxFactorScaledValue(dis.readInt());
			fit.setMinFactorScaledValue(dis.readInt());
			fit.setFieldDataFlags(dis.readShort());
			fit.setColorMapType(dis.readShort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return fit;
	}

	public static String getHeaderName(int headerType){

		switch(headerType){
		case RadarConfig.HEADER_DATA:
			return "DATA";
		case RadarConfig.HEADER_EXTENDED_TRACKING:
			return "EXTENTED_TRACKING";
		case RadarConfig.HEADER_FIELD_INFO_TYPE:
			return "FIELD_INFO_TYPE";
		case RadarConfig.HEADER_HOUSEKEEPING:
			return "HOUSEKEEPING";
		case RadarConfig.HEADER_POWER_METERS_UPDATE:
			return "POWER_METERS_UPDATE";
		case RadarConfig.HEADER_PROCESSOR_INFO:
			return "PROCESSOR_INFO";
		case RadarConfig.HEADER_RADAR_INFO:
			return "RADAR_INFO";
		case RadarConfig.HEADER_SCAN_SEGMENT:
			return "SCAN_SEGMENT";
		case RadarConfig.HEADER_SWEEP_NOTICE:
			return "SWEEP_NOTICE";
		case RadarConfig.HEADER_TRACKING:
			return "TRACKING";
		case RadarConfig.HEADER_TRANSMITTER_INFO:
			return "TRANSMITTER_INFO";
		}
		return "UNKNOWN";
	}
}
