package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Experiment;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;

public class Utilities {
	private S3					s3;
	private static final int	TEMP_DIR_ATTEMPTS	= 10000;

	public Utilities() {
		s3 = new S3(User.awsAccessKey, User.awsSecretKey);
	}

	public static File createTempDir(String prefix, boolean random){
		if (random)
			return createTempDirRandom(prefix);
		else
			return createTempDir(prefix);
	}
	
	private static File createTempDir(String prefix){
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		File tempDir = new File(baseDir, prefix);
		if (tempDir.exists())
			return tempDir;
		if (tempDir.mkdir())
			return tempDir;
		else return null;
	}
	
	
	private static File createTempDirRandom(String prefix) {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, prefix + baseName + counter);
			if (tempDir.mkdir())
				return tempDir;
		}
		throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried "
			+ baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}

	public static HashMap<String, Workflow> getWorkflows(String shared, String uid) {
		HashMap<String, Workflow> workflowsMap = null;
		String url = PortalUtilities.getWorkflowFeedURL();
		if (shared == null)
			url = url + "?field_is_shared=All";
		else
			url = url + "?field_is_shared=" + shared;

		if (uid == null)
			url = url + "&field_creator_uid=All";
		else
			url = url + "&field_creator_uid=" + uid;

		String jsonText = PortalUtilities.getDataFromURL(url);
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
			workflowsMap = new HashMap<String, Workflow>();
			for (int i = 0; i < workFlowArray.size(); i++) {
				JSONObject workflowObj = (JSONObject) workFlowArray.get(i);
				workflowObj = (JSONObject) workflowObj.get("workflow");
				Workflow workflow = new Workflow();
				workflow.setPath(workflowObj.get("path").toString());
				workflow.setTitle(workflowObj.get("title").toString());
				workflow.setDescription(workflowObj.get("description").toString());
				workflow.setKeywords(workflowObj.get("keywords").toString());
				workflow.setCreator(workflowObj.get("creator").toString());
				workflow.setSubmittor(workflowObj.get("submittor").toString());
				workflow.setShared(Integer.parseInt(workflowObj.get("isShared").toString()) > 0);
				workflowsMap.put(workflow.getTitle(), workflow);
			}
		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Unable to parse json object");
			return null;
		}
		return workflowsMap;
	}

	public static HashMap<String, Workflow> getMyWorkflows() {
		return getWorkflows(null, User.portalUserID);
	}

	public static HashMap<String, Workflow> getSharedWorkflows() {
		return getWorkflows("1", null);
	}

	public static HashMap<String, Workflow> getAllWorkflows() {
		return getWorkflows(null, null);
	}

	public static HashMap<String, Experiment> getAllExperiments() {
		HashMap<String, Experiment> experimentsMap = new HashMap<String, Experiment>();
		String jsonText = PortalUtilities.getDataFromURL(PortalUtilities.getExperimentFeedURL());
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
			for (int i = 0; i < experimentArray.size(); i++) {
				JSONObject experimentObj = (JSONObject) experimentArray.get(i);
				experimentObj = (JSONObject) experimentObj.get("experiment");
				Experiment experiment = new Experiment();
				experiment.setCreator(experimentObj.get("creator").toString());
				experiment.setCreatorID(experimentObj.get("creatorID").toString());
				experiment.setDescription(experimentObj.get("description").toString());
				experiment.setTitle(experimentObj.get("title").toString());
				experimentsMap.put(experiment.getTitle(), experiment);
			}

			return experimentsMap;
		}
		catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Unable to parse json object");
			return null;
		}
	}

	public static Set<String> getOtherBuckets() {
		Set<String> myWorkflowBuckets = new HashSet<String>();
		HashMap<String, Workflow> workflows = getMyWorkflows();
		if (workflows != null)
			for (String key : workflows.keySet()) {
				Workflow workflow = workflows.get(key);
				myWorkflowBuckets.add(workflow.getBucket());
			}
		Set<String> allBuckets = getAllExperiments().keySet();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		Set<String> localProjects = new HashSet<String>();
		for (IProject project : projects) {
			localProjects.add(project.getName());
		}
		allBuckets.removeAll(myWorkflowBuckets);
		allBuckets.removeAll(localProjects);
		return allBuckets;
	}

	public static void deleteRecursive(File localPath) {
		if (localPath == null)
			return;
		File[] files = localPath.listFiles();
		if (files.length == 0){
			localPath.delete();
			return;
		}
		for (File file : files){
			if (file.isDirectory())
				deleteRecursive(file);
			else
				file.delete();
		}
		localPath.delete();
	}
}
