package edu.uah.itsc.xively.ingestor.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.xively.ingestor.exceptions.FeedParserException;
import edu.uah.itsc.xively.ingestor.service.Datastream;
import edu.uah.itsc.xively.ingestor.service.Feed;

public class XivelyJSONParser {

	public static Feed parseFeed(String feedJSON) throws FeedParserException{

		//Create a new fed object
		Feed feed = new Feed();

		try {
			JSONObject obj = new JSONObject(feedJSON);

			//Fill the value for feed
			if(obj.has("id"))
				feed.setId(obj.getString("id"));
			if(obj.has("title"))
				feed.setTitle(obj.getString("title"));
			if(obj.has("description"))
				feed.setDescription(obj.getString("description"));
			if(obj.has("feed"))
				feed.setFeedUrl(obj.getString("feed"));
			if(obj.has("status"))
				feed.setStatus(obj.getString("status"));
			if(obj.has("updated"))
				feed.setUpdated(obj.getString("updated"));
			if(obj.has("created"))
				feed.setCreated(obj.getString("created"));
			if(obj.has("creator"))
				feed.setCreator(obj.getString("creator"));
			if(obj.has("version"))
				feed.setVersion(obj.getString("version"));

			//This is primary key so we need this field from JSON
			if(obj.has("device_serial"))
				feed.setDeviceSerial(obj.getString("device_serial"));
			else{
				throw new FeedParserException("Device serial not found for feed: " + feed.getId());
			}

			if(obj.has("product_id"))
				feed.setProductId(obj.getString("product_id"));
			if(obj.has("private"))
				feed.setPrivate(obj.getString("private"));


			//Fill up the datastream for this feed
			JSONArray arr = obj.getJSONArray("datastreams");
			for(int i=0; i < arr.length(); ++i){

				//Create a new datastream object
				Datastream datastream = new Datastream();
				JSONObject dsObj = arr.getJSONObject(i);

				//Fill up the values for given datastream

				//This is primary key so this field is needed
				if(dsObj.has("id"))
					datastream.setStreamId(dsObj.getString("id").toLowerCase());
				else{
					throw new FeedParserException("Datastream Id not found for one or more datastreams in Feed: " + feed.getId());
				}

				if(dsObj.has("at"))
					datastream.setUpdated(dsObj.getString("at"));

				if(dsObj.has("unit")){
					if(dsObj.getJSONObject("unit").has("symbol"))
						datastream.setUnitSymbol(dsObj.getJSONObject("unit").getString("symbol"));

					if(dsObj.getJSONObject("unit").has("label"))
						datastream.setUnitLabel(dsObj.getJSONObject("unit").getString("label"));
				}

				if(dsObj.has("max_value"))
					datastream.setMaxValue(dsObj.getString("max_value"));

				if(dsObj.has("min_value"))
					datastream.setMinValue(dsObj.getString("min_value"));

				if(dsObj.has("current_value"))
					datastream.setCurrentValue(dsObj.getString("current_value"));

				//Append datastream object to feed
				feed.addDatastream(datastream);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			System.out.println("JSON Parser Error: Invalid json format!");
			e.printStackTrace();
		}
		return feed;
	}
}
