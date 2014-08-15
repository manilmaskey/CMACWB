package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Notification;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;
import edu.uah.itsc.cmac.util.GITUtility;

public class NavigatorView extends CommonNavigator {

	private CommonViewer				viewer;
	private HashMap<String, Workflow>	workflows;

	public static final String			ID	= "edu.uah.itsc.cmac.NavigatorView";

	public CommonViewer getViewer() {
		return viewer;
	}

	// @Override
	// protected Object getInitialInput() {
	//
	// return projects;
	// }

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		System.out.println("AWS: " + User.awsAccessKey + "  " + User.awsSecretKey);
		if (User.awsAccessKey != null) {
			viewer = super.getCommonViewer();
			IProgressMonitor monitor = new NullProgressMonitor();
			System.out.println("Workspace Location: "
				+ ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString());
			System.out.println("Create Folder ------------ >");
			buildMyBucketsAsProjects(monitor);
			checkNotifications();
		}

		setTitleToolTip("Experiments which contain your workflows including imported workflows");
	}

	private void checkNotifications() {
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
				if (done) {
					String nodeID = workflowObject.get("nid").toString();
					PortalPost portalPost = new PortalPost();
					Notification notification = new Notification();
					notification.setClonePath(clonePath);
					notification.setPath(path);
					notification.setWorkflow(workflowName);
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
				}
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

	private boolean processNotification(String workflowName, String owner, String path, String clonePath) {
		path = "/" + path;
		path = path.replaceAll("//", "/");
		String[] paths = path.split("/");
		String bucket = paths[1];
		String message = "User '" + owner + "' has granted permission to clone workflow '" + workflowName
			+ "'. Do you want to clone it locally?";
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

	private void buildMyBucketsAsProjects(IProgressMonitor monitor) {
		workflows = Utilities.getMyWorkflows();
		if (workflows == null)
			return;
		for (String key : workflows.keySet()) {
			Workflow workflow = workflows.get(key);
			String workflowName = workflow.getWorkflowName();
			String bucket = workflow.getBucket();
			String origBucket = bucket;

			try {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(bucket);
				if (!project.exists()) {
					project.create(monitor);
				}
				if (!project.isOpen())
					project.open(monitor);
				String remotePath = "amazon-s3://.jgit@";

				if (workflow.isShared())
					remotePath = remotePath + S3.getCommunityBucketName() + "/";
				remotePath = remotePath + origBucket + "/" + workflow.getCreator() + "/" + workflowName + ".git";

				String localPath = project.getLocation().toString() + "/" + workflowName;
				System.out.println(remotePath + "\n" + localPath);
				File localPathDir = new File(localPath);
				if (!localPathDir.exists()) {
					localPathDir.mkdirs();
					GITUtility.cloneRepository(localPath, remotePath);
					project.refreshLocal(IProject.DEPTH_INFINITE, null);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private <T> String joinSet(Set<T> set, String delimiter) {
		Iterator<T> iter = set.iterator();
		String joinedString = null;
		while (iter.hasNext()) {
			T element = iter.next();
			joinedString = (joinedString == null) ? element.toString() : joinedString + delimiter + element.toString();
		}
		return joinedString;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
