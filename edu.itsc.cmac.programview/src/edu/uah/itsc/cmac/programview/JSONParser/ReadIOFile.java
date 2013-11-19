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
		// set vid
		object.setVid(input_Object.getInt("vid"));
		// set title
		object.setTitle(input_Object.getString("title"));
		// set data type
		if (input_Object.get("field_data_type") instanceof JSONArray){
			
		}else{
		JSONObject dataType_Object = input_Object.getJSONObject("field_data_type");
		object.setData_Type((dataType_Object.getJSONArray("und")).getJSONObject(0).getString("value"));
		}
		// set data value
		if (input_Object.get("field_data_value") instanceof JSONArray){
			// the data value is empty
			object.setData_Value(null);
		}else if(input_Object.get("field_data_value") instanceof JSONObject){
			JSONObject dataValue_Object = input_Object.getJSONObject("field_data_value");
			object.setData_Value((dataValue_Object.getJSONArray("und")).getJSONObject(0).getString("value"));
		}
		return object;
	}

}
