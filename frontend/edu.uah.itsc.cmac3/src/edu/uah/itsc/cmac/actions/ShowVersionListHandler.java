/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.cmac.model.VersionViewInterface;

/**
 * @author sshrestha
 * 
 */
public class ShowVersionListHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		Object firstElement = selection.getFirstElement();

		final IFolder folder = (IFolder) firstElement;
		final String parentPath = folder.getParent().getLocation().toString();

		try {
			VersionViewInterface versionViewInterface = (VersionViewInterface) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().showView("edu.uah.itsc.cmac.versionview.views.VersionView");
			versionViewInterface.accept(folder, folder.getName(), parentPath);

		}
		catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
