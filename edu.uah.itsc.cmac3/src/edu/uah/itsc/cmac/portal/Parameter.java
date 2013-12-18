/**
 * 
 */
package edu.uah.itsc.cmac.portal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sshrestha
 * 
 */
public class Parameter {
	private String title;
	private String body;
	private String option;
	private String type;
	private String status;
	private String format;
	private String nid;
	private String default_value;

	public JSONObject getJSON() throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("title", title);
		jsonData.put("type", "parameter");
		if (body != null && !body.isEmpty())
			jsonData.put("body", getComplexObject("value", body));
		if (option != null && !option.isEmpty())
			jsonData.put("field_option", getComplexObject("value", option));
		if (type != null && !type.isEmpty())
			jsonData.put("field_type", getComplexObject("value", type));
		if (status != null && !status.isEmpty())
			jsonData.put("field_status", getComplexObject("value", status));
		if (format != null && !format.isEmpty())
			jsonData.put("field_format", getComplexObject("value", format));
		// added field - rohith
		if (default_value != null && !default_value.isEmpty())
			jsonData.put("field_default",
					getComplexObject("value", default_value));
		return jsonData;
	}

	private JSONObject getComplexObject(String key, String value)
			throws JSONException {

		JSONObject undObject = new JSONObject();
		JSONArray undArray = new JSONArray();
		JSONObject undArrayObject = new JSONObject();

		/*
		 * This method will return a JSONObject similar to "field_is_shared": {
		 * "und": [ { "value": "1" } ] }
		 */

		undArrayObject.put(key, value);
		undArray.put(undArrayObject);
		undObject.put("und", undArray);
		return undObject;

	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the option
	 */
	public String getOption() {
		return option;
	}

	/**
	 * @param option
	 *            the option to set
	 */
	public void setOption(String option) {
		this.option = option;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body
	 *            the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the format
	 */
	public String getFormat() {
		return format;
	}

	/**
	 * @param format
	 *            the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return the nid
	 */
	public String getNid() {
		return nid;
	}

	/**
	 * @param nid
	 *            the nid to set
	 */
	public void setNid(String nid) {
		this.nid = nid;
	}

	// --------------- Getters and setters added by rohith
	/**
	 * 
	 * @return default_value
	 */
	public String getDefault_value() {
		return default_value;
	}

	/**
	 * 
	 * @param default_value
	 */
	public void setDefault_value(String default_value) {
		this.default_value = default_value;
		// -----------------end of newly added code ---------
	}

}
