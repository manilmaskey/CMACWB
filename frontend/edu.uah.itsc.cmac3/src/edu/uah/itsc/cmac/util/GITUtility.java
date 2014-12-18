/**
 * 
 */
package edu.uah.itsc.cmac.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.InvalidTagNameException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.PersonIdent;
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
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.URIish;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.Utilities;

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
		remoteConfig.setTagOpt(TagOpt.FETCH_TAGS);
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

	public static void commitLocalChanges(String repoName, String repoLocalPath, String commitMessage, String userName,
		String userEmail) throws IOException, NoFilepatternException, GitAPIException {
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
		RevCommit commit = git.commit().setMessage(commitMessage).setAuthor(userName, userEmail).call();
		System.out.println("Committed changes: " + commit.getFullMessage());
		repository.close();
	}

	public static void push(String repoName, String repoLocalPath, String repoRemotePath) throws IOException,
		URISyntaxException, InvalidRemoteException, TransportException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File localPath = new File(repoLocalPath + "/" + repoName + "/.git");
		Repository repository = builder.setGitDir(localPath).findGitDir().build();
		Git git = new Git(repository);

		// Get reference of head in master branch
		Ref head = repository.getRef("refs/heads/master");

		if (head == null) {
			System.out.println("Nothing to push");
			throw new InvalidConfigurationException("Not a valid tracking workflow");
		}

		// Get commit object from the head of the master branch
		RevWalk walk = new RevWalk(repository);
		RevCommit commit = walk.parseCommit(head.getObjectId());

		String remote = "origin";
		String branch = "refs/heads/master";
		String trackingBranch = "refs/remotes/" + remote + "/master";

		// Update heads of both local and remote master branch
		RefUpdate branchRefUpdate = repository.updateRef(branch);
		branchRefUpdate.setNewObjectId(commit.getId());
		branchRefUpdate.update();

		RefUpdate trackingBranchRefUpdate = repository.updateRef(trackingBranch);
		trackingBranchRefUpdate.setNewObjectId(commit.getId());
		trackingBranchRefUpdate.update();

		// Get the config file and remote config for 'origin' remote from the config
		final StoredConfig config = repository.getConfig();
		RemoteConfig remoteConfig = new RemoteConfig(config, remote);
		remoteConfig.setTagOpt(TagOpt.FETCH_TAGS);

		// Get number of remote config
		int numRefSpec = remoteConfig.getPushRefSpecs().size();

		RefSpec pushRefSpec = new RefSpec(branch + ":" + branch);

		PushCommand pushCommand = git.push();

		// if there are no remote config(s) create a new remote config for amazon S3
		if (numRefSpec <= 0) {
			URIish uri = new URIish(repoRemotePath + "/" + User.username + "/" + repoName + ".git");
			remoteConfig.addURI(uri);
			remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/" + remote + "/*"));
			remoteConfig.addPushRefSpec(pushRefSpec);
			remoteConfig.update(config);
			config.save();
		}

		pushCommand.setRemote(remote).setRefSpecs(pushRefSpec);

		// Push tags as well and set force push
		pushCommand.setPushTags();
		pushCommand.setForce(true);
		// RevCommit commit2 = git.commit().setMessage("Commit to push").call();
		Iterable<PushResult> resultIterable = pushCommand.call();
		PushResult result = resultIterable.iterator().next();
		// TrackingRefUpdate trackingRefUpdate = result.getTrackingRefUpdate(trackingBranch);
		System.out.println("Pushed local changes. " + result.getMessages());
		repository.close();
	}

	public static void pull(String repoName, String repoLocalPath) {
		if (!validRepoName(repoName))
			return;
		File localPath = new File(repoLocalPath + "/" + repoName + "/.git");
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			Repository repository = builder.setGitDir(localPath).findGitDir().build();
			Git git = new Git(repository);
			PullResult pullResult = git.pull().call();

			MergeResult mergeResult = pullResult.getMergeResult();

			if (mergeResult.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
				System.out.println("Conflict occurred. Merge failed " + mergeResult.getConflicts().toString());
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
		if (repoName == null || repoName.isEmpty()) {
			return false;
		}
		else
			return true;
	}

	public static Git getGit(String repoName, String repoLocalPath) {
		if (!validRepoName(repoName))
			return null;
		File localPath = new File(repoLocalPath + "/" + repoName + "/.git");
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		try {
			Repository repository = builder.setGitDir(localPath).findGitDir().build();
			Git git = new Git(repository);
			repository.close();
			return git;
		}
		catch (Exception e) {
			return null;

		}
	}

	public static Ref createTag(String repoName, String repoLocalPath, String versionName, String comments)
		throws ConcurrentRefUpdateException, InvalidTagNameException, NoHeadException, GitAPIException {
		if (!validRepoName(repoName))
			return null;
		Git git = getGit(repoName, repoLocalPath);
		Ref tagRef = git.tag().setName(versionName).setMessage(comments)
			.setTagger(new PersonIdent(User.username, User.userEmail)).call();
		git.getRepository().close();
		git.close();
		return tagRef;
	}

	public static void hardReset(String repoName, String repoLocalPath, String ref) {
		Git git = getGit(repoName, repoLocalPath);
		hardReset(git, ref);
		git.getRepository().close();
		git.close();
	}

	public static void hardReset(Git git, String ref) {
		try {
			// Ref resultRef = git.reset().setRef(ref).addPath(".").setMode(ResetType.HARD).call();
			Ref resultRef = git.reset().setRef(ref).setMode(ResetType.HARD).call();
			git.getRepository().close();
		}
		catch (Exception e) {
			e.printStackTrace();

		}
	}

	public static void revert(String repoName, String repoLocalPath, Ref commit) {
		Git git = getGit(repoName, repoLocalPath);
		try {
			RevCommit revertCommand = git.revert().include(commit).call();
			git.getRepository().close();
			git.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Collection<Ref> getTagList(String repoRemotePath) {
		Repository repository = null;
		try {
			File localPath = Utilities.createTempDir("EmptyRepository", false);

			repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
			repository.create();

			Collection<Ref> tagList = Git.wrap(repository).lsRemote().setTags(true).setRemote(repoRemotePath).call();
			repository.close();
			Utilities.deleteRecursive(localPath);
			return tagList;
		}
		catch (Exception e) {
			e.printStackTrace();
			repository.close();
			return null;
		}
	}

	/**
	 * Deletes files matched using pattern from the repository
	 * 
	 * @param repoName
	 * - Name of the repository
	 * @param repoLocalPath
	 * - Path of the repository <b>NOT</b> including the repository. If C:\projects\myProject is the actual repository
	 * location, pass "C:\projects" only
	 * @param pattern
	 * - Git pattern to be deleted
	 * @return - Returns DirCache returned by JGit's {@link org.eclipse.jgit.api.RmCommand#call()}
	 * @throws NoFilepatternException
	 * @throws GitAPIException
	 */
	public static DirCache delete(String repoName, String repoLocalPath, String pattern) throws NoFilepatternException,
		GitAPIException {
		Git git = getGit(repoName, repoLocalPath);
		DirCache cache = git.rm().addFilepattern(pattern).call();
		git.close();
		return cache;
	}

	public static void modifyRemote(String repoName, String repoLocalPath, String newRemotePath)
		throws NoFilepatternException, GitAPIException {
		Git git = getGit(repoName, repoLocalPath);
		if (git != null) {
			Repository repository = git.getRepository();
			// Get the config file, reset origin section with community bucket repository location
			StoredConfig config = repository.getConfig();
			config.unsetSection("remote", "origin");
			config.setString("remote", "origin", "url", newRemotePath);

			try {
				RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
				remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
				remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
				remoteConfig.setTagOpt(TagOpt.FETCH_TAGS);
				remoteConfig.update(config);
				config.save();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	public static void removeRemote(String repoName, String repoLocalPath) throws NoFilepatternException,
		GitAPIException {
		Git git = getGit(repoName, repoLocalPath);
		if (git != null) {
			Repository repository = git.getRepository();
			// Get the config file, reset origin section with community bucket repository location
			StoredConfig config = repository.getConfig();
			config.unsetSection("remote", "origin");

			try {
				config.save();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}