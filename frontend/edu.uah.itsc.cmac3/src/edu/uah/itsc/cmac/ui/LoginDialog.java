package edu.uah.itsc.cmac.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.Activator;
import edu.uah.itsc.cmac.Utilities;
import edu.uah.itsc.cmac.portal.PortalConnector;
import edu.uah.itsc.cmac.ui.preferencewizard.PreferenceWizard;

public class LoginDialog {

	private Display	display;
	private Shell	shell;
	private Text	userText;
	private Text	passText;

	public LoginDialog(Display display) {
		this.display = display;
	}

	public void createContents() {
		shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE) & (~SWT.MAX) & (~SWT.MIN) & (~SWT.CLOSE));
		shell.setText("Login to CWB");
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);

		setWindowPosition(shell);
		
		// Check if the preferences have been set already. If not open wizard page
		if (!Utilities.isPreferenceSet())
			openPreferenceWizard();

		// Shell must be created with style SWT.NO_TRIM

		Label logoLabel = new Label(shell, SWT.NONE);
		logoLabel.setImage(getImageFromPlugin("login.png"));

		Composite loginForm = new Composite(shell, SWT.BORDER);
		GridLayout loginFormLayout = new GridLayout(3, false);
		loginForm.setLayout(loginFormLayout);

		GridData loginLabelGridData = new GridData();
		loginLabelGridData.horizontalSpan = 3;
		loginLabelGridData.horizontalAlignment = SWT.CENTER;

		Label emptylabel1 = new Label(loginForm, SWT.NONE);
		emptylabel1.setLayoutData(loginLabelGridData);

		GridData textGridData = new GridData();
		textGridData.horizontalSpan = 2;
		textGridData.horizontalAlignment = SWT.LEFT;
		textGridData.grabExcessHorizontalSpace = true;
		textGridData.widthHint = 200;
		Label userLabel = new Label(loginForm, SWT.NONE);
		userLabel.setText("Username");
		userText = new Text(loginForm, SWT.BORDER);
		userText.setMessage("Enter your username");
		userText.setLayoutData(textGridData);

		Label passLabel = new Label(loginForm, SWT.NONE);
		passLabel.setText("Password");
		passText = new Text(loginForm, SWT.BORDER | SWT.PASSWORD);
		passText.setMessage("Enter your Password");
		passText.setLayoutData(textGridData);

		Label emptylabel2 = new Label(loginForm, SWT.NONE);
		emptylabel2.setLayoutData(loginLabelGridData);

		GridData buttonGridData = new GridData();
		buttonGridData.horizontalAlignment = SWT.CENTER;
		buttonGridData.widthHint = 100;

		Button loginButton = new Button(loginForm, SWT.PUSH);
		loginButton.setText("Login");
		loginButton.setLayoutData(buttonGridData);

		Button cancelButton = new Button(loginForm, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(buttonGridData);

		Button createNewButton = new Button(loginForm, SWT.PUSH);
		createNewButton.setText("New Account");
		createNewButton.setLayoutData(buttonGridData);

		Label emptylabel3 = new Label(loginForm, SWT.NONE);
		emptylabel3.setLayoutData(loginLabelGridData);

		Label copyRightLabel = new Label(loginForm, SWT.NONE);
		copyRightLabel.setText("Copyright: ITSC, University of Alabama in Huntsville");
		copyRightLabel.setLayoutData(loginLabelGridData);

		shell.pack();
		setWindowPosition(shell);
		shell.open();

		loginButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				login();
			}
		});

		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
				shell.dispose();
				System.exit(0);
			}
		});

		createNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateAccountDialog createAccountDialog = new CreateAccountDialog(shell);
				createAccountDialog.createContents();
			}
		});

		Listener listener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.keyCode == '\r')
					login();
			}
		};

		display.addFilter(SWT.KeyDown, listener);

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}

		display.removeFilter(SWT.KeyDown, listener);
	}

	private void openPreferenceWizard() {
		WizardDialog dialog = new WizardDialog(shell, new PreferenceWizard());
		int returnvalue = dialog.open();
		if (returnvalue == 1) {
			System.exit(0);
		}
	}

	private Image getImageFromPlugin(String imageName) {
		Bundle bundle = Activator.getDefault().getBundle();
		Path path = new Path(imageName);
		URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
		URL fileUrl = null;
		try {
			fileUrl = FileLocator.toFileURL(url);
			Image logoImage = new Image(display, fileUrl.getPath());
			return logoImage;
		}
		catch (IOException e) {
			System.out.println(e.toString());
			return null;
		}
	}

	private void setWindowPosition(Shell shell) {
		Monitor primary = display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();

		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		shell.setLocation(x, y);
	}

	private void login() {
		shell.setCursor(new Cursor(display, SWT.CURSOR_WAIT));
		String username = userText.getText();
		String password = passText.getText();

		PortalConnector pc = new PortalConnector();

		JSONObject jsonObject = pc.connect(username, password);

		if (jsonObject != null) {
			User.username = username;
			User.password = password;

			// Get preferences
			Preferences preferences = InstanceScope.INSTANCE.getNode("edu.uah.itsc.cmac.preferences");
			Preferences prefOwner = preferences.node("workspaceOwnerNode");
			String owner = prefOwner.get("workspaceOwner", User.username);

			// If the owner is a different user, give warning and ask user if he wants to continue
			if (!owner.equals(username)) {
				System.out.println("Not workspace owner");
				boolean answer = MessageDialog.openQuestion(shell, "Workspace Conflict", "This workspace is owned by '"
					+ owner + "'. If  you continue the workspace will be merged and it might generate conflicts. "
					+ "\nAre you sure you want to continue?");
				if (!answer) {
					finish();
					return;
				}
			}

			else {
				prefOwner.put("workspaceOwner", User.username);
				try {
					preferences.flush();
				}
				catch (BackingStoreException e2) {
					e2.printStackTrace();
					System.out.println("Cannot flush preferences");
				}
			}
			finish();
			shell.close();

			String homeDir = System.getProperty("user.home");
			File jgitFile = new File(homeDir + "/" + ".jgit");
			if (jgitFile.exists()) {
				jgitFile.delete();
			}
			try {
				jgitFile.createNewFile();
				S3 s3 = new S3();
				s3.createJgitContents(jgitFile, false);
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}

			if (!jgitFile.exists()) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot load your credentials");
			}

			System.out.println("Connecting to XMPP-----------------------");

		}
		else {
			MessageDialog.openError(shell, "Error", "Could not login!");
			finish();
		}
	}

	private void finish() {
		shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
	}
}