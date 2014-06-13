/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.util.FileUtility;
import edu.uah.itsc.cmac.util.GITUtility;
import edu.uah.itsc.cmac.util.PropertyUtility;

/**
 * @author sshrestha
 * 
 */
public class ImportWorkflowHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands. ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Importing workflow... ");
		IStructuredSelection selection = StructuredSelection.EMPTY;
		IFolder selectedFolder = null;
		IFolder bucketFolder = null;
		String path = "";
		try {

			selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IFile) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
					"You can only import folders!");
				return null;
			}
			else if (firstElement instanceof IFolder) {
				selectedFolder = (IFolder) firstElement;
				bucketFolder = (IFolder) selectedFolder.getParent().getParent();
				if (!(bucketFolder.getParent() instanceof IProject)) {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
						"You can only import workflow!");
					return null;
				}
				path = selectedFolder.getFullPath().toString();
			}

			String copyFromFolderPath = path;
			String folderToCopy = "";

			folderToCopy = copyFromFolderPath;

			// copyFromFolderPath = copyFromFolderPath.replaceFirst(s3.getCommunityBucketName(), "");
			copyFromFolderPath = copyFromFolderPath.replaceAll("^/+", "");
			// Replace first three words separated by "/" with empty space
			folderToCopy = folderToCopy.replaceFirst("^/[\\w|-]+/[\\w|-]+/[\\w|-]+/", "");
			System.out.println("folderpath: " + folderToCopy);

			final String copyFolderPath = copyFromFolderPath;
			final String bucketName = bucketFolder.getName();
			final String remotePath = "amazon-s3://.jgit@" + copyFolderPath + ".git";
			final String localPath = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName).getLocation()
				+ "/" + User.username + "/" + folderToCopy;
			final String repoOwner = selectedFolder.getParent().getName();
			System.out.println(remotePath + "\n" + localPath);

			// We do not download folders now. We have to clone the repository locally
			Job job = new Job("Importing..") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					// buildTree(s3, copyFolderPath, folderCopy,
					// ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName));
					try {
						GITUtility.cloneRepository(localPath, remotePath);
						setOwnerProperty(localPath, repoOwner);

						IFolder userFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName)
							.getFolder(User.username);
						userFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
					}
					catch (Exception e) {
						Display.getDefault().asyncExec(new Runnable() {

							@Override
							public void run() {
								MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
									"Error while importing");
							}
						});
					}
					monitor.done();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void setOwnerProperty(final String localPath, final String repoOwner) throws IOException {
		String workflowPropertyFileName = localPath + "/.cmacworkflow";
		String gitIgnoreFileName = localPath + "/.gitignore";
		File propFile = new File(workflowPropertyFileName);
		if (!propFile.exists())
			propFile.createNewFile();

		File gitIgnoreFile = new File(gitIgnoreFileName);
		if (!gitIgnoreFile.exists()) {
			gitIgnoreFile.createNewFile();
			FileUtility.writeTextFile(gitIgnoreFileName, ".cmacworkflow");
		}

		PropertyUtility propUtil = new PropertyUtility(workflowPropertyFileName);
		propUtil.setValue("owner", repoOwner);
	}

}
