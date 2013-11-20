package edu.uah.itsc.workflow.menuOptions;

import java.io.FileNotFoundException;
import java.io.IOException;

import jsonForSave.JSONWrite;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import piworkflow.editors.MultiPageEditor;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class WorkFlowSave implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		MultiPageEditor mpe = new MultiPageEditor();
		String fileName = null;
		String path = null; 
		try {
			
			IWorkbenchPart workbenchPart = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getActivePart();
			IFile file = (IFile) workbenchPart.getSite().getPage()
					.getActiveEditor().getEditorInput().getAdapter(IFile.class);
			if (file == null)
				throw new FileNotFoundException();
			path = file.getRawLocation().toOSString();
			System.out.println("path: " + path);
			
			fileName = mpe.getFileName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONWrite writerObject = new JSONWrite();
		writerObject.createJSONFile(path);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
	}

}
