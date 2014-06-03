package edu.uah.itsc.cmac.actions;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

public class NewFileHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("New file selected");

		NewWindow nw = new NewWindow();
		nw.getfilename();
		return nw;

		// ISelection selection = PlatformUI.getWorkbench()
		// .getActiveWorkbenchWindow().getSelectionService()
		// .getSelection();
		//
		// if (selection != null && selection instanceof IStructuredSelection) {
		// Object obj = ((IStructuredSelection) selection).getFirstElement();
		// IFolder res = (IFolder) obj;
		// // System.out.println("res parent " + res.getParent());
		// System.out.println("New file selected " + res.getLocation());
		//
		// String location = res.getLocation().toString();
		//
		// IFile mywfFile = res.getFile("testfile.wf");
		// System.out.println(mywfFile);
		//
		// byte[] bytes = "".getBytes();
		// InputStream source = new ByteArrayInputStream(bytes);
		//
		// try {
		// mywfFile.create(source, IResource.NONE, null);
		// } catch (CoreException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		// NewFileWizardPage nf = new NewFileWizardPage(selection);
		// nf.initialize();
		// }

		// return selection;
	}
}
