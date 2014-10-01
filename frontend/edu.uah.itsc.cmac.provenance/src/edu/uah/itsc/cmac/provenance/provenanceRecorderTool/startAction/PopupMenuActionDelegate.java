package edu.uah.itsc.cmac.provenance.provenanceRecorderTool.startAction;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ProcessBuilder.Redirect;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.Activator;
//import edu.uah.itsc.cmac.provenace.handlers.ProvenanceHandler;
import edu.uah.itsc.cmac.Utilities;

public class PopupMenuActionDelegate implements IObjectActionDelegate {
	private Shell shell;

	public PopupMenuActionDelegate() {
		super();
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {

		shell = targetPart.getSite().getShell();
	}

	public void run(IAction action) {

		IEditorPart myeditor = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		IViewReference[] views = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getViewReferences();

		if (myeditor != null && myeditor.isDirty())
			myeditor.doSave(new NullProgressMonitor());

		/*
		 * IEditorPart myeditor = PlatformUI.getWorkbench()
		 * .getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		 */
		String filePath = myeditor.toString().replace("PyEdit", "")
				.replace("[", "").replace("]", "");
		;

		IEditorInput input = myeditor.getEditorInput();
		String fileName = input.getName();

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			String content = new String(readAllBytes(get(filePath)));
			// System.out.println(content);
			String server_ip = Utilities.getKeyValueFromPreferences("portal",
					"portal_domain");
			String wrapped_code = Request
					.Post("http://" + server_ip + "/prov-wrapper/prov-wrap.php")
					.bodyForm(
							Form.form().add("code", content)
									.add("filename", fileName).build())
					.execute().returnContent().asString();

			// System.out.println(wrapped_code);

			String trace_code = Request
					.Get("http://" + server_ip
							+ "/prov-wrapper/trace_execution.py").execute()
					.returnContent().asString()
					.replaceAll("54.208.76.40", server_ip);

			String tempfileName = "trace_" + fileName;

			Writer writer1 = null;
			Writer writer2 = null;
			try {

				writer1 = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath.replace(fileName,
								tempfileName)), "utf-8"));
				writer1.write(wrapped_code);

				writer2 = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(filePath.replace(fileName,
								"trace_execution.py")), "utf-8"));
				writer2.write(trace_code);

			} catch (IOException ex) {
				// report
			} finally {
				try {
					writer1.close();
					writer2.close();
				} catch (Exception ex) {
				}
			}

			try {
				// Process p =
				// Runtime.getRuntime().exec("python "+filePath.replace(fileName,
				// tempfileName) );

				ProcessBuilder pb = new ProcessBuilder("python",
						filePath.replace(fileName, tempfileName));
				pb.directory(new File(filePath.replace(fileName, "")));
				File log = new File("log");
				pb.redirectErrorStream(true);
				pb.redirectOutput(Redirect.appendTo(log));
				Process p = pb.start();
				p.waitFor();

				IProject[] iProjects = ResourcesPlugin.getWorkspace().getRoot()
						.getProjects();

				for (int i = 0; i < iProjects.length; i++) {
					iProjects[i].refreshLocal(IFolder.DEPTH_INFINITE, null);
				}

				// IFolder userFolder =
				// ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName).getFolder(User.username);

				// userFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
			} catch (IOException ex) {
				Logger.getLogger(PopupMenuActionDelegate.class.getName()).log(
						Level.SEVERE, null, ex);
			}

		} catch (Exception e) {

		}

	}

}
