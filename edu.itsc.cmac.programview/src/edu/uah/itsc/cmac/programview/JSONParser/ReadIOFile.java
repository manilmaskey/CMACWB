package edu.uah.itsc.cmac.programview.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uah.itsc.uah.programview.programObjects.IOPOJO;


/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class ReadIOFile {

	/**
	 * Read the data from the JSONObject and stores it in an IOPOJO object.
	 * 
	 * @param IOObject
	 *            JSONObject of the I/O
	 * @return IOPOJO object
	 * @throws Exception 
	 */
	public IOPOJO read_IOFile(JSONObject IOObject, String path) throws Exception {
		
		String IO_Path = path + IOObject.getInt("nid");
		JSONObject input_Object = new JSONObject(JsonURLReader.readUrl(IO_Path));
		
		IOPOJO object = new IOPOJO();
		
		// set required data
//		JSONObject titleobject = (JSONObject) input_Object.get("title");
//		String title = (String) titleobject.get("safe_value");
		object.setTitle((String) input_Object.get("title"));
		
		if (!(input_Object.get("field_option") instanceof JSONArray)){
		JSONObject optionobject = (JSONObject) (input_Object.get("field_option"));
		JSONArray ound = optionobject.getJSONArray("und");
		JSONObject ovalue = ound.getJSONObject(0);
		String option = ovalue.getString("safe_value");
		object.setOption(option);
		}else{
			object.setOption("");
		}
		
		if (!(input_Object.get("field_type") instanceof JSONArray)){
		JSONObject feildobject = (JSONObject) (input_Object.get("field_type"));
		JSONArray tund = feildobject.getJSONArray("und");
		JSONObject tvalue = tund.getJSONObject(0);
		String type = tvalue.getString("safe_value");
		object.setType(type);
		}else{
			object.setType("");
		}
		
		if (!(input_Object.get("field_status") instanceof JSONArray)){
		JSONObject statusobject = (JSONObject) (input_Object.get("field_status"));
		JSONArray sund = statusobject.getJSONArray("und");
		JSONObject svalue = sund.getJSONObject(0);
		String status = svalue.getString("safe_value");
		object.setStatus(status);
		}else{
			object.setStatus("");
		}
		
		if (!(input_Object.get("field_format") instanceof JSONArray)){
		JSONObject formatobject = (JSONObject) (input_Object.get("field_format"));
		JSONArray fund = formatobject.getJSONArray("und");
		JSONObject fvalue = fund.getJSONObject(0);
		String format = fvalue.getString("safe_value");
		object.setFormat(format);
		}else{
			object.setFormat("");
		}
		
		return object;
	}

}
