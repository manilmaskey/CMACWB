/**
 * 
 */
package edu.uah.itsc.cmac.searchview.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.searchview.models.SearchResult;
import edu.uah.itsc.cmac.searchview.models.SearchResultInterface;

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
			description.setText(searchResult.getDescription().trim());
			description.setEditable(false);

			GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
			textGridData.widthHint = 400;
			description.setLayoutData(textGridData);

			Button button = new Button(composite, SWT.PUSH);
			button.setText("Import Workflow");

			button.addSelectionListener(new SelectionAdapter() {
				private void buildTree(String copyFromFolderPath, String folderToCopy, IResource resource) {
					System.out.println(copyFromFolderPath);
					System.out.println(folderToCopy);
					IFolder userFolder = ((IProject) resource).getFolder(User.username);
					IFolder folderToCreate = userFolder.getFolder(folderToCopy);
					if (!folderToCreate.exists()) {
						createFolderPath(userFolder, folderToCopy);
					}
					if (folderToCreate.exists()) {
						downloadFolder(copyFromFolderPath, folderToCreate);
					}

				}

				private void downloadFolder(String copyFromFolderPath, IFolder copyToFolder) {
					S3 s3 = new S3();
					AmazonS3 amazonS3Service = s3.getAmazonS3Service();

					ListObjectsRequest lor = new ListObjectsRequest();
					lor.setBucketName(s3.getCommunityBucketName());
					lor.setDelimiter(s3.getDelimiter());
					lor.setPrefix(copyFromFolderPath.replaceAll("$/+", "") + "/");

					ObjectListing filteredObjects = amazonS3Service.listObjects(lor);

					for (S3ObjectSummary objectSummary : filteredObjects.getObjectSummaries()) {
						String currentResource = objectSummary.getKey();
						String[] fileNameArray = currentResource.split("/");
						String fileName = fileNameArray[fileNameArray.length - 1];
						if (currentResource.indexOf("_$folder$") > 0) {

						}
						else {
							String fullFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
								+ java.io.File.separator + copyToFolder.getProject().getName() + java.io.File.separator
								+ User.username + java.io.File.separator + copyToFolder.getName()
								+ java.io.File.separator + fileName;
							System.out.println("Downloading file " + currentResource);
							System.out.println("fullFilePath: " + fullFilePath);

							s3.downloadFile(s3.getCommunityBucketName(), currentResource, fullFilePath);

						}

					}
					try {
						copyToFolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
					}
					catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				private void createFolderPath(IFolder folder, String folderToAdd) {
					if (folderToAdd.indexOf("/") <= 0) {
						IFolder newFolder = folder.getFolder(folderToAdd);
						if (!newFolder.exists()) {
							try {
								newFolder.create(true, true, null);
							}
							catch (CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					else
						while (folderToAdd.indexOf("/") > 0) {
							folderToAdd = folderToAdd.substring(folderToAdd.indexOf("/") + 1);

						}
				}

				public void widgetSelected(SelectionEvent event) {
					try {
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

						String bucketName = null;
						copyFromFolderPath = copyFromFolderPath.replaceAll("^/+", "");
						fromIndex = copyFromFolderPath.indexOf('/');
						bucketName = copyFromFolderPath.substring(0, fromIndex);
						System.out.println("folderpath: " + copyFromFolderPath);
						buildTree(copyFromFolderPath, folderToCopy, ResourcesPlugin.getWorkspace().getRoot()
							.getProject(bucketName));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			ExpandItem item = new ExpandItem(bar, SWT.NONE, 0);
			item.setText(searchResult.getTitle());
			item.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setControl(composite);

		}

	}

}
