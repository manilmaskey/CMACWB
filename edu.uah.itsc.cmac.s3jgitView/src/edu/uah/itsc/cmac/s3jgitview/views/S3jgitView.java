package edu.uah.itsc.cmac.s3jgitview.views;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.uah.itsc.cmac.util.GITUtility;

public class S3jgitView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String	ID			= "edu.uah.itsc.cmac.s3jgitview.views.S3jgitView";
	private static final String	REMOTE_URL	= "amazon-s3://.jgit@";
	private Text				repoNameText;

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		createWidgets(parent);
	}

	private void createWidgets(final Composite parent) {

		Label lblRepositoryName = new Label(parent, SWT.NONE);
		lblRepositoryName.setBounds(10, 10, 99, 20);
		lblRepositoryName.setText("Repository Name");

		repoNameText = new Text(parent, SWT.BORDER);

		Button btnCloneRepository = new Button(parent, SWT.NONE);
		btnCloneRepository.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					GITUtility.cloneRepository("C:\\temp\\test5", REMOTE_URL + "cmac-test-experiment/shree/test1250.git");
				}
				catch (InvalidRemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (TransportException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (GitAPIException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnCloneRepository.setText("Clone Repository");

		Button btnCreateLocalRepository = new Button(parent, SWT.NONE);
		btnCreateLocalRepository.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createLocalRepository(repoNameText.getText());
			}

			private void createLocalRepository(String repoName) {
				try {
					GITUtility.createLocalRepo("test1250", "C:\\temp\\");
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnCreateLocalRepository.setText("Create Local Repository");

		Button btnPushToS = new Button(parent, SWT.NONE);
		btnPushToS.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pushToS3(repoNameText.getText());
			}

			private void pushToS3(String repoName) {
				try {
					GITUtility.push("test1250", "C:\\temp", REMOTE_URL + "cmac-test-experiment/shree");
				}
				catch (InvalidRemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (TransportException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnPushToS.setText("Push to S3");

		Button btnPullFromS = new Button(parent, SWT.NONE);
		btnPullFromS.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pullFromS3(repoNameText.getText());
			}

			private void pullFromS3(String repoName) {
				GITUtility.pull("test1250", "C:\\temp", REMOTE_URL + "cmac-test-experiment/shree");
			}
		});
		btnPullFromS.setText("Pull from S3");

		Button btnCommit = new Button(parent, SWT.NONE);
		btnCommit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commitLocalChanges(repoNameText.getText());
			}

			private void commitLocalChanges(String repoName) {
				try {
					GITUtility.commitLocalChanges("test1250", "C:\\temp", "Testing commit");
				}
				catch (NoFilepatternException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (GitAPIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		btnCommit.setText("Commit local changes");
	}

	protected boolean validRepoName() {
		String repoName = repoNameText.getText();
		if (repoName.isEmpty()) {
			MessageDialog.openInformation(this.getViewSite().getShell(), "Error",
				"Please enter a valid repository name");
			return false;
		}
		else
			return true;
	}

	@Override
	public void setFocus() {

	}
}