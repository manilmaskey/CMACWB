/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;
import org.json.JSONException;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;
import edu.uah.itsc.cmac.util.GITUtility;

/**
 * @author sshrestha
 * 
 */
public class RenameHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		final Object firstElement = selection.getFirstElement();

		if (firstElement instanceof IFolder) {
			final IFolder folder = (IFolder) firstElement;
			try {
				folder.refreshLocal(IFolder.DEPTH_INFINITE, null);
			}
			catch (CoreException e1) {
				e1.printStackTrace();
				return null;
			}
			String oldName = folder.getName();

			String workflowOwner = S3.getWorkflowOwner(folder.getLocation().toString());
			if (!workflowOwner.equals(User.username)) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"You can only rename workflows which you own. This workflow is owned by '" + workflowOwner + "'");
				return null;
			}

			String path = "/" + folder.getProject().getName() + "/" + workflowOwner + "/" + folder.getName();
			HashMap<String, String> workflowMap = PortalUtilities.getPortalWorkflowDetails(path);

			if (workflowMap != null && Integer.parseInt(workflowMap.get("isShared")) == 1) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"This workflow is already shared and cannot be renamed.");
				return null;
			}

			InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), "Rename", "Enter new name",
				folder.getName(), new IInputValidator() {

					@Override
					public String isValid(String newText) {
						if (newText.isEmpty())
							return "Name cannot be empty";
						if (newText.equalsIgnoreCase(folder.getName()))
							return "Please provide a new name";
						return null;
					}
				});
			if (dialog.open() == InputDialog.OK) {
				String newName = dialog.getValue();
				/*
				 * SharedWorkflowView.refreshCommunityResource(); if
				 * (SharedWorkflowView.isSharedWorkflowByOther(folder.getProject().getName(), newName)) { MessageDialog
				 * .openError( Display.getDefault().getActiveShell(), "Error",
				 * "A workflow with this name is already shared in this experiment."); return null; }
				 */
				try {

					RenameResourceChange rrChange = new RenameResourceChange(folder.getFullPath(), newName);
					rrChange.perform(new NullProgressMonitor());
					if (workflowMap == null)
						return null;

					String sourceBucketName = null;
					boolean isShared = Integer.parseInt(workflowMap.get("isShared")) > 0 ? true : false;
					String newPath = "/" + folder.getProject().getName() + "/" + User.username + "/" + newName;
					Workflow workflow = new Workflow(workflowMap.get("title"), workflowMap.get("description"), newPath,
						workflowMap.get("keywords"), isShared);
					PortalPost post = new PortalPost();
					String nodeID = workflowMap.get("nid");
					post.put(PortalUtilities.getNodeRestPoint() + "/" + nodeID, workflow.getJSON());
					System.out.println("workflow" + workflow.getJSON());
					String newRemotePath = "amazon-s3://.jgit@";
					String workflowPath = User.username + "/" + oldName + ".git" + "/";
					String newWorkflowPath = User.username + "/" + newName + ".git";

					sourceBucketName = folder.getProject().getName();

					newRemotePath = newRemotePath + folder.getProject().getName() + "/" + User.username + "/" + newName
						+ ".git";
					GITUtility.modifyRemote(newName, folder.getParent().getLocation().toString(), newRemotePath);

					S3 s3 = new S3();
					// Move repository from old location to new location
					s3.copyAllFilesInS3(sourceBucketName, workflowPath, sourceBucketName, newWorkflowPath);
					s3.deleteFolderInS3(sourceBucketName, workflowPath);
				}
				catch (CoreException e) {
					e.printStackTrace();
					return null;
				}
				catch (NoFilepatternException e) {
					e.printStackTrace();
				}
				catch (GitAPIException e) {
					e.printStackTrace();
				}
				catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		return null;
	}
}
