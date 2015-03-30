package edu.uah.itsc.xively.ingestor.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import edu.uah.itsc.xively.ingestor.config.XivelyConfig;



public class XivelyService {

	//Instance of this class
	private XivelyService instance = null;

	//Constructor
	public XivelyService(){

	}

	//Get an instance of this class
	public XivelyService instance(){
		if(this.instance == null){
			this.instance = new XivelyService();
		}
		return this.instance;
	}
	
	//Generate feed request url
	public static String generateFeedRequestURL(String feed_info){
		
		String s[] = feed_info.split(",");
		if(s.length == 4){
			return "https://api.xively.com/v2/feeds/"+s[0]+".json?apikey="+s[1];
		}
		else{
			System.out.println("WARNING: Invalid feed key: " + feed_info);
		}
		return "";
	}
	//Load feed list from file for batch update
	/*
	 * Template for feedList file
	 * feed_id_1,api_key_1
	 * feed_id_2,api_key_2
	 * ...,...
	 * ...,...
	 */
	public static Vector<String> loadFeedList(String fileName){
		
		Vector<String> feedList = new Vector<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));

			String line;
			while((line = br.readLine()) != null){

				//Ignore comment lines and blank lines
				if(line.trim().compareTo("") == 0 || line.charAt(0) == '#')
					continue;

				//
				feedList.add(line);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: File '" + fileName + "' not found!");
			System.exit(1);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error reading from file");
			e.printStackTrace();
			System.exit(1);
		}
		return feedList;

	}
	//Load dbserver configuration from file
	/*
	 * Template for db config file
	 * #No quotation mark (") needed for strings
	 * DB_PROTOCOL=protocol
	 * DB_SERVER_NAME=server_name
	 * DB_SERVER_PORT=port
	 * DB_NAME=name
	 * DB_USER_NAME=username
	 * DB_PASSWORD=password
	 */
	
	public static void loadDBConfigurationFromFile(String fileName){

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));

			String line;
			String keyValue[];
			while((line = br.readLine()) != null){

				//Ignore comment lines and blank lines
				if(line.trim().compareTo("") == 0 || line.charAt(0) == '#')
					continue;

				//
				keyValue = line.trim().split("=");

				//Check for proper length of configuration file
				if(keyValue.length != 2){
					System.out.println("Warning: Invalid configuration string found in config file '" + fileName + "'");
					continue;
				}

				//Check the value of key and assign them to respective config variable
				if(keyValue[0].toUpperCase().compareTo("DB_PROTOCOL") == 0){
					XivelyConfig.setDBProtocol(keyValue[1]);
				}
				else if(keyValue[0].toUpperCase().compareTo("DB_SERVER_NAME") == 0){
					XivelyConfig.setDBServer(keyValue[1]);
				}
				else if(keyValue[0].toUpperCase().compareTo("DB_SERVER_PORT") == 0){
					XivelyConfig.setDBPort(Integer.parseInt(keyValue[1]));
				}
				else if(keyValue[0].toUpperCase().compareTo("DB_NAME") == 0){
					XivelyConfig.setDBName(keyValue[1]);
				}
				else if(keyValue[0].toUpperCase().compareTo("DB_USER_NAME") == 0){
					XivelyConfig.setDBUsername(keyValue[1]);
				}
				else if(keyValue[0].toUpperCase().compareTo("DB_PASSWORD") == 0){
					XivelyConfig.setDBPassword(keyValue[1]);
				}
				else{
					System.out.println("WARNING: Unknown setting variable '" + keyValue[0] + "'");
				}


			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error: File '" + fileName + "' not found!");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error reading from file");
			e.printStackTrace();
			System.exit(1);
		}

	}
}
