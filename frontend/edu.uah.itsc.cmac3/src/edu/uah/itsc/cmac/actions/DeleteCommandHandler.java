/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.http.HttpResponse;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;

/**
 * @author sshrestha
 * 
 */
public class DeleteCommandHandler extends AbstractHandler {
	private S3			s3;
	private AmazonS3	amazonS3Service;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands. ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean userConfirmation = MessageDialog
			.openConfirm(Display.getDefault().getActiveShell(), "Warning! Delete resources from cloud!!",
				"Are you sure you want to delete this resource from cloud?\nNote that this will also delete the shared resource.");
		if (!userConfirmation)
			return null;
		else {
			try {
				s3 = new S3();
				amazonS3Service = s3.getAmazonS3Service();
				final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
					.getCurrentSelectionChecked(event);
				deleteFromS3(selection);
				String bucketName = getBucketName(selection);
				Object[] selectedObjects = selection.toArray();

				for (Object selectedObject : selectedObjects) {
					if (selectedObject instanceof IFolder) {
						String path = ((IFolder) selectedObject).getFullPath().toString();
						deleteWorkflowFromPortal(path);
						((IFolder) selectedObject).delete(true, null);
						((IFolder) selectedObject).getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
					}
					else if (selectedObject instanceof IProject) {
						deleteBucketFromCloud(bucketName);
						deleteBucketFromPortal(bucketName);
						((IProject) selectedObject).delete(true, null);
					}
					else if (selectedObject instanceof IFile) {
						((IFile) selectedObject).delete(true, null);
					}

				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * @param selection
	 * @return
	 */
	private void deleteFromS3(final IStructuredSelection selection) {
		ArrayList<String> allFiles = getAllFiles(selection);
		System.out.println(allFiles);
		String bucketName = getBucketName(selection);
		s3.deleteFilesFromBucket(allFiles, bucketName);
		allFiles = getAllFilesCommunity(selection);
		System.out.println(allFiles);
		s3.deleteFilesFromBucket(allFiles, S3.getCommunityBucketName());
	}

	private void deleteBucketFromPortal(String bucketName) {
		HashMap<String, String> nodeMap = PortalUtilities.getPortalExperimentDetails(bucketName);
		String nodeID;
		if (nodeMap != null)
			nodeID = nodeMap.get("nid");
		else
			nodeID = null;
		if (nodeID != null && nodeID.length() > 0) {
			PortalPost portalPost = new PortalPost();
			HttpResponse response = portalPost.delete(PortalUtilities.getNodeRestPoint() + "/" + nodeID);
			if (response.getStatusLine().getStatusCode() != 200)
				System.out.println("Error deleting experiment from Portal");
			System.out.println(response);
		}
	}

	private void deleteBucketFromCloud(String bucketName) {
		amazonS3Service.deleteBucket(bucketName);
	}

	private String getBucketName(IStructuredSelection selection) {
		IResource resource = (IResource) selection.getFirstElement();
		return resource.getProject().getName();
	}

	private void deleteWorkflowFromPortal(String path) {
		HashMap<String, String> nodeMap = PortalUtilities.getPortalWorkflowDetails(path);

		String nodeID;
		if (nodeMap != null) {
			nodeID = nodeMap.get("nid");
		}
		else
			nodeID = null;
		if (nodeID != null && nodeID.length() > 0) {
			PortalPost portalPost = new PortalPost();
			HttpResponse response = portalPost.delete(PortalUtilities.getNodeRestPoint() + "/" + nodeID);
			System.out.println(response);
		}
	}

	private String getS3File(IFile file, IProject experiment) {
		String key = file.getFullPath().toString();
		if (file.isLinked())
			key = file.getFullPath().toString().replaceFirst("^L", "");
		key = key.replaceFirst("^/" + experiment.getName(), "");
		key = key.replaceAll("^/", "");
		return key;
	}

	private String getS3Folder(IFolder folder, IProject experiment) {
		String key = folder.getFullPath().toString();
		key = key.replaceFirst("^F", "");
		key = key.replaceFirst("^/" + experiment.getName(), "");
		key = key.replaceAll("^/", "");
		return key;

	}

	private ArrayList<String> getAllFiles(IResource resource) {
		ArrayList<String> files = new ArrayList<String>();
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			files.add(getS3File(file, file.getProject()));
		}
		else {
			IResource[] members = null;
			try {
				if (resource instanceof IProject)
					members = ((IProject) resource).members();
				else if (resource instanceof IFolder) {
					IFolder folder = (IFolder) resource;
					members = folder.members();
					files.add(getS3Folder(folder, folder.getProject()) + "_$folder$");
				}
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
			for (IResource member : members) {
				ArrayList<String> memberFiles = getAllFiles(member);
				files.addAll(memberFiles);
			}
		}
		return files;
	}

	// private ArrayList<String> getAllFiles(IStructuredSelection selection) {
	// ArrayList<String> allFiles = new ArrayList<String>();
	// Object[] selectedObjects = selection.toArray();
	// for (Object selectedObject : selectedObjects) {
	// ArrayList<String> files = getAllFiles((IResource) selectedObject);
	// allFiles.addAll(files);
	// }
	//
	// return allFiles;
	// }

	private ArrayList<String> getAllFiles(IStructuredSelection selection) {
		ArrayList<String> allFiles = new ArrayList<String>();
		Object[] selectedObjects = selection.toArray();

		for (Object selectedObject : selectedObjects) {
			if (selectedObject instanceof IFolder) {
				IFolder selectedFolder = (IFolder) selectedObject;
				String bucketName = selectedFolder.getProject().getName();
				String path = selectedFolder.getFullPath().toString().replaceFirst(bucketName, "").replace("//", "");
				// Since we are only dealing with git repository, add ".git" at the end
				allFiles.addAll(s3.getAllFiles(bucketName, path + ".git"));

			}
		}
		return allFiles;
	}

	private ArrayList<String> getAllFilesCommunity(IStructuredSelection selection) {
		ArrayList<String> allFiles = new ArrayList<String>();
		Object[] selectedObjects = selection.toArray();

		for (Object selectedObject : selectedObjects) {
			if (selectedObject instanceof IFolder) {
				IFolder selectedFolder = (IFolder) selectedObject;
				String bucketName = selectedFolder.getProject().getName();
				String path = selectedFolder.getFullPath().toString().replaceFirst(bucketName, "").replace("//", "");
				// Since we are only dealing with git repository, add ".git" at the end
				allFiles.addAll(s3.getAllFiles(S3.getCommunityBucketName(), bucketName + "/" + path + ".git"));

			}
		}
		return allFiles;
	}
}
