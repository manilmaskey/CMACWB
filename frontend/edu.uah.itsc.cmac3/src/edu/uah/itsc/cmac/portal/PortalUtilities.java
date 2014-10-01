/**
 * 
 */
package edu.uah.itsc.cmac.portal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.Utilities;
import edu.uah.itsc.cmac.util.GITUtility;

/**
 * @author sshrestha
 * 
 */
public class PortalUtilities {
	private static ArrayList<PortalUser>	portalUserList	= null;
	private static JSONParser				parser			= new JSONParser();

	private PortalUtilities() {
	}

	public static String getDataFromHTTP(String url) {
		PortalPost portalPost = new PortalPost();
		HttpResponse response = portalPost.get(url);
		InputStream is = null;
		StringBuilder sb = new StringBuilder();
		try {
			is = response.getEntity().getContent();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}
		}
		catch (IllegalStateException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String getDataFromURL(String url) {

		StringBuilder sb = new StringBuilder();
		String xmlText;
		try {
			InputStream is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}

		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		xmlText = sb.toString();
		return xmlText;
	}

	public static String getWorkflowFeedURL() {
		return Utilities.getKeyValueFromPreferences("portal", "workflow_url");
	}

	public static String getNodeRestPoint() {
		return Utilities.getKeyValueFromPreferences("portal", "node_rest_url");
	}

	public static String getCronURL() {
		return Utilities.getKeyValueFromPreferences("portal", "portal_cron_url");
	}

	public static String getExperimentFeedURL() {
		return Utilities.getKeyValueFromPreferences("portal", "experiment_url");
	}

	public static String getTokenURL() {
		return Utilities.getKeyValueFromPreferences("portal", "token_url");
	}

	public static String getPortalUserURL() {
		return Utilities.getKeyValueFromPreferences("portal", "portal_user_url");
	}

	public static String getPortalLoginURL() {
		return Utilities.getKeyValueFromPreferences("portal", "portal_login_url");
	}

	public static String getUserListURL() {
		return Utilities.getKeyValueFromPreferences("portal", "user_list_url");
	}

	public static String getNotificationURL() {
		return Utilities.getKeyValueFromPreferences("portal", "notification_url");
	}

	public static String getPortalDomain() {
		return Utilities.getKeyValueFromPreferences("portal", "portal_domain");
	}

	public static HashMap<String, String> getPortalWorkflowDetails(String path) {
		path = "/" + path;
		path = path.replaceFirst("//", "/");
		String jsonText = PortalUtilities.getDataFromURL(PortalUtilities.getWorkflowFeedURL()
			+ "?field_is_shared=All&field_could_path_value=" + path);
		Object obj;
		try {
			obj = parser.parse(jsonText);
			JSONObject workflows = (JSONObject) obj;

			if (workflows == null)
				return null;
			JSONArray workFlowArray = (JSONArray) workflows.get("workflows");
			if (workFlowArray == null || workFlowArray.size() == 0)
				return null;
			JSONObject workflow = (JSONObject) workFlowArray.get(0);
			workflow = (JSONObject) workflow.get("workflow");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("nid", workflow.get("nid").toString());
			map.put("path", workflow.get("path").toString());
			map.put("title", workflow.get("title").toString());
			map.put("description", workflow.get("description").toString());
			map.put("keywords", workflow.get("keywords").toString());
			map.put("isShared", workflow.get("isShared").toString());
			map.put("creator", workflow.get("creator").toString());
			map.put("submittor", workflow.get("submittor").toString());
			map.put("allow_clone_to", workflow.get("allow_clone_to").toString());

			return map;
		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Unable to parse json object");
			return null;
		}
	}

	public static HashMap<String, String> getPortalNotificationDetails(String path) {
		path = "/" + path;
		path = path.replaceFirst("//", "/");
		String jsonText = PortalUtilities.getDataFromURL(PortalUtilities.getNotificationURL() + "?field_path_value="
			+ path);
		Object obj;
		try {
			obj = parser.parse(jsonText);
			JSONObject workflows = (JSONObject) obj;

			if (workflows == null)
				return null;
			JSONArray workFlowArray = (JSONArray) workflows.get("notifications");
			if (workFlowArray == null || workFlowArray.size() == 0)
				return null;
			JSONObject workflow = (JSONObject) workFlowArray.get(0);
			workflow = (JSONObject) workflow.get("notification");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("nid", workflow.get("nid").toString());
			map.put("path", workflow.get("path").toString());
			map.put("title", workflow.get("title").toString());
			map.put("description", workflow.get("description").toString());
			map.put("recipient", workflow.get("recipient").toString());
			map.put("notSeenBy", workflow.get("not_seen_by").toString());
			map.put("clonePath", workflow.get("clone_path").toString());

			return map;
		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Unable to parse json object");
			return null;
		}
	}

	public static HashMap<String, String> getPortalExperimentDetails(String bucketName) {
		String jsonText = PortalUtilities.getDataFromURL(PortalUtilities.getExperimentFeedURL() + "?title="
			+ bucketName);
		Object obj;
		try {
			obj = parser.parse(jsonText);
			JSONObject experiments = (JSONObject) obj;

			if (experiments == null)
				return null;
			JSONArray experimentArray = (JSONArray) experiments.get("experiments");
			if (experimentArray == null || experimentArray.size() == 0)
				return null;
			JSONObject experiment = (JSONObject) experimentArray.get(0);
			experiment = (JSONObject) experiment.get("experiment");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("nid", experiment.get("nid").toString());
			map.put("title", experiment.get("title").toString());
			map.put("creator", experiment.get("creator").toString());
			map.put("creatorID", experiment.get("creatorID").toString());
			map.put("description", experiment.get("description").toString());
			return map;
		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Unable to parse json object");
			return null;
		}
	}

	public static ArrayList<PortalUser> getUserList() {
		if (portalUserList != null)
			return portalUserList;
		else {
			try {
				PortalPost post = new PortalPost();
				HttpResponse response = post.get(PortalUtilities.getUserListURL());
				String jsonText = EntityUtils.toString(response.getEntity());
				Object obj;
				obj = parser.parse(jsonText);
				JSONObject users = (JSONObject) obj;

				if (users == null)
					return null;
				JSONArray userArray = (JSONArray) users.get("users");
				if (userArray == null || userArray.size() == 0)
					return null;
				int i = 0;
				portalUserList = new ArrayList<PortalUser>();
				for (i = 0; i < userArray.size(); i++) {
					JSONObject user = (JSONObject) userArray.get(i);
					user = (JSONObject) user.get("user");

					String username = (String) user.get("name");
					String uid = (String) user.get("uid");
					String email = (String) user.get("email");
					String firstName = (String) user.get("first_name");
					String lastName = (String) user.get("last_name");

					PortalUser pUser = new PortalUser(username, uid, email, firstName, lastName);
					portalUserList.add(pUser);
				}

				return portalUserList;
			}
			catch (ParseException e) {
				e.printStackTrace();
				System.out.println("Unable to parse json object");
				return null;
			}
			catch (IllegalStateException e) {
				e.printStackTrace();
				return null;
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static void checkNotifications() {
		String url = PortalUtilities.getNotificationURL() + "?field_recipient_uid=" + User.portalUserID
			+ "&field_not_seen_by_uid=" + User.portalUserID;
		String jsonText = PortalUtilities.getDataFromURL(url);
		Object obj;
		try {
			JSONParser parser = new JSONParser();
			obj = parser.parse(jsonText);
			JSONObject workflows = (JSONObject) obj;

			if (workflows == null)
				return;
			JSONArray workFlowArray = (JSONArray) workflows.get("notifications");
			if (workFlowArray == null || workFlowArray.size() == 0)
				return;

			for (int i = 0; i < workFlowArray.size(); i++) {
				JSONObject workflowObject = (JSONObject) workFlowArray.get(i);
				workflowObject = (JSONObject) workflowObject.get("notification");
				String clonePath = workflowObject.get("clone_path").toString();
				String path = workflowObject.get("path").toString();
				String workflowName = workflowObject.get("workflow").toString();
				String owner = workflowObject.get("owner").toString();
				boolean done = processNotification(workflowName, owner, path, clonePath + ".git");
				// Remove seen by regardless of whether user clones the workflow or not.
				// if (done) {
				String nodeID = workflowObject.get("nid").toString();
				PortalPost portalPost = new PortalPost();
				Notification notification = new Notification();
				notification.setClonePath(clonePath);
				notification.setPath(path);
				notification.setWorkflow(workflowName);
				notification.setRecipients(workflowObject.get("recipient").toString());
				String notSeenBy = workflowObject.get("not_seen_by").toString();
				HashSet<String> notSeenBySet = new HashSet<String>(Arrays.asList(notSeenBy.split(",\\s*")));
				notSeenBySet.remove(User.portalUserID);
				notSeenBy = joinSet(notSeenBySet, ",");
				notification.setNotSeenBy(notSeenBy);

				HttpResponse response = portalPost.put(PortalUtilities.getNodeRestPoint() + "/" + nodeID,
					notification.getJSON());
				if (response.getStatusLine().getStatusCode() != 200) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
						"Could not update portal while trying to update notification.");
				}
				// }
			}

		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Unable to parse json object");
			return;
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static boolean processNotification(String workflowName, String owner, String path, String clonePath) {
		path = "/" + path;
		path = path.replaceAll("//", "/");
		String[] paths = path.split("/");
		String bucket = paths[1];
		String message = "User '" + owner + "' has sent workflow '" + workflowName + "'. Do you want to accept it?";
		boolean answer = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Notification", message);
		if (answer) {
			try {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(bucket);
				if (!project.exists())
					project.create(null);
				if (!project.isOpen())
					project.open(null);
				String repoLocalPath = project.getLocation().toString() + "/" + workflowName;
				S3 s3 = new S3();
				File jgitFile = new File(System.getProperty("user.home") + "/.jgit");
				s3.createJgitContents(jgitFile, true);

				GITUtility.cloneRepository(repoLocalPath, clonePath);
				s3.createJgitContents(jgitFile, false);

				GITUtility.removeRemote(workflowName, project.getLocation().toString());
				project.refreshLocal(IProject.DEPTH_INFINITE, null);
				return true;
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}

	private static <T> String joinSet(Set<T> set, String delimiter) {
		Iterator<T> iter = set.iterator();
		String joinedString = null;
		while (iter.hasNext()) {
			T element = iter.next();
			joinedString = (joinedString == null) ? element.toString() : joinedString + delimiter + element.toString();
		}
		return joinedString;
	}

}
