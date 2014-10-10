/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.cmac.util.GITUtility;

/**
 * @author sshrestha
 * 
 */
public class InitTrackingWorkflowHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object firstElement = selection.getFirstElement();

		final IProject project = (IProject) firstElement;
		final String path = project.getLocation().toOSString();

		InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), "New Workflow",
			"Enter name for new workflow", "workflow", new IInputValidator() {
				@Override
				public String isValid(String newText) {
					if (newText.isEmpty())
						return "You must provide name for the workflow";
					return null;
				}
			});

		if (dialog.open() == Window.OK) {

			String workflow = dialog.getValue();
			try {
				GITUtility.createLocalRepo(workflow, path);
			}
			catch (IOException exception) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error!!", exception.getMessage());
				return null;
			}

			try {
				if (!project.isOpen())
					project.open(null);
				project.refreshLocal(IProject.DEPTH_INFINITE, null);
			}
			catch (CoreException e1) {
				e1.printStackTrace();
			}

		}
		return null;
	}
}
