package edu.uah.itsc.cmac.actions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jgit.lib.Ref;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.Utilities;
import edu.uah.itsc.cmac.model.ExecuteCommand;
import edu.uah.itsc.cmac.util.GITUtility;

public class ScriptAction extends Action {
	protected static String	ID	= "Action.script";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getText() {
		return "Execute";
	}

	@Override
	public void run() {
		final Shell shell = new Shell();
		shell.setText("Workflow Settings");
		shell.setLayout(new GridLayout(3, false));

		Label title = new Label(shell, SWT.NONE);
		title.setText("Title : ");
		final Text titleText = new Text(shell, SWT.BORDER);
		addSpanData(titleText);

		Label description = new Label(shell, SWT.NONE);
		description.setText("Description : ");
		final Text descText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		descText.setLayoutData(new GridData(GridData.FILL_BOTH));
		org.eclipse.swt.widgets.Button ok = new org.eclipse.swt.widgets.Button(shell, SWT.PUSH);
		ok.setText("  OK  ");
		org.eclipse.swt.widgets.Button cancel = new org.eclipse.swt.widgets.Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		// Button ok = new org.eclipse.swt.widgets.Button(parent, style)
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// ////////
				// try {
				IEditorPart myeditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getActiveEditor();
				if (myeditor.isDirty())// myeditor.doSave(monitor);
					myeditor.doSaveAs();

				IEditorInput input = myeditor.getEditorInput();

				String myfilelocation = ((IFileEditorInput) input).getFile().getLocation().toString();

				IProject proj = ((IFileEditorInput) input).getFile().getProject();

				File temp = new File(myfilelocation);
				String folderName = temp.getParentFile().getName();

				// new ProgressMonitorDialog(shell).run(true, true, new
				// LongRunningOperation(true,titleText.getText(),descText.getText(),myfilelocation,folderName,proj.getName(),proj.getFolder(folderName)));
				// }
				// catch (InvocationTargetException e) {
				// MessageDialog.openError(shell, "Error", e.getMessage());
				// }
				// catch (InterruptedException e) {
				// MessageDialog.openInformation(shell, "Cancelled", e.getMessage());
				// }
				shell.close();
				// /////
				// /////

			}
		});
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				System.out.println("cancel button pressed");
				shell.close();
			}
		});

		shell.pack();
		shell.open();
	}

	private void addSpanData(Control comp) {
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		comp.setLayoutData(data);
	}

}

class LongRunningOperation implements IRunnableWithProgress {
	private static final int	TOTAL_TIME	= 10000;
	private static final int	INCREMENT	= 500;
	private boolean				indeterminate;
	private String				file;
	private String				bucket;
	private String				folder;
	private String				title;
	private String				desc;
	private String				versionName;
	private String				comments;
	private IFolder				folderResource;
	private IWorkbenchPage		page;
	private String				publicURL;
	private String				repoOwner;
	private boolean				isSharedRepo;

	public LongRunningOperation(boolean indeterminate, String title, String desc, String versionName, String comments,
		String file, String folder, String bucket, IFolder folderResource, IWorkbenchPage page, String publicURL,
		String repoOwner, boolean isSharedRepo) {
		this.indeterminate = indeterminate;
		this.title = title;
		this.desc = desc;
		this.versionName = versionName;
		this.comments = comments;
		this.file = file;
		this.folder = folder;
		this.bucket = bucket;
		this.folderResource = folderResource;
		this.page = page;
		this.publicURL = publicURL;
		this.repoOwner = repoOwner;
		this.isSharedRepo = isSharedRepo;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		monitor.beginTask("Executing workflow...", indeterminate ? IProgressMonitor.UNKNOWN : TOTAL_TIME);

		String repoName = folderResource.getName();
		String repoLocalPath = folderResource.getParent().getLocation().toString();
		try {
			String repoRemotePath = "amazon-s3://.jgit@";
			if (isSharedRepo) {
				repoRemotePath = repoRemotePath + "cmac-community/";
			}
			repoRemotePath = repoRemotePath + bucket;

			GITUtility.pull(repoName, repoLocalPath);
			GITUtility.commitLocalChanges(repoName, repoLocalPath, "Commit before creating tag", User.username,
				User.userEmail);
			Ref ref = GITUtility.createTag(repoName, repoLocalPath, User.username + "." + versionName, comments);
			GITUtility.push(repoName, repoLocalPath, repoRemotePath);

			ExecuteCommand execCommand = new ExecuteCommand.Builder(bucket, repoName, file).shared(isSharedRepo)
				.name(User.username).mail(User.userEmail).repoOwner(repoOwner)
				.accessKey(Utilities.getKeyValueFromPreferences("s3", "aws_admin_access_key"))
				.secretKey(Utilities.getKeyValueFromPreferences("s3", "aws_admin_secret_key"))
				.largeBucketName(Utilities.getKeyValueFromPreferences("s3", "large_bucket_name")).build();
			StringEntity seData = new StringEntity(execCommand.toJSONString());
			seData.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			String postURL = Utilities.getKeyValueFromPreferences("s3", "backend_execute_url");
			postURL = postURL.replace("[url]", publicURL).replace("[port]", "8080");
			HttpResponse response = postData(postURL, seData);
			if (response.getStatusLine().getStatusCode() == 200) {
				GITUtility.pull(repoName, repoLocalPath);
				String message = EntityUtils.toString(response.getEntity());
				createLargeFileLinks(message, folderResource);
				folderResource.refreshLocal(IFolder.DEPTH_INFINITE, null);
			}
			else {
				System.out.println("Error" + response.getStatusLine() + "\n"
					+ response.getEntity().getContent().toString());
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		monitor.done();
		if (monitor.isCanceled())
			throw new InterruptedException("The long running operation was cancelled");
	}

	private void createLargeFileLinks(String message, IFolder folderResource) {
		try {
			JSONObject jsonResponse = new JSONObject(message);
			jsonResponse = jsonResponse.getJSONObject("response");
			final String largeDir = jsonResponse.getString("largeDir");
			JSONArray largeFiles = jsonResponse.getJSONArray("files");
			if (largeFiles.length() <= 0)
				return;

			IFolder largeDirFolder = folderResource.getFolder(largeDir);
			if (!largeDirFolder.exists())
				largeDirFolder.create(true, true, null);

			for (int i = 0; i < largeFiles.length(); i++) {
				JSONObject file = (JSONObject) largeFiles.get(i);
				String fileName = file.getString("name");
				int fileSize = file.getInt("size");
				String relativePath = file.getString("path");
				String s3Location = file.getString("s3Location");
				IFile ifile = largeDirFolder.getFile(relativePath);
				if (ifile.exists())
					ifile.delete(true, null);
				if (!ifile.getParent().exists()) {
					File parentDir = new File(ifile.getParent().getLocation().toString());
					parentDir.mkdirs();
					largeDirFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);

				}
				File localFile = new File(ifile.getLocation().toString());
				if (!localFile.exists())
					localFile.createNewFile();
				ifile.createLink(ifile.getLocation(), IResource.NONE, null);
				ifile.setPersistentProperty(new QualifiedName("edu.uah.itsc.cmac3.needS3Download", "needS3Download"),
					"true");
				ifile.setPersistentProperty(new QualifiedName("edu.uah.itsc.cmac3.s3Location", "s3Location"),
					s3Location);
			}

			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
						"Some large files were generated while executing this program.\nThese files are stored in directory '"
							+ largeDir + "' and will be downloaded when you try to open them.");
				}
			});

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private HttpResponse postData(String url, StringEntity seData) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(url);
		HttpClient httpClient = new DefaultHttpClient();
		BasicHttpContext mHttpContext = new BasicHttpContext();
		httpPost.setEntity(seData);
		HttpResponse response = httpClient.execute(httpPost, mHttpContext);
		return response;
	}

}