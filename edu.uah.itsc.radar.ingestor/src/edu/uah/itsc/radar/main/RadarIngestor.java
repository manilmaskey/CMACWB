package edu.uah.itsc.radar.main;


import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import edu.uah.itsc.radar.config.RadarConfig;
import edu.uah.itsc.radar.services.DataHeader;
import edu.uah.itsc.radar.services.FieldTypeInfoHeader;
import edu.uah.itsc.radar.services.HousekeepingHeader;
import edu.uah.itsc.radar.services.RadarIngestorServices;
import edu.uah.itsc.radar.services.ScanSegmentHeader;

public class RadarIngestor {

	/**
	 * @param args
	 */

	//Server
	private static Socket server = null;

	//Field type info headers
	private static Vector<FieldTypeInfoHeader> fieldHeaders = new Vector<FieldTypeInfoHeader>();

	//Input and output stream to server
	private static DataOutputStream dos = null;
	private static DataInputStream dis = null;
	private static int headerType, headerLength;
	private static String headerName = null;

	//Log File 
	private static BufferedWriter logFile = null;
	private static BufferedWriter dataFile = null;

	public static void main(String[] args) {

		//Initialize log file
//		try {
//			logFile = new BufferedWriter(new FileWriter("data.csv"));
//		} catch (IOException e) {
//			System.out.println("Log Error: " + e.getMessage());
//			System.exit(1);
//		}

		try {
			//Connect to radar server
			Log("Connecting to: " + RadarConfig.RADAR_IP + " at port " + RadarConfig.RADAR_PORT);
			server = new Socket(RadarConfig.RADAR_IP,RadarConfig.RADAR_PORT);
			Log("Connected to: " + server.getRemoteSocketAddress());

			//Get input and output streams
			dos = new DataOutputStream(server.getOutputStream()); // To send data to server
			dis = new DataInputStream(server.getInputStream()); // To get data from server

			//Send HELLO to server
			Log("Saying HELLO to server");
			dos.writeInt(RadarConfig.HELLO);

			//Send request to DATA CHANNEL
			Log("Requesting DATA CHANNEL");
			dos.writeInt(RadarConfig.DATA_CHANNEL);

			//Wait for buffer to be ready [NEED A BETTER WAY TO DO THIS!]
			for(long i =0; i<999999999; ++i);

			//Read the field_type_info header sent by server
			while(dis.available() > 0){
				
				//Read header type and header length
				headerType = dis.readInt();
				headerLength = dis.readInt();

				//Parse the Field_Type_Info Header
				fieldHeaders.add(RadarIngestorServices.parseFieldTypeInfoHeader(dis));

			}
			Log("Received " + fieldHeaders.size() + " field info type header(s)");

			//Display the field info received
			for(int i=0; i<fieldHeaders.size(); ++i)
				Log(fieldHeaders.get(i).toString());


			//Send request to receive all feeds
			Log("Send request for all feeds");
			dos.writeLong(RadarConfig.FEED_REFLECTIVITY);

			//Wait for buffer to be ready [NEED A BETTER WAY TO DO THIS!]
			for(long i =0; i<999999999; ++i);


			//Various packets that can be received during real time reading of data from server
			DataHeader dh = null;
			HousekeepingHeader hk = null;
			ScanSegmentHeader sgh = null;
			
			//Variables used while processing data during PPI scan
			float _lat, _lon, _angle, _range;

			//Main loop
			while(true){

				//Read header type and header length info
				headerType = dis.readInt();
				headerLength = dis.readInt();

				//Determine type of header
				headerName = RadarIngestorServices.getHeaderName(headerType);
				if(headerName.compareTo("UNKNOWN") != 0){
					//KNOWN HEADER

					if(headerName.compareTo("DATA") == 0){
						//DATA HEADER

						//Parse the data header
						dh = RadarIngestorServices.parseDataHeader(dis);

						//Skip extra bytes at the end of data header
						dis.skipBytes(headerLength - 8 - RadarConfig.SIZE_DATA_HEADER);

						//Now process the following data based on the current radar scan mode
						//Determine the radar scan mode
						if(sgh != null && hk != null && sgh.getScanMode() == 0){
							//PPI Scan mode
							
							//Determine the current angle of the ray
							_angle = ((float)(dh.getStartAz() + dh.getEndAz())) / 2;
							_angle = (_angle * 360) / hk.getAngleScale();
							
							//Compute lat and lon for each data point and dump that tuple(lat,lon,data) to file
							for(int i = 0; i < dh.getNumGates(); ++i){
								
								//Distance from radar
								_range = i * hk.getGateWidth();
								
								//Latitude
								_lat = (float) (_range * Math.sin( Math.toRadians(_angle)));
									_lat = (float) (((float)hk.getRadarLatitude() / 1000000) + Math.toDegrees(_lat / RadarConfig.RADIUS_OF_EARTH));
									
								//Longitude
								_lon = (float) (_range * Math.cos( Math.toRadians(_angle)));
									_lon = (float) (((float)hk.getRadarLongitude() / 1000000) + Math.toDegrees(_lon / RadarConfig.RADIUS_OF_EARTH));
									
								//Dump to file
								logFile.write(_lat+","+_lon+","+ fieldHeaders.get(0).getValue(dis.readByte()));	
								logFile.newLine();
								logFile.flush();
							}
						}
						else{
							//Other scan mode [RHI, Fixed Angle, etc.]
							for(int j=0; j < dh.getNumGates(); ++j){
								logFile.write(fieldHeaders.get(0).getValue(dis.readByte()) + ",");
							}
							logFile.newLine();
							logFile.flush();
						}
					}
					else if(headerName.compareTo("HOUSEKEEPING") == 0){
						//HOUSEKEEPING HEADER

						//Parse the house keeping header
						hk = RadarIngestorServices.parseHousekeepingHeader(dis);
						Log("Antenna Mode: " + hk.getAntennaMode());
					}
					else if(headerName.compareTo("SCAN_SEGMENT") == 0){
						//SCAN SEGMENT HEADER

						//Parse the scan segment header
						sgh = RadarIngestorServices.parseScanSegmentHeader(dis);
						Log("Radar Scan Mode: " + sgh.getScanMode());

						//Create new data file based on the radar scan mode
						if(sgh.getScanMode() == 5){
							
							//Radar is idle i.e. not sending valid data.
							Log("Radar is idle!");
							break; //QUIT???
							
						}
						else{
							
							//Close previous data file
							if(logFile != null) logFile.close();
							
							//Create a new data file for given scan mode [fileName = CSU-CHILL_SCAN-MODE_TIMESTAMP.csv]
							String fileName = hk.getRadarID()+ "_"+sgh.getScanMode()+"_"+ new Date()+".csv";
							fileName = fileName.replaceAll(" ", "");
							logFile = new BufferedWriter(new FileWriter(fileName));
						}
					}
					else{
						//KNOWN BUT NOT DATA HEADER
						//System.out.println(headerName);
						dis.skipBytes(headerLength - 8);
					}
				}
				else{
					//UNKNOWN HEADER
					//WHAT TO DO ?
				}
			}

			//Close the connection to server
			server.close();

		} catch (UnknownHostException e) {

			Log("Unknown Host: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {

			Log("IO Exception: " + e.getMessage());
			e.printStackTrace();
		}

	}

	private static void Log(String message){
		System.out.println("["+Calendar.getInstance().getTime().toString() +"] "+ message);
		/*try {
			logFile.write("["+Calendar.getInstance().getTime().toString() +"] "+ message);
			logFile.newLine();
			logFile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}


}
