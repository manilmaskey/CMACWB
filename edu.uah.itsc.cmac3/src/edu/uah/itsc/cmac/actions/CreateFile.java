package edu.uah.itsc.cmac.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class CreateFile {

	public void createfile(String filename) {
		ISelection selection = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getSelectionService()
				.getSelection();

		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			IFolder res = (IFolder) obj;
			// System.out.println("res parent " + res.getParent());
			System.out.println("New file selected " + res.getLocation());

			String location = res.getLocation().toString();

			IFile mywfFile = res.getFile(filename);
			System.out.println(mywfFile);

			byte[] bytes = "".getBytes();
			InputStream source = new ByteArrayInputStream(bytes);

			try {
				mywfFile.create(source, IResource.NONE, null);
				
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				
				System.out.println("file path is " + mywfFile);
				
				IDE.openEditor(page, mywfFile, true);
				
				
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

}
