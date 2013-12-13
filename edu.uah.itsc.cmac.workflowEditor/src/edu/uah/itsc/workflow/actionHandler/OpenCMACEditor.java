package edu.uah.itsc.workflow.actionHandler;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * 
 * @author lsamudrala
 * 
 */
public class OpenCMACEditor implements IWorkbenchWindowActionDelegate {

	private IStructuredSelection selection = StructuredSelection.EMPTY;

	public OpenCMACEditor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			IResource res = (IResource) obj;
			// If we had a selection lets open the editor

			if (obj != null) {
				if (obj.getClass().getName().toString() == "org.eclipse.core.internal.resources.File") {
					File file = (File) obj;
					IFile fileToBeOpened = file;
					IEditorInput editorInput = new FileEditorInput(
							fileToBeOpened);
					IWorkbenchWindow window = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow();
					IWorkbenchPage page = window.getActivePage();
					try {
						page.openEditor(editorInput,
								"edu.uah.itsc.cmac.workflow.editor");
					} catch (PartInitException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection sel) {
		if (sel instanceof IStructuredSelection) {
			selection = (IStructuredSelection) sel;
		} else {
			selection = StructuredSelection.EMPTY;
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
