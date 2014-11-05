package edu.uah.itsc.cmac.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

public class CMACArtifactPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (expectedValue.equals("upload") || expectedValue.equals("execute") || expectedValue.equals("share")
			|| expectedValue.equals("allowClone")) {

			if (receiver instanceof IProject)
				return false;
			else if (receiver instanceof IFolder) {

				IFolder folder = (IFolder) receiver;

				if (folder.getParent() instanceof IProject
					&& !folder.getParent().getName().equals(S3.getCommunityBucketName()))
					return true;
				else
					return false;
				//
				// if ((folder.getParent() instanceof IProject)
				// || folder.getProject().getName().equals(S3.getCommunityBucketName()))
				// return false;
				//
				// else if (!folder.getProject().getName().equals(S3.getCommunityBucketName())) {
				// if (folder.getParent() instanceof IFolder) {
				// IFolder maybeUserFolder = (IFolder) folder.getParent();
				// if (maybeUserFolder.getName().equals(User.username) && !expectedValue.equals("execute"))
				// return true;
				// else
				// return false;
				// }
				// else
				// return false;
				// }
				// else
				// return true;

			}
			else if (receiver instanceof IFile) {
				IFile file = (IFile) receiver;
				if (file.getProject().getName().equals(S3.getCommunityBucketName()))
					return false;
				else if (!file.getProject().getName().equals(S3.getCommunityBucketName())) {
					// if (!file.getParent().getName().equals(User.username) && expectedValue.equals("execute")) {
					if (expectedValue.equals("execute")) {
						// Do not disable execute for scidb users for now
						// if (User.sciDBUserName != null || User.sciDBPassword != null)
						// return false;
						String ext = file.getFileExtension();
						return (ext.equals("py") || ext.equals("pro"));

					}
					else
						return false;
				}
				else
					return false;

			}
			else
				return false;
		}

		else if (expectedValue.equals("refresh")) { // Refresh

			if (receiver instanceof IProject) {
				if (((IProject) receiver).getName().equals(S3.getCommunityBucketName()))
					return true;
				else
					return false;
			}
			else
				return false;
		}

		else if (expectedValue.equals("import")) {
			if (receiver instanceof IFolder) {
				IFolder folder = (IFolder) receiver;
				if (folder.getParent() instanceof IFolder
					&& folder.getParent().getParent().getParent() instanceof IProject
					&& folder.getProject().getName().equals(S3.getCommunityBucketName()))
					return true;
				else
					return false;
			}
			else
				return false;
		}

		else if (expectedValue.equals("initTrackingWorkflow")) {

			if (receiver instanceof IProject) {
				IProject project = (IProject) receiver;
				if (!project.getName().equals(S3.getCommunityBucketName()))
					return true;
				else
					return false;
			}
			else
				return false;

			// if (receiver instanceof IFolder) {
			// IFolder folder = (IFolder) receiver;
			// if (folder.getParent() instanceof IProject && folder.getName().equalsIgnoreCase(User.username)
			// && !folder.getParent().getName().equalsIgnoreCase(S3.getCommunityBucketName())) {
			// return true;
			// }
			// else
			// return false;
			// }
			// else
			// return false;
		}

		else if (expectedValue.equals("saveForTrackingWorkflow") || expectedValue.equals("ShowVersionList")
			|| expectedValue.equals("AddVersion") || expectedValue.equals("UpdateCommand")) {
			if (receiver instanceof IFolder) {
				IFolder folder = (IFolder) receiver;

				if (folder.getParent() instanceof IProject
					&& !folder.getParent().getName().equals(S3.getCommunityBucketName()))
					return true;
				else
					return false;

				// if (folder.getParent() instanceof IFolder) {
				// IFolder parent = (IFolder) folder.getParent();
				// if (!parent.getName().equalsIgnoreCase(User.username))
				// return false;
				// if (!(parent.getParent() instanceof IProject))
				// return false;
				// if (parent.getProject().getName().equalsIgnoreCase(S3.getCommunityBucketName()))
				// return false;
				// return true;
				// }
				// else
				// return false;
			}
			else
				return false;
		}

		else if (expectedValue.equals("delete")) {
			// Disable deleting project/bucket/experiment for now
			/*
			 * if (receiver instanceof IProject) { return true; } else
			 */
			if (receiver instanceof IFolder) {
				IFolder folder = (IFolder) receiver;
				// Enable "Delete from cloud" only if this folder is not community bucket and is an immediate child of
				// userfolder
				if ((!folder.getProject().getName().equals(S3.getCommunityBucketName()))
					&& folder.getParent() instanceof IProject) {
					// HashMap<String, String> nodeMap = PortalUtilities
					// .getPortalWorkflowDetails(folder.getFullPath()
					// .toString());
					// if (nodeMap == null || nodeMap.get("nid") == "" ||
					// nodeMap.get("nid") == null)
					// return false;
					// else
					return true;
				}
				else
					return false;
			}
			else
				return false;

		}
		else if (expectedValue.equals("deleteLocal")) {
			if (receiver instanceof IFolder || receiver instanceof IFile)
				return true;
			else
				return false;
		}
		else if (expectedValue.equals("rename")) {
			if (receiver instanceof IFolder) {
				return true;
			}
			else
				return false;
		}

		else
			return false;

	}
}