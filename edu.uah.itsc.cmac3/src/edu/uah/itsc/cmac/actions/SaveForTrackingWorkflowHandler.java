/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.cmac.util.GITUtility;

/**
 * @author sshrestha
 *
 */
public class SaveForTrackingWorkflowHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object firstElement = selection.getFirstElement();

		final IFolder folder = (IFolder) firstElement;
		final String parentPath = folder.getParent().getLocation().toString();
		try {
			GITUtility.commitLocalChanges(folder.getName(), parentPath, "Testing commit");
		}
		catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (GitAPIException e) {
			MessageDialog.openError(new Shell(), "Error!!", "This workflow is not enabled to be tracked");
		}
		return null;
	}

}
