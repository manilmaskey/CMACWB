/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
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
		catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error!!", "This workflow is not enabled to be tracked");
		}
		return null;
	}

}
