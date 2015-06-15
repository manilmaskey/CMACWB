package edu.uah.itsc.radar.main;


import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import edu.uah.itsc.radar.config.RadarConfig;
import edu.uah.itsc.radar.postprocessing.RadarIngestorPPIToNetCDF;
import edu.uah.itsc.radar.services.DataHeader;
import edu.uah.itsc.radar.services.FieldTypeInfoHeader;
import edu.uah.itsc.radar.services.HousekeepingHeader;
import edu.uah.itsc.radar.services.RadarIngestorServices;
import edu.uah.itsc.radar.services.RadarIngestorTimer;
import edu.uah.itsc.radar.services.ScanSegmentHeader;

public class RadarIngestor {

	//Server
	private static Socket server = null;

	//Field type info headers
	private static Vector<FieldTypeInfoHeader> fieldHeaders = new Vector<FieldTypeInfoHeader>();

	//Input and output stream to server
	private static DataOutputStream dos = null;
	private static DataInputStream dis = null;
	private static int headerType, headerLength;

	//Log File 
	private static BufferedWriter logFile = null;

	//Data File [ AT THE MOMENT I AM ONLY RECORDING DATA FOR PPI SCAN ]
	private static BufferedWriter dataFile = null;

	//Main
	public static void main(String[] args) {

		//Timer
		RadarIngestorTimer rit = null;

		//Initialize log file
		try {
			logFile = new BufferedWriter(new FileWriter("log.txt"));
		} catch (IOException e) {
			System.out.println("Log Error: " + e.getMessage());
			System.exit(1);
		}

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

			//Wait for buffer to be ready
			rit = new RadarIngestorTimer(2500); // milisecs
			rit.start();
			rit.join();

			//Read the field_type_info header sent by server
			while(dis.available() > 0){

				//Read header type and header length
				headerType = dis.readInt();
				headerLength = dis.readInt();

				//Parse the Field_Type_Info Header
				fieldHeaders.add(RadarIngestorServices.parseFieldTypeInfoHeader(dis));

			}
			Log("Received " + fieldHeaders.size() + " field info type header(s)");

			//Send request to receive all feeds
			Log("Send request for reflectivity");
			dos.writeLong(RadarConfig.FEED_REFLECTIVITY);

			//Wait for buffer to be ready
			rit = new RadarIngestorTimer(2500);
			rit.start();
			rit.join();

			//Various packets that can be received during real time reading of data from server
			DataHeader dh = null;
			HousekeepingHeader hk = null;
			ScanSegmentHeader sgh = null;

			//Keep info about last scan
			String lastPPIFilename = null;
			int lastScanMode = -1;

			//Main loop
			while(true){

				//Read header type and header length info
				headerType = dis.readInt();
				headerLength = dis.readInt();

				//Apply action based on type of header
				switch(headerType){

				case RadarConfig.HEADER_DATA:
					//DATA HEADER

					//Parse the data header
					dh = RadarIngestorServices.parseDataHeader(dis);

					//Skip extra bytes at the end of data header
					dis.skipBytes(headerLength - 8 - RadarConfig.SIZE_DATA_HEADER);

					//Now process the following data based on the current radar scan mode
					if(sgh != null && hk != null){

						//Determine the radar scan mode
						switch(sgh.getScanMode()){

						case 0:
							//PPI Scan mode

							//Record reflectivity data
							RadarIngestorServices.recordData_PPI(dataFile, dis, dh, hk, fieldHeaders.get(0));
							break;


							//[FILL IN LOGIC FOR OTHER SCAN MODES HERE!]

						default:
							//AT THE MOMENT NO PROCESSING LOGIC IS SPECIFIED FOR OTHER TYPE OF RADAR SCANS!
							//Other scan mode [RHI, Fixed Angle, etc.]
							dis.skipBytes(dh.getNumGates());
							break;
						}
					}
					break;
				case RadarConfig.HEADER_HOUSEKEEPING:
					//HOUSEKEEPING HEADER

					//Parse the house keeping header
					hk = RadarIngestorServices.parseHousekeepingHeader(dis);
					//Log("Antenna Mode: " + hk.getAntennaMode());
					break;

				case RadarConfig.HEADER_SCAN_SEGMENT:
					//SCAN SEGMENT HEADER

					//Parse the scan segment header
					sgh = RadarIngestorServices.parseScanSegmentHeader(dis);
					Log("Radar Scan Mode: " + sgh.getScanMode());
					
					//Determine the last scan mode and process the file accordingly
					if(lastScanMode >= 0){
						switch(lastScanMode){
						case 0:
							//PPI mode
							if(dataFile != null){
								
								//Close data file
								dataFile.close();
								File tempFile = new File(lastPPIFilename);
								if( tempFile.length() > 100){
									//Run Post Processor to convert that to netCDF file
									(new RadarIngestorPPIToNetCDF(lastPPIFilename, 
											hk.getRadarLatitude() / 1000000.0f, 
											hk.getRadarLongitude() / 1000000.0f,
											lastPPIFilename.replace(".csv", ".nc"))).start();
								}else{
									System.out.println("Skipping file: " + lastPPIFilename);
									tempFile.delete();
								}
							}
							break;
							//ADD POST PROCESSING LOGIC FOR OTHER MODES
						}
					}

					//Now logic for new mode
					//Create new data file based on the radar scan mode
					switch(sgh.getScanMode()){
					case 5:
						//IDLE mode

						//Radar is idle i.e. not sending valid data.
						Log("Radar is idle!");
						Log("Retrying in 10 secs");
						rit = new RadarIngestorTimer(10000);
						rit.start(); rit.join();

						Log("Resending request for feeds...");
						dos.writeLong(RadarConfig.FEED_REFLECTIVITY);
						rit = new RadarIngestorTimer(2500);
						rit.start(); rit.join();

						break;
					case 0:
						//PPI Mode
						//Create a new data file for given scan mode [fileName = CSU-CHILL_SCAN-MODE_TIMESTAMP.csv]
						lastPPIFilename = hk.getRadarID()+ "_"+sgh.getScanMode()+"_"+ new Date()+ ".csv";
						lastPPIFilename = lastPPIFilename.replaceAll(" ", "_");
						dataFile = new BufferedWriter(new FileWriter(lastPPIFilename));
						break;
					default:
						//HANDLE INITIALIZATION FOR OTHER MODES
					}

					//Save the mode
					lastScanMode = sgh.getScanMode();
					break;

				case RadarConfig.HEADER_EXTENDED_TRACKING:
				case RadarConfig.HEADER_FIELD_INFO_TYPE:
				case RadarConfig.HEADER_POWER_METERS_UPDATE:
				case RadarConfig.HEADER_PROCESSOR_INFO:
				case RadarConfig.HEADER_RADAR_INFO:
				case RadarConfig.HEADER_SWEEP_NOTICE:
				case RadarConfig.HEADER_TRACKING:
				case RadarConfig.HEADER_TRANSMITTER_INFO:
					//KNOWN BUT NOT NECESSARY
					dis.skipBytes(headerLength - 8);
					break;
				default:
					//UNKNOWN HEADER
					//WHAT TO DO ?
				}
			}

			//Close the connection to server
			//logFile.close();
			//server.close();

		} catch (UnknownHostException e) {

			Log("Unknown Host: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {

			Log("IO Exception: " + e.getMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//Logger
	private static void Log(String message){
		System.out.println("["+Calendar.getInstance().getTime().toString() +"] "+ message);
		try {
			logFile.write("["+Calendar.getInstance().getTime().toString() +"] "+ message);
			logFile.newLine();
			logFile.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
