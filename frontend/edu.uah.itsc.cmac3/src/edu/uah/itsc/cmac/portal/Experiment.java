/**
 * 
 */
package edu.uah.itsc.cmac.portal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.aws.User;

/**
 * @author sshrestha
 * 
 */
public class Experiment {

	private String	title;
	private String	description;
	private String	creator;
	private String	workflows;

	public Experiment(String title, String description, String userID, String workflows) {
		this.title = title;
		this.description = description;
		this.creator = userID;
		this.workflows = workflows;
	}

	public Experiment() {
		super();
	}

	public JSONObject getJSON() throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("title", title);
		jsonData.put("type", "experiment");
		if (description != null && !description.isEmpty())
			jsonData.put("body", getComplexObject("value", description));
		if (creator != null && !creator.isEmpty())
			jsonData.put("field_creator", getComplexObject("uid", User.username));
		// jsonData.put("field_creator", new JSONObject("{'und':'"
		// + creator + "'}"));
		// if (workflows != null && !workflows.isEmpty())
		// jsonData.put("field_workflows", new JSONObject("{'und':'"
		// + workflows + "'}"));
		return jsonData;
	}

	private JSONObject getComplexObject(String key, String value) throws JSONException {

		JSONObject undObject = new JSONObject();
		JSONArray undArray = new JSONArray();
		JSONObject undArrayObject = new JSONObject();

		/*
		 * This method will return a JSONObject similar to "field_is_shared": { "und": [ { "value": "1" } ] }
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * @param creator
	 *            the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * @return the workflows
	 */
	public String getWorkflows() {
		return workflows;
	}

	/**
	 * @param workflows
	 *            the workflows to set
	 */
	public void setWorkflows(String workflows) {
		this.workflows = workflows;
	}

}
