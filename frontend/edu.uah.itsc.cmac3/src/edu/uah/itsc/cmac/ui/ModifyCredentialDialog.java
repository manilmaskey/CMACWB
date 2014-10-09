package edu.uah.itsc.cmac.ui;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.Activator;
import edu.uah.itsc.cmac.portal.PortalConnector;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.SciDBUser;

// import com.swtdesigner.SWTResourceManager;

public class ModifyCredentialDialog {

	private Shell		loginShell;
	private Display		display;
	private LoginDialog	loginDialog;
	private Shell		shell;

	public ModifyCredentialDialog(Shell loginShell, LoginDialog loginDialog) {
		this.loginShell = loginShell;
		this.loginDialog = loginDialog;
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

		Label userLabel = new Label(loginForm, SWT.NONE);
		userLabel.setText("Username");
		final Text userText = new Text(loginForm, SWT.BORDER);
		userText.setMessage("Enter your CWB username");
		userText.setLayoutData(textGridData);

		Label passLabel = new Label(loginForm, SWT.NONE);
		passLabel.setText("Password");
		final Text passText = new Text(loginForm, SWT.BORDER | SWT.PASSWORD);
		passText.setMessage("Enter your CWB password");
		passText.setLayoutData(textGridData);

		Label sciDBServerNameLabel = new Label(loginForm, SWT.NONE);
		sciDBServerNameLabel.setText("SciDB Server Name");
		final Combo serverCombo = new Combo(loginForm, SWT.READ_ONLY);
		final String[] servers = getSciDBServerList();
		serverCombo.setItems(servers);
		serverCombo.setLayoutData(textGridData);

		Label sciDBUsernameLabel = new Label(loginForm, SWT.NONE);
		sciDBUsernameLabel.setText("SciDB username");
		final Text sciDBUsernameText = new Text(loginForm, SWT.BORDER);
		sciDBUsernameText.setMessage("SciDB username");
		sciDBUsernameText.setLayoutData(textGridData);

		Label sciDBPasswordLabel = new Label(loginForm, SWT.NONE);
		sciDBPasswordLabel.setText("SciDB password");
		final Text sciDBPasswordText = new Text(loginForm, SWT.BORDER | SWT.PASSWORD);
		sciDBPasswordText.setMessage("Enter new SciDB password");
		sciDBPasswordText.setLayoutData(textGridData);

		Label emptylabel6 = new Label(loginForm, SWT.NONE);
		emptylabel6.setLayoutData(loginLabelGridData);

		Label emptylabel7 = new Label(loginForm, SWT.NONE);

		GridData buttonGridData = new GridData();
		buttonGridData.horizontalAlignment = SWT.CENTER;
		buttonGridData.widthHint = 100;

		Button loginButton = new Button(loginForm, SWT.PUSH);
		loginButton.setText("Submit");
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
				String sciDBUsername = sciDBUsernameText.getText();
				String sciDBPassword = sciDBPasswordText.getText();
				String selectedServer = null;

				int comboSelection = serverCombo.getSelectionIndex();
				if (comboSelection >= 0)
					selectedServer = servers[comboSelection];
				else {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
						"Please select the server to log in.");
					return;
				}

				JSONObject jsonObject;
				try {
					jsonObject = updateAccount(username, password, selectedServer, sciDBUsername, sciDBPassword);
					if (jsonObject != null) {
						shell.close();
						loginShell.setVisible(true);
						loginShell.setActive();
						MessageDialog.openInformation(shell, "Success", "Modified password successfully");
					}
					else
						MessageDialog
							.openError(shell, "Error",
								"Could not update your SciDB password. Check your CWB username, password and SciDB server!");
				}
				catch (Exception e1) {
					MessageDialog.openError(shell, "Error", "Could not update your SciDB passowrd!\n" + e1.getMessage());
					e1.printStackTrace();
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

	private JSONObject updateAccount(String username, String password, String sciDBServerName, String sciDBUsername,
		String sciDBPassword) throws Exception {
		PortalConnector pc = new PortalConnector();
		JSONObject jsonObject = pc.connect(username, password);
		HttpClient httpclient = new DefaultHttpClient();

		String session_name = jsonObject.getString("session_name");
		String session_id = jsonObject.getString("sessid");

		BasicHttpContext mHttpContext = new BasicHttpContext();
		CookieStore mCookieStore = new BasicCookieStore();
		BasicClientCookie cookie = new BasicClientCookie(session_name, session_id);
		cookie.setVersion(0);
		cookie.setDomain(PortalUtilities.getPortalDomain());
		cookie.setPath("/");
		mCookieStore.addCookie(cookie);
		mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);

		PortalPost post = new PortalPost();
		String csrfToken = post.getCSRF(PortalUtilities.getTokenURL(), mHttpContext);

		HashMap<String, String> map = getSciDBUserDetail(sciDBUsername, sciDBServerName);
		if (map == null)
			throw new Exception("The SciDB username or Server detail is incorrect");
		String nodeID = map.get("nid");

		SciDBUser sciDBUser = new SciDBUser(sciDBServerName, "", sciDBUsername, sciDBPassword, username, username);
		StringEntity sciDBSe = new StringEntity(sciDBUser.getJSON().toString());
		sciDBSe.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
		HttpPut httpPut = new HttpPut(PortalUtilities.getNodeRestPoint() + "/" + nodeID);
		httpPut.setEntity(sciDBSe);
		httpPut.addHeader("X-CSRF-TOKEN", csrfToken);
		HttpResponse response = httpclient.execute(httpPut, mHttpContext);
		if (response.getStatusLine().getStatusCode() != 200)
			throw new Exception(EntityUtils.toString(response.getEntity()));
		return jsonObject;
	}

	private HashMap<String, String> getSciDBUserDetail(String sciDBUsername, String server) {
		HashMap<String, String> map = null;
		try {
			String url = PortalUtilities.getSciDBUserDetailURL() + "?field_scidb_username_value=" + sciDBUsername
				+ "&field_scidb_server_name_value=" + URLEncoder.encode(server, "UTF-8");
			String response = PortalUtilities.getDataFromURL(url);
			JSONObject jsonResponse = new JSONObject(response);
			JSONArray serverArray = jsonResponse.getJSONArray("users");
			if (serverArray.length() == 0)
				throw new Exception("You don't have credentials for this server");
			if (serverArray.length() > 1)
				System.out.println("More than one user detial");
			JSONObject userObj = serverArray.getJSONObject(0).getJSONObject("user");
			map = new HashMap<String, String>();
			map.put("server", userObj.getString("scidb_server_name"));
			map.put("server_url", userObj.getString("scidb_server_url"));
			map.put("username", userObj.getString("scidb_username"));
			map.put("password", userObj.getString("scidb_password"));
			map.put("nid", userObj.getString("nid"));
		}
		catch (Exception e) {

		}
		return map;
	}

	private String[] getSciDBServerList() {
		String response = PortalUtilities.getDataFromURL(PortalUtilities.getSciDBServerListURL());
		HashMap<String, String> servers = new HashMap<String, String>();
		try {
			JSONObject jsonResponse = new JSONObject(response);
			JSONArray serverArray = jsonResponse.getJSONArray("servers");
			for (int i = 0; i < serverArray.length(); i++) {
				JSONObject serverObj = serverArray.getJSONObject(i).getJSONObject("server");
				String serverName = serverObj.getString("scidb_server_name");
				String serverURL = serverObj.getString("scidb_server_url");
				if (!servers.containsKey(serverName))
					servers.put(serverName, serverURL);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return servers.keySet().toArray(new String[servers.size()]);
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