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
public class SciDBUser {

	private String	serverName;
	private String	serverAddress;
	private String	username;
	private String	password;
	private String	portalUserID;
	private String	title;

	public SciDBUser(String serverName, String serverAddress, String username, String password, String portalUserID,
		String title) {
		super();
		this.serverName = serverName;
		this.serverAddress = serverAddress;
		this.username = username;
		this.password = password;
		this.portalUserID = portalUserID;
		this.title = title;
	}

	public JSONObject getJSON() throws JSONException {
		JSONObject jsonData = new JSONObject();
		jsonData.put("title", title);
		jsonData.put("type", "scidb_user");

		if (serverName != null && !serverName.isEmpty())
			jsonData.put("field_scidb_server_name", getComplexObject("value", serverName));

		if (serverAddress != null && !serverAddress.isEmpty())
			jsonData.put("field_scidb_server_url", getComplexObject("value", serverAddress));

		if (username != null && !username.isEmpty())
			jsonData.put("field_scidb_username", getComplexObject("value", username));

		if (password != null && !password.isEmpty())
			jsonData.put("field_scidb_password", getComplexObject("value", password));

		if (portalUserID != null && !portalUserID.isEmpty())
			jsonData.put("field_portal_user", getComplexObject("uid", portalUserID));

		return jsonData;
	}

	private JSONObject getComplexObject(String key, String value) throws JSONException {
		JSONObject undObject = new JSONObject();
		JSONArray undArray = new JSONArray();
		JSONObject undArrayObject = new JSONObject();
		undArrayObject.put(key, value);
		undArray.put(undArrayObject);
		undObject.put("und", undArray);
		return undObject;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPortalUserID() {
		return portalUserID;
	}

	public void setPortalUserID(String portalUserID) {
		this.portalUserID = portalUserID;
	}

}
