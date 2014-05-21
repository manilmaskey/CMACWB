/**
 * 
 */
package edu.uah.itsc.cmac.portal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author sshrestha
 * 
 */
public class PortalUtilities {
	private static Properties	properties	= null;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
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

	private static String getKeyValueFromProperties(String key) {
		if (properties != null && properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		if (properties == null)
			properties = new Properties();
		try {
			properties.load(PortalUtilities.class.getClassLoader().getResourceAsStream("portal.properties"));
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return properties.getProperty(key);
	}

	/*
	 * This method builds the REST URL to fetch data from server. The URL is defined in search.properties file. Also,
	 * the extraquery is appended to the URL, so that the content type to search on can be changed dynamically
	 */
	public static String getWorkflowFeedURL() {
		return getKeyValueFromProperties("workflow_url");
	}

	public static String getNodeRestPoint() {
		return getKeyValueFromProperties("node_rest_url");

	}

	public static String getCronURL() {
		return getKeyValueFromProperties("portal_cron_url");

	}

	public static String getExperimentFeedURL() {
		return getKeyValueFromProperties("experiment_url");
	}
	
	public static String getTokenURL() {
		return getKeyValueFromProperties("token_url");
	}

	public static HashMap<String, String> getPortalWorkflowDetails(String path) {
		String jsonText = PortalUtilities.getDataFromURL(PortalUtilities.getWorkflowFeedURL()
			+ "?field_is_shared=All&field_could_path_value=" + path);
		JSONParser parser = new JSONParser();
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
		JSONParser parser = new JSONParser();
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
}
