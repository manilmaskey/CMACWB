/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.util.GITUtility;
import edu.uah.itsc.cmac.util.PropertyUtility;

/**
 * @author sshrestha
 * 
 */
public class UpdateCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object firstElement = selection.getFirstElement();

		final IFolder folder = (IFolder) firstElement;
		final String parentPath = folder.getParent().getLocation().toString();
		String remotePath = getRemotePath(folder);
		GITUtility.pull(folder.getName(), parentPath);
		try {
			folder.refreshLocal(IFolder.DEPTH_INFINITE, null);
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getRemotePath(IFolder repoFolder) {
		String workflowOwner = User.username;
		boolean isShared = false;
		String remotePath = null;

		File workflowPropertyFile = new File(repoFolder.getLocation().toString() + "/.cmacworkflow");

		if (workflowPropertyFile.exists()) {
			PropertyUtility propUtil = new PropertyUtility(workflowPropertyFile.getAbsolutePath());
			workflowOwner = propUtil.getValue("owner");
		}

		String path = "/" + repoFolder.getProject().getName() + "/" + workflowOwner + "/" + repoFolder.getName();
		HashMap<String, String> nodeMap = PortalUtilities.getPortalWorkflowDetails(path);
		if (nodeMap != null) {
			isShared = Integer.parseInt(nodeMap.get("isShared")) > 0 ? true : false;
		}

		remotePath = "amazon-s3://.jgit@";
		if (isShared)
			remotePath = remotePath + S3.getCommunityBucketName();
		remotePath = remotePath + "/" + repoFolder.getProject().getName() + "/" + workflowOwner + "/"
			+ repoFolder.getName() + ".git";
		return remotePath;
	}
}
