/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.NavigatorView;
import edu.uah.itsc.cmac.util.GITUtility;

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
			// Object object = selection.getFirstElement();
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final NavigatorView view = (NavigatorView) page.findView("edu.uah.itsc.cmac.NavigatorView");
			final S3 s3 = view.getS3();

			String copyFromFolderPath = path;
			String folderToCopy = "";

			folderToCopy = copyFromFolderPath;

			copyFromFolderPath = copyFromFolderPath.replaceFirst(s3.getCommunityBucketName(), "");
			copyFromFolderPath = copyFromFolderPath.replaceAll("^/+", "");
			// Replace first three words separated by "/" with empty space
			folderToCopy = folderToCopy.replaceFirst("^/[\\w|-]+/[\\w|-]+/[\\w|-]+/", "");
			System.out.println("folderpath: " + folderToCopy);

			final String copyFolderPath = copyFromFolderPath;
			final String folderCopy = folderToCopy;
			final String bucketName = bucketFolder.getName();
			String remotePath = "amazon-s3://.jgit@" + copyFolderPath + ".git";
			String localPath = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName).getLocation()+ "/" + User.username + "/" + folderToCopy;
			System.out.println(remotePath + "\n" + localPath);
			
			// We do not download folders now. We have to clone the repository locally
			GITUtility.cloneRepository(localPath, remotePath);
			IFolder userFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName).getFolder(User.username);
			userFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
//			Job job = new Job("Importing..") {
//				@Override
//				protected IStatus run(IProgressMonitor monitor) {
//					buildTree(s3, copyFolderPath, folderCopy,
//						ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName));
//					monitor.done();
//					return Status.OK_STATUS;
//				}
//			};
//			job.setUser(true);
//			job.schedule();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private void buildTree(S3 s3, String copyFromFolderPath, String folderToCopy, IResource resource) {
		System.out.println(copyFromFolderPath);
		System.out.println(folderToCopy);
		IFolder userFolder = ((IProject) resource).getFolder(User.username);
		IFolder folderToCreate = userFolder.getFolder(folderToCopy);
		if (!folderToCreate.exists()) {
			createFolderPath(userFolder, folderToCopy);
		}
		if (folderToCreate.exists()) {
			downloadFolder(s3, copyFromFolderPath, folderToCreate);
		}

	}

	private void downloadFolder(S3 s3, String copyFromFolderPath, IFolder copyToFolder) {
		AmazonS3 amazonS3Service = s3.getAmazonS3Service();

		ListObjectsRequest lor = new ListObjectsRequest();
		lor.setBucketName(s3.getCommunityBucketName());
		lor.setDelimiter(s3.getDelimiter());
		lor.setPrefix(copyFromFolderPath.replaceAll("$/+", "") + "/");

		ObjectListing filteredObjects = amazonS3Service.listObjects(lor);

		for (S3ObjectSummary objectSummary : filteredObjects.getObjectSummaries()) {
			String currentResource = objectSummary.getKey();
			String[] fileNameArray = currentResource.split("/");
			String fileName = fileNameArray[fileNameArray.length - 1];
			if (currentResource.indexOf("_$folder$") > 0) {

			}
			else {
				String fullFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
					+ java.io.File.separator + copyToFolder.getProject().getName() + java.io.File.separator
					+ User.username + java.io.File.separator + copyToFolder.getName() + java.io.File.separator
					+ fileName;
				System.out.println("Downloading file " + currentResource);
				System.out.println("fullFilePath: " + fullFilePath);

				s3.downloadFile(s3.getCommunityBucketName(), currentResource, fullFilePath);

			}

		}
		try {
			copyToFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
		}
		catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createFolderPath(IFolder folder, String folderToAdd) {
		if (folderToAdd.indexOf("/") <= 0) {
			IFolder newFolder = folder.getFolder(folderToAdd);
			if (!newFolder.exists()) {
				try {
					newFolder.create(true, true, null);
				}
				catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
			while (folderToAdd.indexOf("/") > 0) {
				folderToAdd = folderToAdd.substring(folderToAdd.indexOf("/") + 1);

			}
	}

}
