/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.ui.NavigatorView;

/**
 * @author sshrestha
 * 
 */
public class DeleteCommandHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean userConfirmation = MessageDialog
				.openConfirm(
						Display.getDefault().getActiveShell(),
						"Warning! Delete resources from cloud!!",
						"Are you sure you want to delete this resource from cloud?\nNote that this will also delete the shared resource.");
		if (userConfirmation) {
			IStructuredSelection selection = StructuredSelection.EMPTY;
			String path = "";

			final IFolder selectedFolder;

			// DeleteObjectsRequest multiObjectDeleteRequest = new
			// DeleteObjectsRequest(
			// s3.getBucketName());
			// List<KeyVersion> keys = new ArrayList<KeyVersion>();

			selection = (IStructuredSelection) HandlerUtil
					.getCurrentSelectionChecked(event);
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IFile) {
				Object[] selectedFiles = selection.toArray();
				selectedFolder = (IFolder) ((IFile) selectedFiles[0])
						.getParent();

				deleteFiles(selectedFiles);
				try {
					selectedFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (firstElement instanceof IFolder) {
				try {
					selectedFolder = (IFolder) firstElement;
					final IResource[] allFiles;
					allFiles = selectedFolder.members();
					Job job = new Job("Deleting...") {
						protected IStatus run(IProgressMonitor monitor) {
							deleteFiles(allFiles);
							monitor.worked(40);
							deleteFolder(selectedFolder);
							monitor.worked(10);
							deleteWorkflowFromPortal(selectedFolder
									.getFullPath().toString());
							monitor.worked(25);
							
							try {
								selectedFolder.delete(true, null);
							} catch (CoreException e) {
								// TODO Auto-generated catch block
								MessageDialog.openError(Display.getDefault().getActiveShell(), "Could not delete folder",
										e.getMessage());
								e.printStackTrace();
								return Status.CANCEL_STATUS;
							}
							monitor.done();
			        		return Status.OK_STATUS;
						}
					};
					job.setUser(true);
					job.schedule();
				} catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		} else
			return null;
	}

	private void deleteWorkflowFromPortal(String path) {
		HashMap<String, String> nodeMap = PortalUtilities
				.getPortalWorkflowDetails(path);

		String nodeID;
		if (nodeMap != null) {
			nodeID = nodeMap.get("nid");
		} else
			nodeID = null;
		if (nodeID != null && nodeID.length() > 0) {
			PortalPost portalPost = new PortalPost();
			String response = portalPost.delete(PortalUtilities
					.getNodeRestPoint() + "/" + nodeID);
			System.out.println(response);
		}
	}

	private void deleteFolder(IFolder folder) {
		S3 s3 = new S3();
		AmazonS3 amazonS3Service = s3.getAmazonS3Service();

		String key = folder.getFullPath().toString();
		key = key.replaceFirst("^/" + s3.getBucketName(), "");
		key = key.replaceAll("^/", "");
		key = key + "_$folder$";

		try {
			amazonS3Service.deleteObject(new DeleteObjectRequest(s3
					.getBucketName(), key));
			amazonS3Service.deleteObject(new DeleteObjectRequest(s3
					.getCommunityBucketName(), key));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param s3
	 * @param amazonS3Service
	 * @param selectedFiles
	 */
	private void deleteFiles(Object[] selectedFiles) {
		S3 s3 = new S3();
		AmazonS3 amazonS3Service = s3.getAmazonS3Service();
		for (Object selectedObject : selectedFiles) {
			IFile selectedFile = (IFile) selectedObject;
			// path = path
			// + ","
			// + (selectedFile.isLinked() ? selectedFile
			// .getFullPath().toString()
			// .replaceFirst("^L", "") : selectedFile
			// .getFullPath());
			String key = selectedFile.isLinked() ? selectedFile.getFullPath()
					.toString().replaceFirst("^L", "") : selectedFile
					.getFullPath().toString();
			key = key.replaceFirst("^/" + s3.getBucketName(), "");
			key = key.replaceAll("^/", "");
			try {
				amazonS3Service.deleteObject(new DeleteObjectRequest(s3
						.getBucketName(), key));
				amazonS3Service.deleteObject(new DeleteObjectRequest(s3
						.getCommunityBucketName(), key));
				selectedFile.delete(true, null);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
