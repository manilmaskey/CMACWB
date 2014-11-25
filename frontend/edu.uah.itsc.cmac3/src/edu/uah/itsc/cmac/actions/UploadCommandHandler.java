package edu.uah.itsc.cmac.actions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.json.JSONException;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;
import edu.uah.itsc.cmac.util.GITUtility;

public class UploadCommandHandler extends AbstractHandler {
	private IStructuredSelection	selection	= StructuredSelection.EMPTY;
	private static final String		REMOTE_URL	= "amazon-s3://.jgit@";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

		final Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IFile) {
			return null;
		}

		if (selection.size() == 1 && firstElement instanceof IFolder) {
			final IFolder selectedFolder = (IFolder) firstElement;
			if (selectedFolder.getParent() != selectedFolder.getProject()
				|| selectedFolder.getProject().getName().equalsIgnoreCase(S3.getCommunityBucketName()))
				return null;

			final Shell shell = new Shell(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
			shell.setText("Save a version");
			shell.setLayout(new GridLayout(2, false));

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
			GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
			gridData.heightHint = 200;
			gridData.widthHint = 300;
			commentText.setLayoutData(gridData);

			/* Submit Button */
			Button submitButton = new Button(shell, SWT.NONE);
			submitButton.setText("Submit");

			submitButton.addSelectionListener(new SelectionAdapter() {
				private void doExpensiveWork(IProgressMonitor monitor) {
					// mimic a long time job here
					for (int i = 0; i < 10; i++) {
						try {
							// give a progress bar to indicate progress
							monitor.worked(10);

							Thread.sleep(2000);
							System.out.println("step: " + i);
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				private void syncWithUI() {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getShell(), "message", "completed!");
						}
					});
				}

				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					final String versionName = versionNameText.getText();
					final String comments = commentText.getText();
					final IFolder selectedFolder = (IFolder) firstElement;
					final String repoLocalPath = selectedFolder.getParent().getLocation().toString();
					final String repoName = selectedFolder.getName();

					Job job2 = new Job("test") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							monitor.beginTask("start task", 100);

							// time consuming work here
							doExpensiveWork(monitor);
							// sync with UI
							syncWithUI();

							return Status.OK_STATUS;
						}

					};
					job2.setUser(true);
					job2.schedule();

					Job job = new Job("Uploading....") {

						protected IStatus run(IProgressMonitor monitor) {
							try {
								GITUtility.pull(repoName, repoLocalPath);
								GITUtility.commitLocalChanges(repoName, repoLocalPath, "Commit before creating tag",
									User.username, User.userEmail);
								Ref ref = GITUtility.createTag(repoName, repoLocalPath, User.username + "."
									+ versionName, comments);
								String project = selectedFolder.getProject().getName();
								String repoRemotePath = REMOTE_URL + project;
								if (ref != null)
									GITUtility.push(repoName, repoLocalPath, repoRemotePath);
								String workflowOwner = S3.getWorkflowOwner(selectedFolder.getLocation().toString());
								String path = "/" + project + "/" + workflowOwner + "/" + repoName;
								HashMap<String, String> workflowMap = PortalUtilities.getPortalWorkflowDetails(path);
								if (workflowMap == null) {
									PortalPost portal = new PortalPost();
									Workflow workflow = new Workflow(repoName, repoName, path, null, false);
									workflow.setCreator(User.username);
									workflow.setSubmittor(User.username);
									portal.post(PortalUtilities.getNodeRestPoint(), workflow.getJSON());
								}
								return Status.OK_STATUS;
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
								return new Status(Status.ERROR, "edu.uah.itsc.cmac3", "Error due to wrong URI\n"
									+ e.getMessage());
							}
							catch (JSONException e) {
								return new Status(Status.ERROR, "edu.uah.itsc.cmac3",
									"Cannot convert to JSON. There may be incomplete information in Portal\n"
										+ e.getMessage());
							}
							finally {
								monitor.done();
							}
						}
					};

					job.addJobChangeListener(new JobChangeAdapter() {
						@Override
						public void done(final IJobChangeEvent event) {

							if (!event.getResult().isOK()) {
								PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
									@Override
									public void run() {
										MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
											.getShell(), "Error uploading", event.getResult().getMessage());
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

			/* Cancel Button */
			Button cancelButton = new Button(shell, SWT.NONE);
			cancelButton.setText("Cancel");
			cancelButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					shell.close();
				}
			});

			shell.pack();
			shell.open();

		}
		return null;
	}

}