package edu.uah.itsc.cmac.s3jgitview.views;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefUpdate;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.TrackingRefUpdate;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.uah.itsc.aws.User;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

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

		Button btnCreateLocalRepository = new Button(parent, SWT.NONE);
		btnCreateLocalRepository.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createLocalRepository(repoNameText.getText());
			}

			private void createLocalRepository(String repoName) {
				if (!validRepoName())
					return;
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("cmac-test-experiment");
				File localPath = new File(project.getLocation() + "/" + User.username + "/" + repoName);
				Repository repository = null;
				// create the directory
				try {
					repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
					repository.create();
					System.out.println("Created repository: " + repository.getDirectory());
					project.refreshLocal(IFolder.DEPTH_INFINITE, null);
					MessageDialog.openInformation(parent.getShell(), "Created", "Created a local repostiory in "
						+ repository.getDirectory());
				}
				catch (Exception e) {
					MessageDialog.openError(parent.getShell(), "Error - Cannot create repository", e.getMessage());
				}
				repository.close();
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
				if (!validRepoName())
					return;
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("cmac-test-experiment");
				File localPath = new File(project.getLocation() + "/" + User.username + "/" + repoName + "/.git");
				System.out.println("Pushing to " + REMOTE_URL + " from " + localPath);
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				try {
					Repository repository = builder.setGitDir(localPath).findGitDir().build();

					Git git = new Git(repository);
					// git.push().setRemote("origin").call();
					Ref head = repository.getRef("refs/heads/master");
					// System.out.println("Found head: " + head);

					// a RevWalk allows to walk over commits based on some filtering that is defined
					RevWalk walk = new RevWalk(repository);
					RevCommit commit = walk.parseCommit(head.getObjectId());

					String remote = "origin";
					String branch = "refs/heads/master";
					String trackingBranch = "refs/remotes/" + remote + "/master";
					RefUpdate branchRefUpdate = repository.updateRef(branch);
					branchRefUpdate.setNewObjectId(commit.getId());
					branchRefUpdate.update();

					RefUpdate trackingBranchRefUpdate = repository.updateRef(trackingBranch);
					trackingBranchRefUpdate.setNewObjectId(commit.getId());
					trackingBranchRefUpdate.update();

					final StoredConfig config = repository.getConfig();
					RemoteConfig remoteConfig = new RemoteConfig(config, remote);
					// cmac-test-experiment/shree/test_s3jgit.git
					URIish uri = new URIish(REMOTE_URL + project.getName() + "/" + User.username + "/" + repoName
						+ "/.git");
					remoteConfig.addURI(uri);
					remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/" + remote + "/*"));
					remoteConfig.update(config);
					config.save();

					// RevCommit commit2 = git.commit().setMessage("Commit to push").call();

					RefSpec spec = new RefSpec(branch + ":" + branch);
					Iterable<PushResult> resultIterable = git.push().setRemote(remote).setRefSpecs(spec).call();

					PushResult result = resultIterable.iterator().next();
					TrackingRefUpdate trackingRefUpdate = result.getTrackingRefUpdate(trackingBranch);
					MessageDialog.openInformation(parent.getShell(), "Success",
						"Pushed local changes\n" + result.getMessages());
				}
				catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		btnPushToS.setText("Push to S3");

		// Button btnPullFromS = new Button(parent, SWT.NONE);
		// btnPullFromS.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// pullFromS3(repoNameText.getText());
		// }
		//
		// private void pullFromS3(String repoName) {
		// if (!validRepoName())
		// return;
		// IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("cmac-test-experiment");
		// File localPath = new File(project.getLocation() + "/" + User.username + "/" + repoName + "/.git");
		// System.out.println("Pulling from " + REMOTE_URL + " to " + localPath);
		// FileRepositoryBuilder builder = new FileRepositoryBuilder();
		// try {
		// Repository repository = builder.setGitDir(localPath).findGitDir().build();
		// Git git = new Git(repository);
		// git.pull().call();
		// MessageDialog.openInformation(parent.getShell(), "Success", "Pulled remote changes\n");
		// }
		// catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
		// btnPullFromS.setText("Pull from S3");

		Button btnCommit = new Button(parent, SWT.NONE);
		btnCommit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				commitLocalChanges(repoNameText.getText());
			}

			private void commitLocalChanges(String repoName) {
				if (!validRepoName())
					return;
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("cmac-test-experiment");
				File localPath = new File(project.getLocation() + "/" + User.username + "/" + repoName + "/.git");
				System.out.println("Commiting local changes in " + localPath);
				FileRepositoryBuilder builder = new FileRepositoryBuilder();
				try {
					Repository repository = builder.setGitDir(localPath).findGitDir().build();
					Git git = new Git(repository);
					git.add().addFilepattern(".").call();
					RevCommit commit = git.commit().setMessage("Test commit")
						.setAuthor(User.username, User.username + "@itsc.uah.edu").call();
					MessageDialog.openInformation(parent.getShell(), "Success",
						"Committed changes\n" + commit.getFullMessage());
				}
				catch (Exception e) {
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