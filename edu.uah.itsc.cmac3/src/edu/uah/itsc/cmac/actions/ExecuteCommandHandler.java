package edu.uah.itsc.cmac.actions;

import java.io.File;
import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

public class ExecuteCommandHandler extends AbstractHandler {
	private IStructuredSelection	selection	= StructuredSelection.EMPTY;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

		Object object = selection.getFirstElement();

		final Shell shell = new Shell();
		// In production code, you should always externalize strings, but this
		// is an example.
		// monitor.beginTask("Converting GSF file...",100);
		if (selection.size() == 1) {

			Object firstElement = selection.getFirstElement();
			IFolder selectedFolder = null;
			String folder = null;
			if (firstElement instanceof IFile) {
				IFile selectedFile = (IFile) firstElement;
				selectedFolder = (IFolder) selectedFile.getParent();
				folder = selectedFile.getParent().getName();

				IEditorPart myeditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
				if (myeditor != null && myeditor.isDirty())
					myeditor.doSave(new NullProgressMonitor());
				// myeditor.doSaveAs();

				String bucket = selectedFolder.getProject().getName();
				// String file = selectedFile.getLocation().toOSString();
				String file = selectedFile.getName();
				System.out.println("Selected Folder:" + selectedFolder.getName());
				String path = selectedFolder.getFullPath().toString();

				try {
					ExecuteDialog executeDialog = new ExecuteDialog(path, file, folder, bucket, selectedFolder,
						getPage());
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return object;
	}

	private void addSpanData(Control comp) {
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		comp.setLayoutData(data);
	}

	/**
	 * Returns the active page or null if no page is available. A page is a composition of views and editors which are
	 * meant to show at the same time.
	 * 
	 * @return The active page.
	 */
	public static IWorkbenchPage getPage() throws Exception {
		final IWorkbenchPage page[] = new IWorkbenchPage[] { null };
		final String errorMessages[] = new String[] { "" };

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				try {
					IWorkbench wb = PlatformUI.getWorkbench();
					IWorkbenchWindow wbWindow = wb.getActiveWorkbenchWindow();
					page[0] = wbWindow.getActivePage();
				}
				catch (Exception e) {
					errorMessages[0] = e.toString();
				}
			}
		});
		if (errorMessages[0].length() > 0) {
			throw new Exception(errorMessages[0]);
		}
		return page[0];
	}
}