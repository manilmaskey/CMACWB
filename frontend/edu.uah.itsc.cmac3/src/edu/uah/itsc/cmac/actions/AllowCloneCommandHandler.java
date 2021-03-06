package edu.uah.itsc.cmac.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.handlers.HandlerUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Notification;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUser;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;
import edu.uah.itsc.cmac.ui.Utilities;
import edu.uah.itsc.cmac.util.GITUtility;

public class AllowCloneCommandHandler extends AbstractHandler implements IHandler {
	private IStructuredSelection			selection		= StructuredSelection.EMPTY;
	private IFolder							selectedFolder;
	private static ArrayList<PortalUser>	portalUserList;
	private HashSet<PortalUser>				grantees;
	private HashMap<String, String>			workflowMap;
	private String							workflowOwner	= null;
	private String							workflowPath;
	private TableViewer						viewer;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);

		if (selection.size() == 1) {
			final Object firstElement = selection.getFirstElement();
			if (firstElement instanceof IFile) {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Information",
					"You can only select workflows!");
			}
			else if (firstElement instanceof IFolder) {
				selectedFolder = (IFolder) firstElement;
				IFolder gitFolder = selectedFolder.getFolder(".git");
				if (!gitFolder.exists()) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
						"You can only select tracking workflows!");
					return null;
				}

				workflowOwner = S3.getWorkflowOwner(selectedFolder.getLocation().toString());
				if (!workflowOwner.equals(User.username)) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
						"You can only send workflows which you own. This workflow is owned by '" + workflowOwner + "'");
					return null;
				}
				createWindow();

			}
		}
		return null;
	}

	private void createWindow() {
		final Shell shell = new Shell(Display.getDefault().getActiveShell());
		shell.setText(selectedFolder.getName() + " - Allow cloning to users");
		shell.setLayout(new GridLayout(1, true));
		grantees = new HashSet<PortalUser>();

		Label userLabel = new Label(shell, SWT.NONE);
		userLabel.setText("Select (Ctrl/Command + Click) users to send");

		Label emptyLabel1 = new Label(shell, SWT.NONE);

		if (portalUserList == null) {
			portalUserList = PortalUtilities.getUserList();
			portalUserList.sort(new Comparator<PortalUser>() {
				@Override
				public int compare(PortalUser user1, PortalUser user2) {
					return user1.getFullName().toLowerCase().compareTo(user2.getFullName().toLowerCase());
				}
			});
		}

		workflowPath = "/" + selectedFolder.getProject().getName() + "/" + User.username + "/"
			+ selectedFolder.getName();
		workflowMap = PortalUtilities.getPortalWorkflowDetails(workflowPath);
		ArrayList<String> prevGranteesName = new ArrayList<String>();
		if (workflowMap != null) {
			prevGranteesName.addAll(Arrays.asList(workflowMap.get("allow_clone_to").split(",\\s*")));
		}

		createTable(shell);

		Label emptyLabel2 = new Label(shell, SWT.NONE);

		if (!prevGranteesName.isEmpty())
			selectPrevious(prevGranteesName);

		Composite buttonComposite = new Composite(shell, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, true));

		Button selectAllButton = new Button(buttonComposite, SWT.PUSH);
		selectAllButton.setText("Select All");
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Table table = viewer.getTable();
				table.selectAll();
				table.showSelection();
			}
		});

		Button selectNoneButton = new Button(buttonComposite, SWT.PUSH);
		selectNoneButton.setText("Select None");
		selectNoneButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				viewer.setSelection(StructuredSelection.EMPTY);
			}
		});

		Button okButton = new Button(buttonComposite, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selectedItems = (IStructuredSelection) viewer.getSelection();
				Iterator iter = selectedItems.iterator();
				while (iter.hasNext()) {
					PortalUser user = (PortalUser) iter.next();
					grantees.add(user);
					System.out.println(user.getFullName());
				}

				Utilities.setCursorBusy(true);
				addClonePermission();
				createNotifications();
				Utilities.setCursorBusy(false);
				shell.close();
			}
		});

		Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		shell.pack();
		shell.open();

	}

	private void selectPrevious(ArrayList<String> prevGranteesName) {
		Table table = viewer.getTable();

		TableItem[] items = table.getItems();
		ArrayList<TableItem> selectedItems = new ArrayList<TableItem>();
		for (TableItem tableItem : items) {
			PortalUser user = (PortalUser) tableItem.getData();
			if (prevGranteesName.contains(user.getUsername()))
				selectedItems.add(tableItem);
		}

		TableItem[] selectedArray = new TableItem[1];
		if (!selectedItems.isEmpty()) {
			table.setSelection(selectedItems.toArray(selectedArray));
			table.showSelection();
		}
	}

	private void createTable(Composite parent) {
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI | SWT.BORDER);

		createColumns();
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridData layoutData = new GridData();
		layoutData.grabExcessVerticalSpace = true;
		layoutData.verticalAlignment = SWT.TOP;
		layoutData.heightHint = 200;
		table.setLayoutData(layoutData);

		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setInput(portalUserList);
	}

	public void createColumns() {
		String[] titles = { "Username", "Name" };
		int[] bounds = { 100, 200 };

		// Username
		TableViewerColumn column = createTableViewerColumn(titles[0], bounds[0], 0);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PortalUser user = (PortalUser) element;
				return user.getUsername();
			}
		});

		// Name
		column = createTableViewerColumn(titles[1], bounds[1], 1);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				PortalUser user = (PortalUser) element;
				return user.getFullName();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(final String title, int bound, final int colNumber) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private void addClonePermission() {
		String allowCloneTo = getAllowedUsers();

		String nodeID = null;
		Workflow workflow = new Workflow();
		workflow.setAllowCloneTo(allowCloneTo);
		PortalPost portalPost = new PortalPost();
		HttpResponse response = null;
		try {
			if (workflowMap != null) {
				nodeID = workflowMap.get("nid");
				workflow.setPath(workflowMap.get("path"));
				workflow.setTitle(workflowMap.get("title"));
				workflow.setDescription(workflowMap.get("description"));
				workflow.setKeywords(workflowMap.get("keywords"));
				workflow.setShared(Integer.parseInt(workflowMap.get("isShared")) > 0 ? true : false);
				workflow.setCreator(workflowMap.get("creator"));
				workflow.setSubmittor(workflowMap.get("submittor"));
				response = portalPost.put(PortalUtilities.getNodeRestPoint() + "/" + nodeID, workflow.getJSON());
			}
			else {
				workflow.setPath(workflowPath);
				workflow.setTitle(selectedFolder.getName());
				workflow.setDescription(selectedFolder.getName());
				workflow.setShared(false);
				workflow.setCreator(User.username);
				workflow.setSubmittor(User.username);
				response = portalPost.post(PortalUtilities.getNodeRestPoint(), workflow.getJSON());
			}
			if (response.getStatusLine().getStatusCode() != 200) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error",
					"Error. Received something other than 200 OK" + "\n" + response.getStatusLine());
				System.out.println("Error. Received something other than 200 OK" + "\n" + response.getStatusLine());
				return;
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Could not send the workflow");
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private String getAllowedUsers() {
		Iterator<PortalUser> iter = grantees.iterator();
		String allowCloneTo = null;
		while (iter.hasNext()) {
			PortalUser user = iter.next();
			allowCloneTo = (allowCloneTo == null) ? user.getPortalUserID() : allowCloneTo + ","
				+ user.getPortalUserID();
		}
		return allowCloneTo;
	}

	private <T> String joinSet(Set<T> set, String delimiter) {
		Iterator<T> iter = set.iterator();
		String joinedString = null;
		while (iter.hasNext()) {
			T element = iter.next();
			joinedString = (joinedString == null) ? element.toString() : joinedString + delimiter + element.toString();
		}
		return joinedString;
	}

	private void createNotifications() {
		String url = PortalUtilities.getNotificationURL() + "?field_path_value=" + workflowPath;
		String jsonText = PortalUtilities.getDataFromHTTP(url);
		try {
			JSONObject jsonObj = new JSONObject(jsonText);
			String nid = null;
			String notSeenBy = null;
			String recipients = null;
			String allowCloneTo = getAllowedUsers();
			if (jsonObj != null) {
				JSONArray notificationArray = jsonObj.getJSONArray("notifications");
				if (notificationArray != null && notificationArray.length() > 0) {
					JSONObject notificationObj = notificationArray.getJSONObject(0).getJSONObject("notification");
					nid = notificationObj.getString("nid");
					notSeenBy = notificationObj.getString("not_seen_by");
					recipients = notificationObj.getString("recipient");

					// Recipients from existing notification
					Set<String> recipientSet = new HashSet<String>(Arrays.asList(recipients.split(",\\s*")));
					// Recipients who have not seen the notification
					Set<String> notSeenBySet = new HashSet<String>(Arrays.asList(notSeenBy.split(",\\s*")));
					// Newly selected recipients
					Set<String> allowCloneToSet = new HashSet<String>(Arrays.asList(allowCloneTo.split(",\\s*")));
					// Remove the recipients who have not seen the notification leaving recipients who have seen the
					// notification
					recipientSet.removeAll(notSeenBySet);
					// Remove the set of users who have already seen the notification
					allowCloneToSet.removeAll(recipientSet);
					// Update not seen by user list
					notSeenBy = joinSet(allowCloneToSet, ",");
				}
			}

			if (notSeenBy == null)
				notSeenBy = allowCloneTo;
			Notification notification = new Notification();
			notification.setPath(workflowPath);
			notification.setDescription("Clone permission for " + selectedFolder.getName());
			notification.setTitle(selectedFolder.getName() + " - notification");
			notification.setNotSeenBy(notSeenBy);
			notification.setRecipients(allowCloneTo);
			notification.setWorkflow(selectedFolder.getName());
			notification.setOwner(User.username);
			String remotePath = "amazon-s3://.jgit@";
			if (workflowMap != null && Integer.parseInt(workflowMap.get("isShared")) > 0)
				remotePath = remotePath + S3.getCommunityBucketName() + "/";
			remotePath = remotePath + selectedFolder.getProject().getName();
			notification.setClonePath(remotePath + "/" + User.username + "/" + selectedFolder.getName());
			System.out.println(notification.getJSON());

			PortalPost post = new PortalPost();
			String restURL = PortalUtilities.getNodeRestPoint();
			HttpResponse response = null;
			if (nid != null) {
				response = post.put(restURL + "/" + nid, notification.getJSON());
			}
			else {
				response = post.post(restURL, notification.getJSON());
			}
			GITUtility.commitLocalChanges(selectedFolder.getName(),
				selectedFolder.getParent().getLocation().toString(), "Commit for allow clone", User.username,
				User.userEmail);
			GITUtility.push(selectedFolder.getName(), selectedFolder.getParent().getLocation().toString(), remotePath);

			if (response.getStatusLine().getStatusCode() != 200) {
				System.out.println("Error:\n" + EntityUtils.toString(response.getEntity()));
			}
			System.out.println("Success:\n" + EntityUtils.toString(response.getEntity()));

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
