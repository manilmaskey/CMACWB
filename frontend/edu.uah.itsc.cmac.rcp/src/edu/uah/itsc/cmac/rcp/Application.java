package edu.uah.itsc.cmac.rcp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
	 */
	public Object start(IApplicationContext context) {
		Display display = PlatformUI.createDisplay();

		try {
			setWorkspace();
		}
		catch (Exception e) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot open the workspace");
			System.exit(0);
			e.printStackTrace();
		}

		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		}
		finally {
			display.dispose();
		}
	}

	private void setWorkspace() throws IOException, MalformedURLException {
		String workspaceLoc = System.getProperty("user.home") + System.getProperty("file.separator") + "cwb.workspace";

		InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), "Set workspace",
			"Select workspace", workspaceLoc, new IInputValidator() {

				@Override
				public String isValid(String newText) {
					if (newText.isEmpty())
						return "You must select your workspace";
					return null;
				}
			});

		if (dialog.open() == Window.OK) {
			workspaceLoc = dialog.getValue();
		}
		else
			System.exit(0);

		Location instanceLocation = Platform.getInstanceLocation();
		if (instanceLocation.isSet())
			instanceLocation.release();
		instanceLocation.set(new URL("file", null, workspaceLoc), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					workbench.close();
			}
		});
	}
}
