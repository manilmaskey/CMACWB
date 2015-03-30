package edu.uah.itsc.xively.ingestor.main;

import java.util.Date;
import java.util.Vector;

import edu.uah.itsc.xively.ingestor.exceptions.FeedParserException;
import edu.uah.itsc.xively.ingestor.service.Feed;
import edu.uah.itsc.xively.ingestor.service.XivelyService;
import edu.uah.itsc.xively.ingestor.utils.XivelyHTTPClient;
import edu.uah.itsc.xively.ingestor.utils.XivelyJSONParser;
import edu.uah.itsc.xively.ingestor.utils.XivelyPOSTGRESClient;

public class XivelyTest {

	public static void main(String[] args) {

		System.out.println("["+new Date()+"]");
		
		//Process arguments
		if(args.length != 2){
			System.out.println("Usage: java -jar xively_ingestor.jar <db_config_file> <feed_list_file>");
			System.exit(0);
		}
		else if(args[0].trim().compareTo("") == 0 || args[1].trim().compareTo("") == 0){
			System.out.println("Please specify a file name");
			System.out.println("Usage: java -jar xively_ingestor.jar <db_config_file> <feed_list_file>");
			System.exit(1);
		}
		
		//Load DB configuration from file
		XivelyService.loadDBConfigurationFromFile(args[0]);
		
		//Load the list of feed to extract from file
		Vector<String> feedList = XivelyService.loadFeedList(args[1]);
		
		//
		String requestUrl;
		String jsonResponse;
		Feed feed = null;
		for(String s : feedList){
			
			//Generate Request URL
			requestUrl = XivelyService.generateFeedRequestURL(s);
			
			//Request JSON for feed
			jsonResponse = XivelyHTTPClient.GET(requestUrl);
			
			//Parse the JSON response
			try {
				feed = XivelyJSONParser.parseFeed(jsonResponse);
			} catch (FeedParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String sa[] = s.split(",");
			feed.setLat(Float.parseFloat(sa[2]));
			feed.setLon(Float.parseFloat(sa[3]));
			
			//Push the feed data to database
			XivelyPOSTGRESClient.pushToDatabase(feed);
		}
		
	}

}
