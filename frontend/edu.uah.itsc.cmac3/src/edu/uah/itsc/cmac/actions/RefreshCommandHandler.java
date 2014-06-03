package edu.uah.itsc.cmac.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.NavigatorView;

public class RefreshCommandHandler extends AbstractHandler {
	private IStructuredSelection	selection	= StructuredSelection.EMPTY;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

		Object object = selection.getFirstElement();
		Job job = new Job("Refreshing...") {
			protected IStatus run(IProgressMonitor monitor) {
				if (selection.size() == 1) {

					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof IFile) {
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
							"You can only share folders!");
					}
					else if (firstElement instanceof IProject) {
						S3 s3 = new S3();
						try {
							NavigatorView view = (NavigatorView) getPage().findView("edu.uah.itsc.cmac.NavigatorView");
							view.refreshCommunityResource();
						}
						catch (Exception e) {
							System.out
								.println("Errror while refreshCommunityResource in RefreshAction " + e.toString());
						}
						IProject communityProject = ResourcesPlugin.getWorkspace().getRoot()
							.getProject(s3.getCommunityBucketName());
						try {
							communityProject.refreshLocal(IResource.DEPTH_INFINITE, monitor);
						}
						catch (CoreException e) {
							e.printStackTrace();
						}
					}

				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return object;
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