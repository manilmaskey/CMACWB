package jsonForSave;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

public class CreateWFfile {

	public void createfile(String file) {

		String filename = getfilename(file);

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
				IPath path = mywfFile.getFullPath();
				populatefile(path);
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private String getfilename(String file) {

		StringTokenizer st = new StringTokenizer(file, ".");
		String key = st.nextToken();
		String val;
		if (st.hasMoreTokens()) {
			val = st.nextToken();
		}

		key = key + ".wf";

		return key;
	}

	public void populatefile(IPath path) throws IOException {

		String content = "This is the content to write into file";

		File file = new File(path.toString());

		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

		System.out.println("Done");

	}
}
