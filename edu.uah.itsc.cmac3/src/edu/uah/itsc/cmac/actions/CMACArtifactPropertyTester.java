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
		S3 s3 = new S3();
		if (expectedValue.equals("upload") || expectedValue.equals("execute") || expectedValue.equals("share")) {

			if (receiver instanceof IProject)
				return false;
			else if (receiver instanceof IFolder) {

				IFolder folder = (IFolder) receiver;
				if ((folder.getParent() instanceof IProject)
					|| folder.getProject().getName().equals(s3.getCommunityBucketName()))
					return false;

				else if (!folder.getProject().getName().equals(s3.getCommunityBucketName())) {
					if (folder.getParent() instanceof IFolder) {
						IFolder maybeUserFolder = (IFolder) folder.getParent();
						if (maybeUserFolder.getName().equals(User.username) && !expectedValue.equals("execute"))
							return true;
						else
							return false;
					}
					else
						return false;
				}
				else
					return true;

			}
			else if (receiver instanceof IFile) {
				IFile file = (IFile) receiver;
				if (file.getProject().getName().equals(s3.getCommunityBucketName()))
					return false;
				else if (!file.getProject().getName().equals(s3.getCommunityBucketName())) {
					if (!file.getParent().getName().equals(User.username)) {
						String ext = file.getFileExtension();
						return (ext.equals("py") || ext.equals("pro") || expectedValue.equals("upload"));

					}
					else
						return false;
				}
				else
					return false;

			}
			else
				return true;
		}

		else if (expectedValue.equals("refresh")) { // Refresh

			if (receiver instanceof IProject) {
				if (((IProject) receiver).getName().equals(s3.getCommunityBucketName()))
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
					&& folder.getProject().getName().equals(s3.getCommunityBucketName()))
					return true;
				else
					return false;
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
				if ((!folder.getProject().getName().equals(s3.getCommunityBucketName()))
					&& folder.getParent().getName().equalsIgnoreCase(User.username)) {
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

		else
			return false;

	}
}