package edu.uah.itsc.cmac.model;

import org.json.JSONObject;

public interface JSONCommunication {
	/**
	 * This method should generate a JSON object to represent the current state of the object.
	 * 
	 * @return JSONObject
	 */
	JSONObject toJSON();

	/**
	 * This method should generate a JSON formatted string to represent the current state of the object. It is a simple
	 * call to toJSON().toString().
	 * 
	 * @return
	 */
	String toJSONString();

	/**
	 * This method should return an instance of the implementing class using the JSON formatted String passed as
	 * parameter
	 * 
	 * @param jsonString
	 *            JSON formatted string. Must return null if the input String is invalid
	 * @return An instance of implementing class
	 */
//	Object toObject(String jsonString);
}
