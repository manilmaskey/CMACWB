package edu.uah.itsc.cmac.rcp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
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
		Display display = Display.getDefault();
		final Shell shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE) & (~SWT.MAX) & (~SWT.MIN) & (~SWT.CLOSE));

		shell.setLayout(new GridLayout(4, false));
		shell.setText("Select workspace");
		GridData emptyData = new GridData();
		emptyData.horizontalSpan = 4;

		Label emptyLabel1 = new Label(shell, SWT.NONE);
		emptyLabel1.setLayoutData(emptyData);

		Label workspaceLabel = new Label(shell, SWT.NONE);
		workspaceLabel.setText("Workspace: ");

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.widthHint = 300;
		final Text workspaceLocText = new Text(shell, SWT.BORDER);
		workspaceLocText.setText(workspaceLoc);
		workspaceLocText.setLayoutData(gridData);

		GridData buttonData = new GridData();
		// buttonData.heightHint = 20;
		buttonData.widthHint = 90;

		Button browseButton = new Button(shell, SWT.PUSH);
		browseButton.setText("Browse");
		browseButton.setLayoutData(buttonData);

		Label emptyLabel2 = new Label(shell, SWT.NONE);
		emptyLabel2.setLayoutData(emptyData);

		Label emptyLabel3 = new Label(shell, SWT.NONE);

		// GridData compositeData = new GridData();
		// compositeData.horizontalAlignment = SWT.CENTER;
		//
		// Composite buttonComposite = new Composite(shell, SWT.NONE);
		Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText("Ok");
		okButton.setLayoutData(buttonData);

		Button cancelButton = new Button(shell, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(buttonData);
		Label emptyLabel4 = new Label(shell, SWT.NONE);

		Label emptyLabel5 = new Label(shell, SWT.NONE);
		emptyLabel2.setLayoutData(emptyData);

		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell);
				String selectedDir = dialog.open();
				if (selectedDir != null)
					workspaceLocText.setText(selectedDir);
			}
		});

		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String selectedDir = workspaceLocText.getText();
				if (selectedDir.isEmpty()) {
					MessageDialog.openError(shell, "Error", "You must select workspace directory");
					return;
				}
				Location instanceLocation = Platform.getInstanceLocation();
				if (instanceLocation.isSet())
					instanceLocation.release();
				try {
					instanceLocation.set(new URL("file", null, selectedDir), false);
				}
				catch (Exception e) {
					MessageDialog.openError(shell, "Error", "Cannot select workspace");
				}
				shell.close();
			}
		});

		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
			}
		});
		shell.pack();
		setWindowPosition(display, shell);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
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

	private void setWindowPosition(Display display, Shell shell) {
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);
	}
}
