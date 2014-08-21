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
public class Notification {
	private String	title;
	private String	description;
	private String	recipients;
	private String	notSeenBy;
	private String	path;
	private String	clonePath;
	private String	workflow;
	private String	owner;

	public JSONObject getJSON() throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("title", title);
		jsonData.put("type", "notification");
		if (description != null && !description.isEmpty())
			jsonData.put("body", getComplexObject("value", description));
		if (workflow != null && !workflow.isEmpty())
			jsonData.put("field_workflow", getComplexObject("value", workflow));
		if (recipients != null && !recipients.isEmpty())
			jsonData.put("field_recipient", getComplexObjectArray(null, recipients.split(",\\s*")));
		else
			jsonData.put("field_recipient", new JSONObject("{'und':'_none'}"));
		if (notSeenBy != null && !notSeenBy.isEmpty())
			jsonData.put("field_not_seen_by", getComplexObjectArray(null, notSeenBy.split(",\\s*")));
		else
			jsonData.put("field_not_seen_by", new JSONObject("{'und':'_none'}"));
		if (path != null && !path.isEmpty())
			jsonData.put("field_path", getComplexObject("value", path));
		if (clonePath != null && !clonePath.isEmpty())
			jsonData.put("field_clone_path", getComplexObject("value", clonePath));
		if (owner != null && !owner.isEmpty())
			jsonData.put("field_workflow_owner", getComplexObject("uid", owner));
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

	private JSONObject getComplexObjectArray(String key, String[] values) throws JSONException {
		JSONObject undObject = new JSONObject();
		JSONArray undArray = new JSONArray();

		/*
		 * This method will return a JSONObject similar to "field_is_shared": { "und": [ { "value": "1" } ] }
		 */
		for (String value : values) {
			if (key != null) {
				JSONObject undArrayObject = new JSONObject();
				undArrayObject.put(key, value);
				undArray.put(undArrayObject);
			}
			else
				undArray.put(value);
		}

		undObject.put("und", undArray);
		return undObject;

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecipients() {
		return recipients;
	}

	public void setRecipients(String recipients) {
		this.recipients = recipients;
	}

	public String getNotSeenBy() {
		return notSeenBy;
	}

	public void setNotSeenBy(String notSeenBy) {
		this.notSeenBy = notSeenBy;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getClonePath() {
		return clonePath;
	}

	public void setClonePath(String clonePath) {
		this.clonePath = clonePath;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "{\nworkflow:" + workflow + "title:" + title + ",\n" + "description:" + description + ",\n" + "path:"
			+ path + ",\n" + "clonePath:" + clonePath + ",\n" + "recipients:" + recipients + ",\n" + "notSeenBy:"
			+ notSeenBy + "\n}";
	}
}
