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
		object.setTitle(input_Object.getString("title"));
		
		object.setOption(input_Object.getString("field_option"));
		
		object.setFormat(input_Object.getString("field_type"));
		
		object.setStatus(input_Object.getString("field_status"));
		
		object.setFormat(input_Object.getString("field_format"));
		
		
		return object;
	}

}
