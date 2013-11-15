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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author sshrestha
 * 
 */
public class PortalUtilities {
	private static Properties properties = null;

	private PortalUtilities() {
	}

	public static String getDataFromURL(String url) {

		StringBuilder sb = new StringBuilder();
		String xmlText;
		try {
			InputStream is = new URL(url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			int cp;
			while ((cp = rd.read()) != -1) {
				sb.append((char) cp);
			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
			properties.load(PortalUtilities.class.getClassLoader()
					.getResourceAsStream("portal.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties.getProperty(key);
	}

	/*
	 * This method builds the REST URL to fetch data from server. The URL is
	 * defined in search.properties file. Also, the extraquery is appended to
	 * the URL, so that the content type to search on can be changed dynamically
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

	public static HashMap<String, String> getPortalWorkflowDetails(String path) {
		String jsonText = PortalUtilities.getDataFromURL(PortalUtilities
				.getWorkflowFeedURL() + "?field_could_path_value=" + path);
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
