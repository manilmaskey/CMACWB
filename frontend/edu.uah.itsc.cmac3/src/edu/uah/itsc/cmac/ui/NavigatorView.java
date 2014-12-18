package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
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

			time.schedule(new TimerTask() {
				@Override
				public void run() {
					PortalUtilities.getUserList();
				}
			}, 0);

		}

		setTitleToolTip("Experiments which contain your workflows including imported workflows");

		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {

				ISelection selection = event.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object obj = ((IStructuredSelection) selection).getFirstElement();
					if (obj instanceof IFile) {
						final IFile file = (IFile) obj;
						String key = null;
						if (file.isLinked()) {
							try {
								String needS3Download = file.getPersistentProperty(new QualifiedName(
									"edu.uah.itsc.cmac3.needS3Download", "needS3Download"));
								if (!needS3Download.equals("true"))
									return;
								key = file.getPersistentProperty(new QualifiedName("edu.uah.itsc.cmac3.s3Location",
									"s3Location"));

							}
							catch (CoreException e1) {
								e1.printStackTrace();
								return;
							}
							final String fileKey = key;
							Job job = new Job("Downloading") {
								@Override
								protected IStatus run(IProgressMonitor monitor) {
									Display.getDefault().asyncExec(new Runnable() {

										@Override
										public void run() {
											MessageDialog
												.openInformation(Display.getDefault().getActiveShell(),
													"Download in progress...",
													"The file is being downloaded. The file will appear once it is downloaded");
										}
									});
									monitor.beginTask("Downloading file", 100);
									S3 s3 = new S3();
									String large_bucket_name = edu.uah.itsc.cmac.Utilities.getKeyValueFromPreferences(
										"s3", "large_bucket_name");
									// String fileKey = file.getProject().getName() + "/" + User.username + "/"
									// + file.getProjectRelativePath().toString();
									monitor.worked(10);
									try {
										s3.downloadFile(file.getLocation().toString(), large_bucket_name, fileKey);
										monitor.worked(100);
									}
									catch (IOException e) {
										e.printStackTrace();
										return Status.CANCEL_STATUS;
									}
									monitor.done();
									return Status.OK_STATUS;
								}
							};

							job.addJobChangeListener(new JobChangeAdapter() {
								@Override
								public void done(IJobChangeEvent event) {
									if (event.getResult().isOK()) {
										try {
											file.getParent().refreshLocal(IFolder.DEPTH_INFINITE, null);
											PlatformUI.getWorkbench().getEditorRegistry()
												.getDefaultEditor(file.getName());
										}
										catch (CoreException e) {
											e.printStackTrace();
										}
									}
								}
							});
							try {
								file.delete(true, null);
							}
							catch (CoreException e) {
								e.printStackTrace();
							}
							job.setUser(true);
							job.schedule();

						}
					}
				}
			}
		});

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
