package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
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

			Timer time = new Timer();
			time.schedule(new TimerTask() {

				@Override
				public void run() {
					System.out.println("Running check notification now");
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							PortalUtilities.checkNotifications();
						}
					});
				}
			}, 0, 1000 * 60);

		}

		setTitleToolTip("Experiments which contain your workflows including imported workflows");
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

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}
