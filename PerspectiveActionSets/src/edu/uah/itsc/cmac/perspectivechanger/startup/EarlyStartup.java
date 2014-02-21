package edu.uah.itsc.cmac.perspectivechanger.startup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.internal.WorkbenchPage;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.LoginDialog;

/**
 * This class is loaded when the workbench start up
 */
@SuppressWarnings("restriction")
public class EarlyStartup implements IStartup {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	public void earlyStartup() {
		/*
		 * The registration of the listener should have been done in the UI thread since
		 * PlatformUI.getWorkbench().getActiveWorkbenchWindow() returns null if it is called outside of the UI thread.
		 */
		Display.getDefault().asyncExec(new Runnable() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

				if (workbenchWindow != null) {
					workbenchWindow.addPerspectiveListener(new PerspectiveAdapter() {

						// //////

						// public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
						// IWorkbenchPartReference partRef, String changeId){

						// }
						public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective,
							String changeId) {
							System.out.println("Changed-----------------------------");
						}

						public void perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
							System.out.println("Closed---------------------------");
						}

						public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
							System.out.println("Deactivated-------------------------------------");
						}

						public void perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
							System.out.println("Opened-----------------------------------");
						}

						// //////

						/*
						 * (non-Javadoc)
						 * 
						 * @seeorg.eclipse.ui.PerspectiveAdapter# perspectiveActivated (org.eclipse.ui.IWorkbenchPage,
						 * org.eclipse.ui.IPerspectiveDescriptor)
						 */
						@Override
						public void perspectiveActivated(IWorkbenchPage page,
							IPerspectiveDescriptor perspectiveDescriptor) {
							super.perspectiveActivated(page, perspectiveDescriptor);
							System.err.println("--> " + perspectiveDescriptor.getId());
							if (perspectiveDescriptor.getId().indexOf("edu.uah.itsc.cmac.perspective") > -1) {
								if (workbenchWindow.getActivePage() instanceof WorkbenchPage) {
									if (User.sessionID == null || User.sessionID.equals("")) {
										LoginDialog loginDialog = new LoginDialog(PlatformUI.createDisplay());
										loginDialog.createContents();
										if (User.awsAccessKey == null) {
											try {

												PlatformUI.getWorkbench().showPerspective(
													"org.eclipse.ui.resourcePerspective",
													PlatformUI.getWorkbench().getActiveWorkbenchWindow());
											}
											catch (WorkbenchException e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
										}

									}

								}
							}
						}
					});
				}
			}
		});
	}

}
