/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.util.GITUtility;

/**
 * @author sshrestha
 * 
 */
public class DeleteLocalCommandHandler extends AbstractHandler implements IHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		boolean userConfirmation = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
			"Warning! Delete!!", "Are you sure you want to delete selected resources?"
				+ "\nNote that this change will take place in shared resources as well.");
		if (!userConfirmation)
			return null;
		else {
			try {
				final IStructuredSelection selection = (IStructuredSelection) HandlerUtil
					.getCurrentSelectionChecked(event);
				Object[] selectedObjects = selection.toArray();
				for (Object selectedObject : selectedObjects) {
					String repoName = null;
					String repoLocalPath = null;
					String fileName = null;
					IFolder repoFolder = null;
					if (selectedObject instanceof IFolder) {
						IFolder selectedFolder = (IFolder) selectedObject;
						if (selectedFolder.getFolder(".git").exists() && selectedFolder.getParent() instanceof IProject) {
							// This is the repository, remove entire folder
							System.out.println("Do repository delete for " + selectedFolder.getName());
							selectedFolder.delete(true, null);
							return null;
						}
						else {
							// normal folder inside repository do git delete
							System.out.println("Do git delete for " + selectedFolder.getName());
							IResource folder = selectedFolder;
							fileName = folder.getName();
							while (folder.getParent() != folder.getProject())
								folder = folder.getParent();
							repoName = folder.getName();
							repoLocalPath = folder.getParent().getLocation().toString();
							repoFolder = selectedFolder;
						}
					}

					if (selectedObject instanceof IFile) {
						// normal file inside repository do git delete
						IFile file = (IFile) selectedObject;
						System.out.println("Do git delete for " + file.getName());
						IResource folder = file.getParent();
						while (folder.getParent() != file.getProject())
							folder = folder.getParent();
						repoName = folder.getName();
						repoLocalPath = folder.getParent().getLocation().toString();
						fileName = file.getProjectRelativePath().toString();
						fileName = fileName.replace(repoName + "/", "");
						repoFolder = (IFolder) folder;
					}
					System.out.println("repoName:  " + repoName + "repoLocalPath: " + repoLocalPath + "fileName: "
						+ fileName);
					GITUtility.delete(repoName, repoLocalPath, fileName);
					GITUtility.commitLocalChanges(repoName, repoLocalPath, "Commit after deletion", User.username,
						User.userEmail);
					repoFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);

				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		return null;
	}

}
