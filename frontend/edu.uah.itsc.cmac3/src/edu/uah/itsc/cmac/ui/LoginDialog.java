package edu.uah.itsc.cmac.ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.Activator;
import edu.uah.itsc.cmac.portal.PortalConnector;

public class LoginDialog {

	private static Text	txt_Password;
	private static Text	txt_Username;
	private Display		display;

	public LoginDialog(Display display) {
		this.display = display;
	}

	public void createContents() {
		// Shell must be created with style SWT.NO_TRIM
		final Shell shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
		final FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 1;
		shell.setLayout(fillLayout);

		// Create a composite with grid layout.
		final Composite composite = new Composite(shell, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.horizontalSpacing = 0;
		composite.setLayout(gridLayout);

		// Setting the background of the composite
		// with the image background for login dialog

		Composite imgComposite = new Composite(shell, SWT.NONE);
		imgComposite.setLayoutData(new GridData(195, 210));
		final Label img_Label = new Label(composite, SWT.BORDER);
		img_Label.setLayoutData(new GridData(195, 210));
		// Button button = new Button (composite, SWT.NONE);
		// button.setLayoutData(new GridData(195, 210));

		Bundle bundle = Activator.getDefault().getBundle();
		Path path = new Path("login.bmp");
		URL url = FileLocator.find(bundle, path, Collections.EMPTY_MAP);
		URL fileUrl = null;
		try {
			fileUrl = FileLocator.toFileURL(url);
		}
		catch (IOException e) {
			System.out.println(e.toString());
		}

		Image img = new Image(display, fileUrl.getPath());

		// final Image img = new Image(display,"/User/mmaskey/Documents/workspace/svn/glider/splash.bmp");
		img_Label.setImage(img);

		// Creating the composite which will contain
		// the login related widgets
		final Composite cmp_Login = new Composite(composite, SWT.NONE);
		final RowLayout rowLayout = new RowLayout();
		rowLayout.fill = true;
		cmp_Login.setLayout(rowLayout);
		final GridData gridData = new GridData(GridData.FILL, GridData.FILL, false, false);
		gridData.widthHint = 296;
		cmp_Login.setLayoutData(gridData);

		// Label for the heading
		final CLabel clbl_UserLogin = new CLabel(cmp_Login, SWT.NONE);
		final RowData rowData = new RowData();
		rowData.width = 280;
		clbl_UserLogin.setLayoutData(rowData);
		clbl_UserLogin.setText("Login");

		// Label for the username
		final CLabel clbl_Username = new CLabel(cmp_Login, SWT.NONE);
		final RowData rowData_1 = new RowData();
		rowData_1.width = 280;
		clbl_Username.setLayoutData(rowData_1);
		clbl_Username.setText("Username");

		// Textfield for the username
		txt_Username = new Text(cmp_Login, SWT.BORDER);
		final RowData rowData_2 = new RowData();
		rowData_2.width = 270;
		txt_Username.setLayoutData(rowData_2);

		// Label for the password
		final CLabel clbl_Password = new CLabel(cmp_Login, SWT.NONE);
		final RowData rowData_3 = new RowData();
		rowData_3.width = 280;
		clbl_Password.setLayoutData(rowData_3);
		clbl_Password.setText("Password");

		// Textfield for the password
		txt_Password = new Text(cmp_Login, SWT.BORDER);
		final RowData rowData_4 = new RowData();
		rowData_4.width = 270;
		txt_Password.setLayoutData(rowData_4);
		txt_Password.setEchoChar('*');

		// Composite to hold button as I want the
		// button to be positioned to my choice.
		final Composite cmp_ButtonBar = new Composite(cmp_Login, SWT.NONE);
		final RowData rowData_5 = new RowData();
		rowData_5.height = 38;
		rowData_5.width = 285;
		cmp_ButtonBar.setLayoutData(rowData_5);
		cmp_ButtonBar.setLayout(new FormLayout());

		// Button for login
		final Button btn_login = new Button(cmp_ButtonBar, SWT.FLAT);
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(0, 28);
		formData.top = new FormAttachment(0, 5);
		formData.right = new FormAttachment(100, -230);
		// formData.left = new FormAttachment(0, -200);
		btn_login.setLayoutData(formData);
		btn_login.setText("Login");

		final Button btn_createAccount = new Button(cmp_ButtonBar, SWT.FLAT);
		final FormData formData2 = new FormData();
		formData2.bottom = new FormAttachment(0, 28);
		formData2.top = new FormAttachment(0, 5);
		formData2.right = new FormAttachment(100, -85);
		// formData2.left = new FormAttachment(100, -10);
		btn_createAccount.setLayoutData(formData2);
		btn_createAccount.setText("Create New Account");

		final Button btn_cancel = new Button(cmp_ButtonBar, SWT.FLAT);
		final FormData formData1 = new FormData();
		formData1.bottom = new FormAttachment(0, 28);
		formData1.top = new FormAttachment(0, 5);
		formData1.right = new FormAttachment(100, -20);
		// formData1.left = new FormAttachment(100, -240);
		btn_cancel.setLayoutData(formData1);
		btn_cancel.setText("Cancel");

		// Adding CLOSE action to this button.
		btn_cancel.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {

				shell.close();
				System.exit(0);
			}
		});

		btn_login.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {

				String username = txt_Username.getText();
				String password = txt_Password.getText();

				PortalConnector pc = new PortalConnector();

				JSONObject jsonObject = pc.connect(username, password);

				if (jsonObject != null) {
					User.username = username;

					// Shreedhan - Store password as well
					User.password = password;

					// Get preferences
					Preferences preferences = InstanceScope.INSTANCE.getNode("edu.uah.itsc.cmac.preferences");
					Preferences prefOwner = preferences.node("workspaceOwnerNode");
					String owner = prefOwner.get("workspaceOwner", User.username);

					// If the owner is a different user, give warning and ask user if he wants to continue
					if (!owner.equals(username)) {
						System.out.println("Not workspace owner");
						boolean answer = MessageDialog.openQuestion(shell, "Workspace Conflict",
							"This workspace is owned by '" + owner
								+ "'. If  you continue the workspace will be merged and it might generate conflicts. "
								+ "\nAre you sure you want to continue?");
						if (!answer) {
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
					// Do not create <username>_$folder$ anymore

					// S3 adminS3 = new S3();
					// ArrayList<String> allBuckets = adminS3.getAllBuckets();
					// for (String bucket : allBuckets) {
					// // If this the community bucket do not create the user folder
					// if (bucket.equalsIgnoreCase(S3.getCommunityBucketName()))
					// continue;
					// if (!adminS3.userFolderExists(username, bucket)) {
					// adminS3.uploadUserFolder(username, bucket);
					// }
					// }
					// if (!adminS3.userFolderExists(username, adminS3.getBucketName())){
					// adminS3.uploadUserFolder(username, adminS3.getBucketName());
					// }
					shell.close();

					String homeDir = System.getProperty("user.home");
					File jgitFile = new File(homeDir + "/" + ".jgit");
					if (jgitFile.exists()) {
						jgitFile.delete();
					}
					try {
						jgitFile.createNewFile();
						String content = "accesskey: " + User.awsAccessKey + "\nsecretkey: " + User.awsSecretKey;
						FileWriter fw = new FileWriter(jgitFile.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(content);
						bw.close();
					}
					catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					if (!jgitFile.exists()) {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
							"Cannot load your credentials");
					}

					System.out.println("Connecting to XMPP-----------------------");
					// XMPPClient xc = new XMPPClient(username,password);
					// try {
					// xc.connect1();
					// } catch (Exception e1) {
					// // TODO Auto-generated catch block
					// System.err.println("XMPP Connection failed-----------------------");
					// e1.printStackTrace();
					// }

				}
				else
					MessageDialog.openError(shell, "Error", "Could not login!");

			}
		});

		btn_createAccount.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				CreateAccountDialog createAccountDialog = new CreateAccountDialog(shell);
				createAccountDialog.createContents();
			}
		});

		// Label for copyright info
		final CLabel clbl_Message = new CLabel(cmp_Login, SWT.NONE);
		clbl_Message.setAlignment(SWT.RIGHT);
		final RowData rowData_6 = new RowData();
		rowData_6.width = 208;
		clbl_Message.setLayoutData(rowData_6);
		clbl_Message.setText("Copyright");

		// Drawing a region which will
		// form the base of the login
		Region region = new Region();
		Rectangle pixel = new Rectangle(1, 1, 530, 210);
		region.add(pixel);
		shell.setRegion(region);

		// Adding ability to move shell around
		Listener l = new Listener() {
			Point	origin;

			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.MouseDown:
					origin = new Point(e.x, e.y);
					break;
				case SWT.MouseUp:
					origin = null;
					break;
				case SWT.MouseMove:
					if (origin != null) {
						Point p = display.map(shell, null, e.x, e.y);
						shell.setLocation(p.x - origin.x, p.y - origin.y);
					}
					break;
				}
			}
		};

		// Adding the listeners
		// to all visible components
		composite.addListener(SWT.MouseDown, l);
		composite.addListener(SWT.MouseUp, l);
		composite.addListener(SWT.MouseMove, l);

		// img_Label.addListener(SWT.MouseDown, l);
		// img_Label.addListener(SWT.MouseUp, l);
		// img_Label.addListener(SWT.MouseMove, l);

		// Positioning in the center of the screen.
		// This for the 1024 resolution only. Later,
		// I plan to make generic so, that it takes
		// the resolution and finds the center of
		// the screen.
		shell.setLocation(320, 290);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		region.dispose();
	}
}