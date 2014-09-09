package edu.uah.itsc.cmac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/*
 * This document is a part of the source code and related artifacts for CMAC Project funded by NASA Copyright © 2013,
 * University of Alabama in Huntsville You may not use this file except in compliance with University of Alabama in
 * Huntsville License. Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the license.
 */

public class Utilities {
	private static String		preferenceFile	= "edu.uah.itsc.cmac.preferences";
	private static Preferences	preferences		= null;

	public static String getKeyValueFromPreferences(String node, String key) {
		if (preferences == null)
			preferences = InstanceScope.INSTANCE.getNode(preferenceFile);
		Preferences prefNode = preferences.node(node);
		String value = prefNode.get(key, null);
		return value;
	}

	public static boolean containsAll(String node, ArrayList<String> list) {
		if (preferences == null)
			preferences = InstanceScope.INSTANCE.getNode(preferenceFile);
		Preferences prefNode = preferences.node(node);
		for (String key : list) {
			String value = prefNode.get(key, null);
			if (value == null)
				return false;
		}
		return true;
	}

	public static void savePreferences(String node, HashMap<String, String> map) throws BackingStoreException {
		if (preferences == null)
			preferences = InstanceScope.INSTANCE.getNode(preferenceFile);
		Preferences prefNode = preferences.node(node);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			prefNode.put(key, value);
		}
		preferences.flush();

	}

	public static boolean isPreferenceSet() {
		ArrayList<String> portalKeys = new ArrayList<String>();
		portalKeys.add("experiment_url");
		portalKeys.add("node_rest_url");
		portalKeys.add("notification_url");
		portalKeys.add("portal_cron_url");
		portalKeys.add("portal_domain");
		portalKeys.add("portal_login_url");
		portalKeys.add("portal_rest_url");
		portalKeys.add("portal_url");
		portalKeys.add("portal_user_url");
		portalKeys.add("search_url");
		portalKeys.add("term_rest_url");
		portalKeys.add("token_url");
		portalKeys.add("user_list_url");
		portalKeys.add("workflow_url");
		if (!containsAll("portal", portalKeys))
			return false;

		ArrayList<String> s3Keys = new ArrayList<String>();
		s3Keys.add("aws_admin_access_key");
		s3Keys.add("aws_admin_secret_key");
		s3Keys.add("aws_user_id");
		s3Keys.add("backend_execute_url");
		s3Keys.add("backend_execute_url_suffix");
		s3Keys.add("community_bucket_name");
		if (!containsAll("s3", s3Keys))
			return false;

		return true;
	}
}
