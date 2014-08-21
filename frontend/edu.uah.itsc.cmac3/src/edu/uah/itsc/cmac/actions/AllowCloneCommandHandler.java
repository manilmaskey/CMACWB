package edu.uah.itsc.cmac.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
import edu.uah.itsc.cmac.util.GITUtility;

public class AllowCloneCommandHandler extends AbstractHandler implements IHandler {
	private IStructuredSelection			selection		= StructuredSelection.EMPTY;
	private IFolder							selectedFolder;
	private static ArrayList<PortalUser>	portalUserList;
	private HashMap<Button, PortalUser>		buttons;
	private HashSet<PortalUser>				grantees;
	private HashMap<String, String>			workflowMap;
	private String							workflowOwner	= null;
	private String							workflowPath;

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
		userLabel.setText("Check users to send");

		Label emptyLabel1 = new Label(shell, SWT.NONE);

		Composite userComposite = new Composite(shell, SWT.BORDER);
		GridLayout layout = new GridLayout(8, false);
		userComposite.setLayout(layout);

		if (portalUserList == null) {
			portalUserList = PortalUtilities.getUserList();
			portalUserList.sort(new Comparator<PortalUser>() {

				@Override
				public int compare(PortalUser user1, PortalUser user2) {
					return user1.getUsername().toLowerCase().compareTo(user2.getUsername().toLowerCase());
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

		buttons = new HashMap<Button, PortalUser>();
		for (final PortalUser user : portalUserList) {
			Label nameLabel = new Label(userComposite, SWT.NONE);
			nameLabel.setText(user.getUsername());
			Button checkButton = new Button(userComposite, SWT.CHECK);
			if (prevGranteesName.contains(user.getUsername())) {
				checkButton.setSelection(true);
				grantees.add(user);
			}
			checkButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Button button = (Button) e.widget;
					if (button.getSelection())
						grantees.add(user);
					else
						grantees.remove(user);
				}
			});
			buttons.put(checkButton, user);
		}

		Label emptyLabel2 = new Label(shell, SWT.NONE);

		if (!portalUserList.isEmpty()) {
			Composite buttonComposite = new Composite(shell, SWT.NONE);
			buttonComposite.setLayout(new GridLayout(2, true));

			Button selectAllButton = new Button(buttonComposite, SWT.PUSH);
			selectAllButton.setText("Select All");
			selectAllButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Map.Entry<Button, PortalUser> entry : buttons.entrySet()) {
						Button button = entry.getKey();
						PortalUser user = entry.getValue();
						grantees.add(user);
						button.setSelection(true);
					}
				}
			});

			Button selectNoneButton = new Button(buttonComposite, SWT.PUSH);
			selectNoneButton.setText("Select None");
			selectNoneButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					for (Map.Entry<Button, PortalUser> entry : buttons.entrySet()) {
						Button button = entry.getKey();
						PortalUser user = entry.getValue();
						grantees.remove(user);
						button.setSelection(false);
					}
				}
			});

			Button okButton = new Button(buttonComposite, SWT.PUSH);
			okButton.setText("OK");
			okButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					addClonePermission();
					createNotifications();
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
		}
		shell.pack();
		shell.open();

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
