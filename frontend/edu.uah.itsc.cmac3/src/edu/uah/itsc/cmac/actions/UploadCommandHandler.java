package edu.uah.itsc.cmac.actions;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

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

		Object object = selection.getFirstElement();
		Job job = new Job("Uploading...") {
			protected IStatus run(IProgressMonitor monitor) {
				if (selection.size() == 1) {

					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof IFile) {
						return Status.CANCEL_STATUS;
					}
					if (firstElement instanceof IFolder) {
						IFolder selectedFolder = (IFolder) firstElement;
						if (selectedFolder.getParent() != selectedFolder.getProject()
							|| selectedFolder.getProject().getName().equalsIgnoreCase(S3.getCommunityBucketName()))
							return Status.CANCEL_STATUS;

						String repoName = selectedFolder.getName();
						String repoLocalPath = selectedFolder.getParent().getLocation().toString();
						String project = selectedFolder.getProject().getName();
						String repoRemotePath = REMOTE_URL + project;

						try {
							GITUtility.pull(repoName, repoLocalPath);
							GITUtility.commitLocalChanges(repoName, repoLocalPath, "Commit for push", User.username,
								User.userEmail);
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
						}
						catch (final Exception e) {
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
										e.getMessage());
								}
							});
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}

					}
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return object;
	}
}