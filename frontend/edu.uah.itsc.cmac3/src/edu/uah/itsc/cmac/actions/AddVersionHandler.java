package edu.uah.itsc.cmac.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.util.GITUtility;

public class AddVersionHandler extends AbstractHandler {
	private static final String	REMOTE_URL	= "amazon-s3://.jgit@";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
		final Object firstElement = selection.getFirstElement();

		if (selection.size() != 1 || !(firstElement instanceof IFolder)) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
				"You can save a version for only one workflow at a time");
			return null;
		}

		addVersion(firstElement);

		return null;
	}

	private void addVersion(final Object firstElement) {
		final Shell shell = new Shell(Display.getDefault().getActiveShell());
		shell.setText("Save a version");
		shell.setLayout(new GridLayout(2, false));

		/* Version Name Label */
		Label versionNameLabel = new Label(shell, SWT.NONE);
		versionNameLabel.setText("Version Name");

		/* Version Name Text */
		final Text versionNameText = new Text(shell, SWT.BORDER);
		versionNameText.setLayoutData(new GridData(SWT.FILL, 20, true, false));

		Label spaceLabel = new Label(shell, SWT.NONE);
		spaceLabel.setText("");

		Label noteLabel = new Label(shell, SWT.NONE);
		noteLabel.setText("(Note: username will be added to the version name during search)");

		/* Comment Label */
		Label versionCommentLabel = new Label(shell, SWT.NONE);
		versionCommentLabel.setText("Comments");

		/* Comment Text */
		final Text commentText = new Text(shell, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 200;
		gridData.widthHint = 300;
		commentText.setLayoutData(gridData);

		/* Submit Button */
		Button submitButton = new Button(shell, SWT.NONE);
		submitButton.setText("Submit");

		submitButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				String versionName = versionNameText.getText();
				String comments = commentText.getText();
				IFolder selectedFolder = (IFolder) firstElement;
				String parentPath = selectedFolder.getParent().getLocation().toString();
				String repoName = selectedFolder.getName();
				try {
					GITUtility.commitLocalChanges(repoName, parentPath, "Commit before creating tag", User.username,
						User.userEmail);
					Ref ref = GITUtility.createTag(repoName, parentPath, User.username + "." + versionName, comments);
					String project = selectedFolder.getProject().getName();
					String repoRemotePath = REMOTE_URL + project;
					if (ref != null)
						GITUtility.push(repoName, parentPath, repoRemotePath);
				}
				catch (Exception exception) {
					exception.printStackTrace();
				}
				shell.close();
			}
		});

		/* Cancel Button */
		Button cancelButton = new Button(shell, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				shell.close();
			}
		});

		shell.pack();
		shell.open();
	}

}
