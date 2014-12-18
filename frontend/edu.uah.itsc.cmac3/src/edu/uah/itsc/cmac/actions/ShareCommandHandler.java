package edu.uah.itsc.cmac.actions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;
import edu.uah.itsc.cmac.ui.SharedWorkflowView;
import edu.uah.itsc.cmac.util.GITUtility;

public class ShareCommandHandler extends AbstractHandler {
	private IStructuredSelection	selection	= StructuredSelection.EMPTY;
	private static final String		REMOTE_URL	= "amazon-s3://.jgit@";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

		Object object = selection.getFirstElement();
		// Job job = new Job("Sharing...") {
		// protected IStatus run(final IProgressMonitor monitor) {

		if (selection.size() == 1) {

			final Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IFile) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
					"You can only share workflows!");
			}
			else if (firstElement instanceof IFolder) {
				final IFolder selectedFolder = (IFolder) firstElement;
				IFolder gitFolder = selectedFolder.getFolder(".git");
				if (!gitFolder.exists()) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
						"You can only share tracking workflows!");
					return null;
				}

				if (SharedWorkflowView.isSharedWorkflowByOther(selectedFolder.getProject().getName(),
					selectedFolder.getName())) {
					MessageDialog
						.openError(
							Display.getDefault().getActiveShell(),
							"Error",
							"A workflow with this name is already shared in this experiment. Please choose another workflow name or another experiment");
					return null;
				}

				final S3 s3 = new S3();
				final String path = "/" + selectedFolder.getProject().getName() + "/" + User.username + "/"
					+ selectedFolder.getName();
				final Shell shell = new Shell(Display.getDefault().getActiveShell());
				shell.setText("Workflow Settings");
				shell.setLayout(new GridLayout(2, false));
				Label title = new Label(shell, SWT.NONE);
				title.setText("Title : ");
				final Text titleText = new Text(shell, SWT.BORDER);

				titleText.setLayoutData(new GridData(SWT.FILL, 20, true, false));

				Label description = new Label(shell, SWT.NONE);
				description.setText("Description : ");
				final Text descText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
				GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
				gridData.heightHint = 100;
				gridData.widthHint = 300;
				descText.setLayoutData(gridData);
				/*
				 * Shreedhan Add keyword label and keyword text
				 */
				Label keywordLabel = new Label(shell, SWT.NONE);
				keywordLabel.setText("Keywords");
				final Text keywordText = new Text(shell, SWT.BORDER);
				keywordText.setLayoutData(new GridData(SWT.FILL, 20, true, false));

				/* Version Name Label */
				Label versionNameLabel = new Label(shell, SWT.NONE);
				versionNameLabel.setText("Version Name");

				/* Version Name Text */
				final Text versionNameText = new Text(shell, SWT.BORDER);
				versionNameText.setLayoutData(new GridData(SWT.FILL, 20, true, false));

				Label spaceLabel = new Label(shell, SWT.NONE);
				spaceLabel.setText("");

				Label noteLabel = new Label(shell, SWT.NONE);
				noteLabel.setText("(Note: username will be added to the version name during search)");

				/* Comment Label */
				Label versionCommentLabel = new Label(shell, SWT.NONE);
				versionCommentLabel.setText("Comments");

				/* Comment Text */
				final Text commentText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);

				commentText.setLayoutData(gridData);

				Button ok = new Button(shell, SWT.PUSH);
				ok.setText("  OK  ");
				Button cancel = new Button(shell, SWT.PUSH);
				cancel.setText("Cancel");
				final String nodeID;
				final boolean isShared;
				HashMap<String, String> nodeMap = PortalUtilities.getPortalWorkflowDetails(path);
				if (nodeMap != null) {
					nodeID = nodeMap.get("nid");
					isShared = Integer.parseInt(nodeMap.get("isShared")) > 0 ? true : false;
					titleText.setText((String) nodeMap.get("title"));
					keywordText.setText((String) nodeMap.get("keywords"));
					descText.setText(((String) nodeMap.get("description")).replaceAll("\\<.*?\\>", ""));
				}
				else {
					nodeID = null;
					isShared = false;
				}

				ok.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						final String title = titleText.getText();
						final String desc = descText.getText();
						final String keyword = keywordText.getText();
						final String versionName = versionNameText.getText();
						final String comments = commentText.getText();

						/****************************/
						Job job = new Job("Sharing...") {
							protected IStatus run(IProgressMonitor monitor) {
								monitor.beginTask("Sharing..", 100);
								try {
									PortalPost portalPost = new PortalPost();

									final Workflow workflow = new Workflow(title, desc, keyword);
									workflow.setPath(path);
									workflow.setShared(true);
									workflow.setSubmittor(User.username);

									JSONObject workflowObj = workflow.getJSON();
									if (nodeID != null) {
										portalPost.put(PortalUtilities.getNodeRestPoint() + "/" + nodeID, workflowObj);
									}
									else {
										workflow.setCreator(User.username);
										HttpResponse response = portalPost.post(PortalUtilities.getNodeRestPoint(),
											workflow.getJSON());
										if (response.getStatusLine().getStatusCode() != 200) {
											return new Status(Status.ERROR, "edu.uah.itsc.cmac3",
												"Invalid return status from Portal. There may be incomplete information in Portal\n"
													+ response.getStatusLine());
										}

									}
									portalPost.runCron();

									// commit and push before sharing
									String repoName = selectedFolder.getName();
									String bucketName = selectedFolder.getProject().getName();
									String repoLocalPath = selectedFolder.getParent().getLocation().toString();
									String repoRemotePath = REMOTE_URL;
									if (isShared)
										repoRemotePath = repoRemotePath + S3.getCommunityBucketName() + "/";
									repoRemotePath = repoRemotePath + bucketName;

									GITUtility.pull(repoName, repoLocalPath);
									GITUtility.commitLocalChanges(repoName, repoLocalPath,
										"Commit before creating tag", User.username, User.userEmail);
									Ref ref = GITUtility.createTag(repoName, repoLocalPath, User.username + "."
										+ versionName, comments);
									if (ref != null)
										GITUtility.push(repoName, repoLocalPath, repoRemotePath);

									s3.shareGITFolder(selectedFolder);

									Display.getDefault().asyncExec(new Runnable() {

										@Override
										public void run() {
											SharedWorkflowView view = (SharedWorkflowView) PlatformUI.getWorkbench()
												.getActiveWorkbenchWindow().getActivePage()
												.findView("edu.uah.itsc.cmac.ui.SharedWorkflowView");
											view.refreshCommunityResource();
										}
									});
									IProject communityProject = ResourcesPlugin.getWorkspace().getRoot()
										.getProject(S3.getCommunityBucketName());
									communityProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
								}
								catch (CoreException e) {
									return new Status(Status.ERROR, "edu.uah.itsc.cmac3",
										"Cannot refresh the shared workflows view\n" + e.getMessage());
								}
								catch (JSONException e) {
									return new Status(Status.ERROR, "edu.uah.itsc.cmac3",
										"Cannot retreive JSON Object\n" + e.getMessage());
								}
								catch (GitAPIException e) {
									return new Status(Status.ERROR, "edu.uah.itsc.cmac3",
										"Error while performing following git operation\n" + e.getMessage());
								}
								catch (IOException e) {
									return new Status(Status.ERROR, "edu.uah.itsc.cmac3",
										"Error while performing following git IO operation\n" + e.getMessage());
								}
								catch (URISyntaxException e) {
									return new Status(Status.ERROR, "edu.uah.itsc.cmac3", "Error in URI syntax\n"
										+ e.getMessage());
								}

								// s3.addWorkflowSharePolicy("cmac_collaborators", "shared_workflow",
								// workflowPath);

								monitor.done();
								return Status.OK_STATUS;
							}
						};

						job.addJobChangeListener(new JobChangeAdapter() {
							@Override
							public void done(final IJobChangeEvent event) {

								if (!event.getResult().isOK()) {
									PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
										@Override
										public void run() {
											MessageDialog.openError(PlatformUI.getWorkbench()
												.getActiveWorkbenchWindow().getShell(), "Error Sharing", event
												.getResult().getMessage());
										}
									});
								}
							}

						});
						job.setUser(true);
						job.schedule();
						shell.close();

					}
				});
				cancel.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent event) {
						shell.close();
					}
				});

				shell.pack();
				shell.open();

			}

		}
		// monitor.done();
		// return Status.OK_STATUS;
		// }
		// };
		// job.setUser(true);
		// job.schedule();

		return object;
	}

	private void addSpanData(Control comp) {
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		comp.setLayoutData(data);
	}

	/**
	 * Returns the active page or null if no page is available. A page is a composition of views and editors which are
	 * meant to show at the same time.
	 * 
	 * @return The active page.
	 */
	public static IWorkbenchPage getPage() throws Exception {
		final IWorkbenchPage page[] = new IWorkbenchPage[] { null };
		final String errorMessages[] = new String[] { "" };

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbench wb = PlatformUI.getWorkbench();
					IWorkbenchWindow wbWindow = wb.getActiveWorkbenchWindow();
					page[0] = wbWindow.getActivePage();
				}
				catch (Exception e) {
					errorMessages[0] = e.toString();
				}
			}
		});
		if (errorMessages[0].length() > 0) {
			throw new Exception(errorMessages[0]);
		}
		return page[0];
	}

}