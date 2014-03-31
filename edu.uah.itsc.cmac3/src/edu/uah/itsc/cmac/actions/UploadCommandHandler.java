package edu.uah.itsc.cmac.actions;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.util.GITUtility;

public class UploadCommandHandler extends AbstractHandler {
	private IStructuredSelection	selection	= StructuredSelection.EMPTY;
	private static final String		REMOTE_URL	= "amazon-s3://.jgit@";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

		Object object = selection.getFirstElement();
		Job job = new Job("Uploading...") {
			protected IStatus run(IProgressMonitor monitor) {
				if (selection.size() == 1) {

					Object firstElement = selection.getFirstElement();
					if (firstElement instanceof IFile) {
						return Status.CANCEL_STATUS;
					}
					if (firstElement instanceof IFolder) {
						IFolder folder = (IFolder) firstElement;
						S3 s3 = new S3();
						if (!folder.getParent().getName().equalsIgnoreCase(User.username)
							|| folder.getProject().getName().equalsIgnoreCase(s3.getCommunityBucketName()))
							return Status.CANCEL_STATUS;

						String repoName = folder.getName();
						String repoLocalPath = folder.getParent().getLocation().toString();
						String project = folder.getProject().getName();
						String repoRemotePath = REMOTE_URL + project + "/" + User.username;

						try {
							GITUtility.push(repoName, repoLocalPath, repoRemotePath);
						}
						catch (InvalidRemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}
						catch (TransportException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}
						catch (URISyntaxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}
						catch (GitAPIException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}

					}
				}
				monitor.done();
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();

		return object;
	}
}