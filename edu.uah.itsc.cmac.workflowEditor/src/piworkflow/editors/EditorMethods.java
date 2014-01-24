package piworkflow.editors;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorReference;

/**
 * 
 * @author lsamudrala
 * 
 */
public class EditorMethods {

	/**
	 * This method grabs the filename from the platformui -> workbench -> active
	 * workbench window -> pages -> page
	 * 
	 * @return the file name
	 */
	public String getFileName() {

		IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getPages();
		IWorkbenchPage page = pagelist[0];
		
		int l = page.getEditorReferences().length;
		String s1 = (page.getEditorReferences()[l-1]).getName();
		
		ISelection iselection = page.getSelection();
		
		IStructuredSelection select = (IStructuredSelection) iselection;
		
		Object obj = select.getFirstElement();
		
		String s = iselection.toString();
		
		System.out.println(obj.toString());
		
		System.out.println("select " + select);
		
		
		
//		String selection = iselection.toString();
		
		String filename = extractFileName(s1);

		return filename;
	}

	/**
	 * This method extracts the file name from the page selection
	 * 
	 * @param selection
	 *            is the page selection from which the file name is to be
	 *            extracted
	 */
	public String extractFileName(String selection) {

		StringTokenizer st = new StringTokenizer(selection, "/");
		String str = null;
		while (st.hasMoreElements()) {
			str = (String) st.nextElement();
		}
		System.out.println("the last string is " + str);

		StringTokenizer st2 = new StringTokenizer(str, "]");
		String key = st2.nextToken();
		String val = null;
		while (st2.hasMoreTokens()) {
			val = st2.nextToken();
		}
		System.out.println("the key is " + key);
		 System.out.println("the value is " + val);

		return key;
	}

	/**
	 * This method takes in the filename and workbench page and returns the path
	 * of the file
	 * 
	 * @param page
	 *            workbench page
	 * @param filename
	 *            name of the file
	 * @return path of the file
	 * @throws PartInitException
	 */
	public String getPath(IWorkbenchPage page, String filename)
			throws PartInitException {

		String path = null;

		IEditorReference[] referencelist = page.getEditorReferences();

		for (int i = 0; i < referencelist.length; i++) {
			IFile ifile = (IFile) referencelist[i].getEditorInput().getAdapter(
					IFile.class);
			if (ifile.getName().equals(filename)) {
				path = ifile.getRawLocation().toOSString();
			}
		}

		return path;

	}
	
//	public void setSelectionListeners (){
//		IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
//				.getActiveWorkbenchWindow().getPages();
//		IWorkbenchPage page = pagelist[0];
//		IEditorReference[] referencelist = page.getEditorReferences();
//		IEditorReference reference = referencelist[0];
//		reference.addPartPropertyListener(new IPropertyChangeListener() {
//			
//			@Override
//			public void propertyChange(PropertyChangeEvent event) {
//				// TODO Auto-generated method stub
//				System.out.println("------------------------------------------------------------------------------");
//			}
//		});
//	}

}
