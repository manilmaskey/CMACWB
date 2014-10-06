package edu.uah.itsc.cmac.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.Activator;
import edu.uah.itsc.cmac.actions.XMPPClient;
import edu.uah.itsc.cmac.portal.PortalConnector;
import edu.uah.itsc.cmac.portal.PortalUtilities;

// import com.swtdesigner.SWTResourceManager;

public class CreateAccountDialog {

	private Shell	loginShell;
	private Display	display;
	private Shell shell;
	
	public CreateAccountDialog(Shell loginShell) {
		this.loginShell = loginShell;
		this.display = loginShell.getDisplay();
	}

	public void createContents() {
		loginShell.setVisible(false);
		// Shell must be created with style SWT.NO_TRIM
		shell = new Shell(display, SWT.SHELL_TRIM & (~SWT.RESIZE) & (~SWT.MAX) & (~SWT.MIN) & (~SWT.CLOSE));
		shell.setText("Create a new account");
		GridLayout layout = new GridLayout(2, false);
		shell.setLayout(layout);

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

		Label emailLabel = new Label(loginForm, SWT.NONE);
		emailLabel.setText("Email");
		final Text emailText = new Text(loginForm, SWT.BORDER);
		emailText.setMessage("Enter your email address");
		emailText.setLayoutData(textGridData);

		Label userLabel = new Label(loginForm, SWT.NONE);
		userLabel.setText("Username");
		final Text userText = new Text(loginForm, SWT.BORDER);
		userText.setMessage("Enter your desired username");
		userText.setLayoutData(textGridData);

		Label passLabel = new Label(loginForm, SWT.NONE);
		passLabel.setText("Password");
		final Text passText = new Text(loginForm, SWT.BORDER | SWT.PASSWORD);
		passText.setMessage("Enter your password");
		passText.setLayoutData(textGridData);

		Label confirmPassLabel = new Label(loginForm, SWT.NONE);
		confirmPassLabel.setText("Confirm Password");
		final Text confirmPassText = new Text(loginForm, SWT.BORDER | SWT.PASSWORD);
		confirmPassText.setMessage("Confirm your password");
		confirmPassText.setLayoutData(textGridData);

		Label sciDBServerNameLabel = new Label(loginForm, SWT.NONE);
		sciDBServerNameLabel.setText("SciDB Server Name");
		final Text sciDBServerNameText = new Text(loginForm, SWT.BORDER);
		sciDBServerNameText.setMessage("SciDB Server Name");
		sciDBServerNameText.setLayoutData(textGridData);

		Label sciDBServerAddrLabel = new Label(loginForm, SWT.NONE);
		sciDBServerAddrLabel.setText("SciDB server address");
		final Text sciDBServerAddrText = new Text(loginForm, SWT.BORDER);
		sciDBServerAddrText.setMessage("SciDB server address");
		sciDBServerAddrText.setLayoutData(textGridData);

		Label sciDBUsernameLabel = new Label(loginForm, SWT.NONE);
		sciDBUsernameLabel.setText("SciDB username");
		final Text sciDBUsernameText = new Text(loginForm, SWT.BORDER);
		sciDBUsernameText.setMessage("SciDB username");
		sciDBUsernameText.setLayoutData(textGridData);

		Label sciDBPasswordLabel = new Label(loginForm, SWT.NONE);
		sciDBPasswordLabel.setText("SciDB password");
		final Text sciDBPasswordText = new Text(loginForm, SWT.BORDER | SWT.PASSWORD);
		sciDBPasswordText.setMessage("SciDB password");
		sciDBPasswordText.setLayoutData(textGridData);

		Label emptylabel6 = new Label(loginForm, SWT.NONE);
		emptylabel6.setLayoutData(loginLabelGridData);

		Label emptylabel7 = new Label(loginForm, SWT.NONE);

		GridData buttonGridData = new GridData();
		buttonGridData.horizontalAlignment = SWT.CENTER;
		buttonGridData.widthHint = 100;

		Button loginButton = new Button(loginForm, SWT.PUSH);
		loginButton.setText("Create");
		loginButton.setLayoutData(buttonGridData);

		Button cancelButton = new Button(loginForm, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(buttonGridData);

		Label emptylabelLast = new Label(loginForm, SWT.NONE);
		emptylabelLast.setLayoutData(loginLabelGridData);

		Label copyRightLabel = new Label(loginForm, SWT.NONE);
		copyRightLabel.setText("Copyright: ITSC, University of Alabama in Huntsville");
		copyRightLabel.setLayoutData(loginLabelGridData);

		shell.pack();
		setWindowPosition(shell);
		shell.open();

		loginButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String username = userText.getText();
				String password = passText.getText();
				String password2 = confirmPassText.getText();
				String email = emailText.getText();
				String sciDBServerName = sciDBServerNameText.getText();
				String sciDBServerAddr = sciDBServerAddrText.getText();
				String sciDBUsername = sciDBUsernameText.getText();
				String sciDBPassword = sciDBPasswordText.getText();

				if (!password.equals(password2)) {
					MessageDialog.openError(shell, "Error", "Password Mismatch");
				}
				else {
					PortalConnector pc = new PortalConnector();
					JSONObject jsonObject = pc.createAccount(username, password, email, sciDBServerName,
						sciDBServerAddr, sciDBUsername, sciDBPassword);

					if (jsonObject != null) {
						XMPPClient xc = new XMPPClient();
						xc.createUser(username, password, email, "");
						xc.setUsername(username);
						xc.setPassword(password);
						User.username = username;
						User.password = password;
						proceedLogin(username, password, sciDBServerName, jsonObject);
					}
					else
						MessageDialog.openError(shell, "Error", "Could not create new account!");
				}
			}
		});

		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loginShell.setVisible(true);
				loginShell.setActive();
				shell.close();
			}
		});

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void proceedLogin(String username, String password, String selectedServer, JSONObject jsonObject) {
		if (jsonObject != null) {
			User.username = username;
			User.password = password;

			if (selectedServer != null)
				try {
					setSciDBUserDetails(selectedServer);
				}
				catch (Exception e) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", e.getMessage());
					finish();
					return;
				}

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

			String homeDir = System.getProperty("user.home");
			File jgitFile = new File(homeDir + "/" + ".jgit");
			if (jgitFile.exists()) {
				jgitFile.delete();
			}

			try {
				jgitFile.createNewFile();
				S3 s3 = new S3();
				s3.createJgitContents(jgitFile, false);
				createSciDBFile();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}

			if (!jgitFile.exists()) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Cannot load your credentials");
			}

			finish();
			shell.close();
			loginShell.close();

		}
		else {
			MessageDialog.openError(shell, "Error", "Could not login!");
			finish();
		}
	}
	

	private void finish() {
		shell.setCursor(new Cursor(display, SWT.CURSOR_ARROW));
	}

	private void createSciDBFile() throws IOException {
		String homeDir = System.getProperty("user.home");
		File sciDBFile = new File(homeDir + "/.cwb_aes");
		if (sciDBFile.exists()) {
			sciDBFile.delete();
		}
		if (User.sciDBUserName != null && User.sciDBPassword != null) {
			sciDBFile.createNewFile();
			String content = "name=" + User.sciDBServerName + "\nurl=" + User.sciDBServerURL + "\nusername="
				+ User.sciDBUserName + "\npassword=" + User.sciDBPassword + "\n";
			FileWriter fw = new FileWriter(sciDBFile.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
			sciDBFile.deleteOnExit();
		}
	}
	
	private void setSciDBUserDetails(String server) throws Exception {
		try {
			String url = PortalUtilities.getSciDBUserDetailURL() + "?field_portal_user_uid=" + User.portalUserID
				+ "&field_scidb_server_name_value=" + URLEncoder.encode(server, "UTF-8");
			String response = PortalUtilities.getDataFromURL(url);
			JSONObject jsonResponse = new JSONObject(response);
			JSONArray serverArray = jsonResponse.getJSONArray("users");
			if (serverArray.length() == 0)
				throw new Exception("You don't have credentials for this server");
			if (serverArray.length() > 1)
				System.out.println("More than one user detial");
			JSONObject userObj = serverArray.getJSONObject(0).getJSONObject("user");
			User.sciDBServerName = userObj.getString("scidb_server_name");
			User.sciDBServerURL = userObj.getString("scidb_server_url");
			User.sciDBUserName = userObj.getString("scidb_username");
			User.sciDBPassword = userObj.getString("scidb_password");
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
}