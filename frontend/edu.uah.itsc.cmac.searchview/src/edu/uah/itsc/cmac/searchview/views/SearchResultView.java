/**
 * 
 */
package edu.uah.itsc.cmac.searchview.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.searchview.models.SearchResult;
import edu.uah.itsc.cmac.searchview.models.SearchResultInterface;
import edu.uah.itsc.cmac.ui.OtherWorkflowView;
import edu.uah.itsc.cmac.util.FileUtility;
import edu.uah.itsc.cmac.util.GITUtility;
import edu.uah.itsc.cmac.util.PropertyUtility;

/**
 * @author sshrestha
 * 
 */
public class SearchResultView extends ViewPart implements SearchResultInterface {

	private ExpandBar	bar;

	@Override
	public void createPartControl(Composite parent) {
		bar = new ExpandBar(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		bar.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

	@Override
	public void accept(ArrayList<SearchResult> searchResults) {
		System.out.println("Total no. of search results is : " + searchResults.size());
		/*
		 * Get all expand items currently in the bar. Dispose all the items. Note that the item should set expanded
		 * value to false, otherwise you will notice weird problems when the items are disposed
		 */

		ExpandItem[] items = bar.getItems();
		for (ExpandItem item : items) {
			item.setExpanded(false);
			item.dispose();
		}
		/*
		 * Create an ExpandItem for each of the searchresult and create the ui as required.
		 */
		for (final SearchResult searchResult : searchResults) {
			Composite composite = new Composite(bar, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));

			Text description = new Text(composite, SWT.NONE | SWT.WRAP);
			String descriptionString = "Title: " + searchResult.getTitle() + "\nOwner: " + searchResult.getCreator()
				+ "\nLast submitted by: " + searchResult.getSubmittor() + "\nDescription:\n"
				+ searchResult.getDescription().trim();
			description.setText(descriptionString);
			description.setEditable(false);

			HashMap<String, String> paths = getPaths(searchResult);
			final String remotePath = paths.get("remotePath");
			final String localPath = paths.get("localPath");
			final String bucketName = paths.get("bucketName");
			final String workflow = paths.get("workflow");

			GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
			textGridData.widthHint = 400;
			description.setLayoutData(textGridData);

			Collection<Ref> tagList = GITUtility.getTagList(remotePath);
			if (!tagList.isEmpty()) {
				Composite tagListComposite = new Composite(composite, SWT.BORDER);
				tagListComposite.setLayout(new RowLayout(SWT.VERTICAL));
				createTagComposite(tagListComposite, tagList, paths, searchResult.getCreator());

			}

			Button button = new Button(composite, SWT.PUSH);
			button.setText("Import Workflow");

			button.addSelectionListener(new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {
					try {
						createInNavigator(bucketName, workflow);
						Job job = new Job("Importing..") {
							@Override
							protected IStatus run(IProgressMonitor monitor) {
								try {
									GITUtility.cloneRepository(localPath, remotePath);
									setOwnerProperty(localPath, searchResult.getCreator());

//									IFolder userFolder = ResourcesPlugin.getWorkspace().getRoot()
//										.getProject(bucketName).getFolder(User.username);
//									userFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
									
									// We will have workflows under project directly now
									IProject project = ResourcesPlugin.getWorkspace().getRoot()
										.getProject(bucketName);
									project.refreshLocal(IProject.DEPTH_INFINITE, null);
									
									Display.getDefault().asyncExec(new Runnable() {

										@Override
										public void run() {
											OtherWorkflowView otherWorkflowView = (OtherWorkflowView) PlatformUI
												.getWorkbench().getActiveWorkbenchWindow().getActivePage()
												.findView("edu.uah.itsc.cmac.ui.OtherWorkflowView");
											otherWorkflowView.refreshOtherWorkflows();
										}
									});
								}
								catch (Exception e) {
									Display.getDefault().asyncExec(new Runnable() {

										@Override
										public void run() {
											MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
												"Error while importing");
										}
									});
								}
								monitor.done();
								return Status.OK_STATUS;
							}
						};
						job.setUser(true);
						job.schedule();

					}
					catch (Exception e) {
						e.printStackTrace();
						showError(e.getMessage());
					}
				}

				private void createInNavigator(String bucketName, String workflowName) {
					IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName);
					try {
						if (!project.exists()) {
							project.create(null);
						}
						project.open(null);
						IFolder folder = project.getFolder(workflowName);
						if (!folder.exists())
							folder.create(true, false, null);
						String folderPath = folder.getLocation().toString();
						File folderFile = new File(folderPath);
						if (!folderFile.exists())
							folderFile.mkdirs();
					}
					catch (CoreException e) {
						e.printStackTrace();
					}
				}
			});

			ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
			item.setText(searchResult.getTitle() + " by " + searchResult.getCreator());
			item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setControl(composite);

		}

	}

	private void setOwnerProperty(final String localPath, final String repoOwner) throws IOException {
		String workflowPropertyFileName = localPath + "/.cmacworkflow";
		String gitIgnoreFileName = localPath + "/.gitignore";
		File propFile = new File(workflowPropertyFileName);
		if (!propFile.exists())
			propFile.createNewFile();

		File gitIgnoreFile = new File(gitIgnoreFileName);
		if (!gitIgnoreFile.exists()) {
			gitIgnoreFile.createNewFile();
			FileUtility.writeTextFile(gitIgnoreFileName, ".cmacworkflow");
		}

		PropertyUtility propUtil = new PropertyUtility(workflowPropertyFileName);
		propUtil.setValue("owner", repoOwner);
	}

	private HashMap<String, String> getPaths(SearchResult searchResult) {
		String copyFromFolderPath = searchResult.getFolderPath();
		String folderToCopy = "";
		folderToCopy = copyFromFolderPath;
		int fromIndex = 0;

		// Remove first two elements separated by '/'
		// We assume that the first element is the bucket name and the second element is the user name.
		folderToCopy = folderToCopy.replaceFirst("^/+", "");
		fromIndex = folderToCopy.indexOf('/');
		fromIndex = folderToCopy.indexOf('/', fromIndex + 1);
		folderToCopy = folderToCopy.substring(fromIndex);
		// Remove all the / character in the beginning
		folderToCopy = folderToCopy.replaceFirst("^/+", "");

		copyFromFolderPath = copyFromFolderPath.replaceAll("^/+", "");
		fromIndex = copyFromFolderPath.indexOf('/');
		System.out.println("folderpath: " + copyFromFolderPath);

		String bucketName = copyFromFolderPath.substring(0, fromIndex);
		String remotePath = "amazon-s3://.jgit@" + S3.getCommunityBucketName() + "/" + copyFromFolderPath + ".git";
		String localPath = ResourcesPlugin.getWorkspace().getRoot().getLocation() + "/" + bucketName + "/"
			+ folderToCopy;
		System.out.println(remotePath + "\n" + localPath);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("localPath", localPath);
		map.put("remotePath", remotePath);
		map.put("bucketName", bucketName);
		map.put("workflow", folderToCopy);
		return map;
	}

	private void createTagComposite(Composite parent, Collection<Ref> tagList, final HashMap<String, String> paths,
		final String creator) {
		for (final Ref ref : tagList) {
			Composite tagComposite = new Composite(parent, SWT.BORDER);
			GridLayout layout = new GridLayout(3, false);
			tagComposite.setLayout(layout);
			Label versionNameLabel = new Label(tagComposite, SWT.NONE);
			versionNameLabel.setText("Version: ");

			Label versionLabel = new Label(tagComposite, SWT.NONE);
			String refName = ref.getName();
			String[] refNameParts = refName.split("/");
			String version = refNameParts[refNameParts.length - 1];
			versionLabel.setText(version);

			Button importTagButton = new Button(tagComposite, SWT.PUSH);
			Image image = new Image(parent.getDisplay(), getClass().getClassLoader().getResourceAsStream(
				"icons/import.png"));
			importTagButton.setImage(image);
			importTagButton.setToolTipText("Import version " + version);

			importTagButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					super.widgetSelected(e);
					String localPath = paths.get("localPath");
					String remotePath = paths.get("remotePath");
					String workflow = paths.get("workflow");
					String bucketName = paths.get("bucketName");
					try {
						GITUtility.cloneRepository(localPath, remotePath);
						setOwnerProperty(localPath, creator);
						GITUtility.hardReset(workflow, localPath, ref.getTarget().getName());
						ResourcesPlugin.getWorkspace().getRoot().getProject(bucketName)
							.refreshLocal(IFolder.DEPTH_INFINITE, null);
					}
					catch (Exception e1) {
						e1.printStackTrace();
						showError(e1.getMessage());
					}
				}
			});

		}
	}

	private void showError(String message) {
		MessageDialog.openError(Display.getCurrent().getActiveShell(), "Search Result View", message);
	}
}
