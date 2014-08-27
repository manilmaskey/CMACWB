package edu.uah.itsc.cmac.portal;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import edu.uah.itsc.aws.User;

public class PortalConnector {

	private String	adminUsername	= "portal";
	private String	adminPassword	= "portal123";

	public PortalConnector() {
	}

	public JSONObject connect(String username, String password) {

		JSONObject jsonObject = null;
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(PortalUtilities.getPortalLoginURL());
		try {
			JSONObject json = new JSONObject();

			System.out.println("username/password " + username + " " + password);
			json.put("username", username);
			json.put("password", password);

			StringEntity se = new StringEntity(json.toString());
			// set request content type
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

			httppost.setEntity(se);

			HttpResponse response = httpclient.execute(httppost);
			String jsonResponse = EntityUtils.toString(response.getEntity());
			// System.out.println("jsonResponse "+jsonResponse);
			jsonObject = new JSONObject(jsonResponse);
			JSONObject jsonUser = jsonObject.getJSONObject("user");
			// System.out.println("session"+session_name+"  "+session_id);
			User.sessionName = jsonObject.getString("session_name");
			User.sessionID = jsonObject.getString("sessid");

			// Shreedhan - Store portal's User ID as well
			jsonUser = jsonObject.getJSONObject("user");
			User.portalUserID = jsonUser.getString("uid");
			User.userEmail = jsonUser.getString("mail");

			JSONObject jsonRoles = jsonUser.getJSONObject("roles");
			Iterator<String> roleKeys = jsonRoles.keys();
			String roleKey = null;
			String roleValue = null;
			if (User.userRoles == null)
				User.userRoles = new HashMap<String, String>();
			while (roleKeys.hasNext()) {
				roleKey = roleKeys.next();
				roleValue = jsonRoles.getString(roleKey);
				User.userRoles.put(roleKey, roleValue);
				if (roleValue.equalsIgnoreCase("admin") || roleValue.equalsIgnoreCase("administrator"))
					User.isAdmin = true;
			}
			// User.userEmail = jsonUser.getString("mail");
			if (!username.equals(adminUsername)) {
				JSONObject jsonAWSAccessKeyField = jsonUser.getJSONObject("field_access_key");
				JSONObject jsonAWSAccessKey = (JSONObject) jsonAWSAccessKeyField.getJSONArray("und").get(0);
				User.awsAccessKey = jsonAWSAccessKey.getString("value");
				JSONObject jsonAWSSecretKeyField = jsonUser.getJSONObject("field_secret_key");
				JSONObject jsonAWSSecretKey = (JSONObject) jsonAWSSecretKeyField.getJSONArray("und").get(0);
				User.awsSecretKey = jsonAWSSecretKey.getString("value");
				// System.out.println("jsonAWSAccessKeyField "+jsonAWSAccessKey.getString("value"));
				// System.out.println("jsonAWSSecretKey "+jsonAWSSecretKey.getString("value"));
			}

		}
		catch (Exception e) {
			System.out.println("Unable to login");
			return null;
		}

		return jsonObject;
	}

	public JSONObject connectAdmin() {

		return connect(adminUsername, adminPassword);
	}

	// /////////////////////////////////////////////////////////////////////////////

	public JSONObject createAccount(String username, String password, String email) {
		JSONObject jsonObject = connectAdmin();
		HttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(PortalUtilities.getPortalUserURL());

		try {
			String session_name = jsonObject.getString("session_name");
			String session_id = jsonObject.getString("sessid");

			JSONObject json = new JSONObject();
			json.put("name", username);
			json.put("pass", password);
			json.put("mail", email);

			StringEntity se = new StringEntity(json.toString());
			// set request content type
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httppost.setEntity(se);

			BasicHttpContext mHttpContext = new BasicHttpContext();
			CookieStore mCookieStore = new BasicCookieStore();

			BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
			cookie.setVersion(0);
			cookie.setDomain(PortalUtilities.getPortalDomain());
			cookie.setPath("/");
			mCookieStore.addCookie(cookie);

			mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

			PortalPost post = new PortalPost();
			String csrfToken = post.getCSRF(PortalUtilities.getTokenURL(), mHttpContext);
			httppost.addHeader("X-CSRF-TOKEN", csrfToken);

			HttpResponse response = httpclient.execute(httppost, mHttpContext);

			System.out.println("Response " + response.toString());
			// String jsonResponse = EntityUtils.toString(response.getEntity());
			// System.out.println(jsonResponse);

			jsonObject = connect(username, password);

		}
		catch (Exception e) {

			System.out.println(e.toString());
			return null;
		}

		return jsonObject;
	}
	// /////////////////////////////////////////////////////////////////////////////

}

// public boolean connect(){
// JSONParser parser = new JSONParser();
//
// Bundle bundle = Activator.getDefault().getBundle();
// Path path = new Path("users.json");
// URL url = FileLocator.find(bundle,path,Collections.EMPTY_MAP);
// URL fileUrl = null;
// boolean found = false;
// try{
// fileUrl = FileLocator.toFileURL(url);
// JSONArray obj = (JSONArray)parser.parse(new FileReader(fileUrl.getPath()));
// int size = obj.size();
//
// int i=0;
// String uname;
// String pword;
// String awskey;
// String awssecretpass ;
// String rootfolder;
// while (!found && i < size){
// JSONObject userItem = (JSONObject) obj.get(i);
// uname = (String) userItem.get("username");
// pword = (String) userItem.get("password");
// awskey = (String) userItem.get("awskey");
// awssecretpass = (String) userItem.get("awssecretpass");
// rootfolder = (String) userItem.get("rootfolder");
// i++;
// if (username.equals(uname) && password.equals(pword)){
// found = true;
//
// User.username = uname;
// User.password = pword;
// User.awsAccessKey = awskey;
// User.awsSecretKey = awssecretpass;
// User.rootFolder = rootfolder;
// }
//
// }
//
//
// } catch (FileNotFoundException e) {
// e.printStackTrace();
// } catch (IOException e) {
// e.printStackTrace();
// }
// catch (org.json.simple.parser.ParseException e) {
// e.printStackTrace();
// }
//
// return found;
// }

// public boolean connect(){
// JSONParser parser = new JSONParser();
//
// Bundle bundle = Activator.getDefault().getBundle();
// Path path = new Path("users.json");
// URL url = FileLocator.find(bundle,path,Collections.EMPTY_MAP);
// URL fileUrl = null;
//
//
//
//
// boolean found = false;
// try{
// fileUrl = FileLocator.toFileURL(url);
// JSONArray obj = (JSONArray)parser.parse(new FileReader(fileUrl.getPath()));
// int size = obj.size();
//
// int i=0;
// String uname;
// String pword;
// String awskey;
// String awssecretpass ;
// String rootfolder;
// while (!found && i < size){
// JSONObject userItem = (JSONObject) obj.get(i);
// uname = (String) userItem.get("username");
// pword = (String) userItem.get("password");
// awskey = (String) userItem.get("awskey");
// awssecretpass = (String) userItem.get("awssecretpass");
// rootfolder = (String) userItem.get("rootfolder");
// i++;
// if (username.equals(uname) && password.equals(pword)){
// found = true;
//
// User.username = uname;
// User.password = pword;
// User.awsAccessKey = awskey;
// User.awsSecretKey = awssecretpass;
// User.rootFolder = rootfolder;
// }
//
// }
//
//
// } catch (FileNotFoundException e) {
// e.printStackTrace();
// } catch (IOException e) {
// e.printStackTrace();
// }
// catch (org.json.simple.parser.ParseException e) {
// e.printStackTrace();
// }
//
// return found;
// }
