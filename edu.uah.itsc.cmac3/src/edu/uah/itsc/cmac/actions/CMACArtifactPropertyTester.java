package edu.uah.itsc.cmac.actions;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

public class CMACArtifactPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if (expectedValue.equals("upload") || expectedValue.equals("execute")
				|| expectedValue.equals("share")) {

			if (receiver instanceof IProject)
				return false;
			else if (receiver instanceof IFolder) {

				IFolder folder = (IFolder) receiver;
				if ((folder.getParent() instanceof IProject)
						|| folder.getProject().getName()
								.equals(S3.communityBucketName))
					return false;

				else if (!folder.getProject().getName()
						.equals(S3.communityBucketName)) {
					if (folder.getParent() instanceof IFolder) {
						IFolder maybeUserFolder = (IFolder) folder.getParent();
						if (maybeUserFolder.getName().equals(User.username)
								&& !expectedValue.equals("execute"))
							return true;
						else
							return false;
					} else
						return false;
				} else
					return true;

			} else if (receiver instanceof IFile) {
				IFile file = (IFile) receiver;
				if (file.getProject().getName().equals(S3.communityBucketName))
					return false;
				else if (!file.getProject().getName()
						.equals(S3.communityBucketName)) {
					if (!file.getParent().getName().equals(User.username)) {
						String ext = file.getFileExtension();
						return (ext.equals("py") || ext.equals("pro") || expectedValue
								.equals("upload"));

					} else
						return false;
				} else
					return false;

			} else
				return true;
		}

		else if (expectedValue.equals("refresh")) { // Refresh

			if (receiver instanceof IProject) {
				if (((IProject) receiver).getName().equals(
						S3.communityBucketName))
					return true;
				else
					return false;
			} else
				return false;
		}

		else if (expectedValue.equals("import")) {
			if (receiver instanceof IFolder) {
				IFolder folder = (IFolder) receiver;
				if (folder.getParent() instanceof IFolder
						&& folder.getProject().getName()
								.equals(S3.communityBucketName))
					return true;
				else
					return false;
			} else
				return false;
		}

		else if (expectedValue.equals("delete")) {
			// Disable deleting project/bucket/experiment for now
			/*
			 * if (receiver instanceof IProject) { return true; } else
			 */
			if (receiver instanceof IFolder) {
				IFolder folder = (IFolder) receiver;
				if (folder.getProject().getName().equals(S3.bucketName)
						&& folder.getParent().getName()
								.equalsIgnoreCase(User.username)) {
					// HashMap<String, String> nodeMap = PortalUtilities
					// .getPortalWorkflowDetails(folder.getFullPath()
					// .toString());
					// if (nodeMap == null || nodeMap.get("nid") == "" ||
					// nodeMap.get("nid") == null)
					// return false;
					// else
					return true;
				} else
					return false;
			} else
				return false;

		}

		else
			return false;

	}
}