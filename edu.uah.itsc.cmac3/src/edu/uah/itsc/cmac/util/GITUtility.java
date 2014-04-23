/**
 * 
 */
package edu.uah.itsc.cmac.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.TransportException;
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
import org.eclipse.jgit.transport.URIish;

import edu.uah.itsc.aws.User;

/**
 * @author sshrestha
 * 
 */
public class GITUtility {
	public static void cloneRepository(String repoLocalPath, String repoCompleteRemotepath)
		throws InvalidRemoteException, TransportException, GitAPIException, URISyntaxException, IOException {
		// repoRemotepath = "amazon-s3://.jgit@cmac-test-experiment/shree/test3.git";
		CloneCommand command = Git.cloneRepository();
		command.setDirectory(new File(repoLocalPath));
		command.setURI(repoCompleteRemotepath);
		Git git = command.call();

		String remote = "origin";
		String branch = "refs/heads/master";
		Repository repository = git.getRepository();
		final StoredConfig config = repository.getConfig();
		RemoteConfig remoteConfig = new RemoteConfig(config, remote);
		RefSpec pushRefSpec = new RefSpec(branch + ":" + branch);
		URIish uri = new URIish(repoCompleteRemotepath);
		remoteConfig.addURI(uri);
		remoteConfig.addPushRefSpec(pushRefSpec);
		remoteConfig.update(config);
		config.save();
		repository.close();
	}

	public static void createLocalRepo(String repoName, String repoLocalPath) throws IOException {
		if (!validRepoName(repoName))
			return;
		File localPath = new File(repoLocalPath + "/" + repoName + "/.git");
		Repository repository = null;
		// create the directory
		try {
			repository = FileRepositoryBuilder.create(localPath);
			repository.create();
			System.out.println("Created a local repostiory in " + repository.getDirectory());
		}
		catch (Exception e) {
			System.out.println("Error - Cannot create repository" + e.getMessage());
			throw new IOException(e.getMessage());
		}
		repository.close();
	}

	public static void commitLocalChanges(String repoName, String repoLocalPath, String commitMessage)
		throws IOException, NoFilepatternException, GitAPIException {
		if (!validRepoName(repoName))
			return;
		File localPath = new File(repoLocalPath + "/" + repoName + "/.git");
		System.out.println("Commiting local changes in " + localPath);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository;
		repository = builder.setGitDir(localPath).findGitDir().build();
		Git git = new Git(repository);
		// git.rm().addFilepattern(".").call();
		git.add().addFilepattern(".").call();
		RevCommit commit = git.commit().setMessage(commitMessage)
			.setAuthor(User.username, User.username + "@itsc.uah.edu").call();
		System.out.println("Committed changes: " + commit.getFullMessage());
		repository.close();
	}

	public static void push(String repoName, String repoLocalPath, String repoRemotePath) throws Exception {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File localPath = new File(repoLocalPath + "/" + repoName + "/.git");
		Repository repository = builder.setGitDir(localPath).findGitDir().build();
		Git git = new Git(repository);
		Ref head = repository.getRef("refs/heads/master");

		if (head == null) {
			System.out.println("Nothing to push");
			throw new Exception("Not a valid tracking workflow");
		}
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

		int numRefSpec = remoteConfig.getPushRefSpecs().size();

		RefSpec pushRefSpec = new RefSpec(branch + ":" + branch);

		PushCommand pushCommand = git.push();
		if (numRefSpec <= 0) {
			// cmac-test-experiment/shree/test_s3jgit.git
			URIish uri = new URIish(repoRemotePath + "/" + repoName + ".git");
			remoteConfig.addURI(uri);
			remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/" + remote + "/*"));
			remoteConfig.addPushRefSpec(pushRefSpec);
			remoteConfig.update(config);
			config.save();
			pushCommand.setRemote(remote).setRefSpecs(pushRefSpec);
		}

		// RevCommit commit2 = git.commit().setMessage("Commit to push").call();
		Iterable<PushResult> resultIterable = pushCommand.call();
		PushResult result = resultIterable.iterator().next();
		// TrackingRefUpdate trackingRefUpdate = result.getTrackingRefUpdate(trackingBranch);
		System.out.println("Pushed local changes. " + result.getMessages());
		repository.close();
	}

	public static void pull(String repoName, String repoLocalPath, String repoRemotePath) {
		if (!validRepoName(repoName))
			return;
		File localPath = new File(repoLocalPath + "/" + repoName + "/.git");
		System.out.println("Pulling from " + repoRemotePath + " to " + localPath);
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			Repository repository = builder.setGitDir(localPath).findGitDir().build();
			Git git = new Git(repository);
			git.fetch().call();

			CheckoutCommand coCmd = git.checkout();
			coCmd.setName("master");
			coCmd.setCreateBranch(false);
			coCmd.call();

			MergeCommand mgCmd = git.merge();
			Ref originHead = repository.getRef("refs/remotes/origin/master");
			mgCmd.include(originHead.getObjectId());
			MergeResult res = mgCmd.call();

			if (res.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
				System.out.println("Conflict occurred. Merge failed " + res.getConflicts().toString());
			}
			else
				System.out.println("Pulled remote changes\n");

			repository.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean validRepoName(String repoName) {
		if (repoName.isEmpty()) {
			return false;
		}
		else
			return true;
	}

}
