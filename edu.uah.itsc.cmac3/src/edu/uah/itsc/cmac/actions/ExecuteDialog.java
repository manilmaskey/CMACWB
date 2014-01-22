/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;

import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;

import edu.uah.itsc.aws.EC2;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Workflow;

/**
 * @author sshrestha
 * 
 */
public class ExecuteDialog {
	private void addSpanData(Control comp) {
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
		data.horizontalSpan = 2;
		comp.setLayoutData(data);
	}

	public ExecuteDialog(final String path, final String file,
			final String folder, final String bucket,
			final IFolder folderResource, final IWorkbenchPage page) {
		final Shell shell = new Shell();
		shell.setText("Workflow Settings");
		shell.setLayout(new GridLayout(2, false));
		Label title = new Label(shell, SWT.NONE);
		title.setText("Title : ");
		final Text titleText = new Text(shell, SWT.BORDER);
		addSpanData(titleText);
		Label description = new Label(shell, SWT.NONE);
		description.setText("Description : ");
		final Text descText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP
				| SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		descText.setLayoutData(data);

		/*
		 * Shreedhan Add keyword label and keyword text
		 */
		Label keywordLabel = new Label(shell, SWT.NONE);
		keywordLabel.setText("Keywords");
		final Text keywordText = new Text(shell, SWT.BORDER);
		addSpanData(keywordText);
		Label instanceLabel = new Label(shell, SWT.NONE);
		instanceLabel.setText("Run on instance");
		final Combo instanceCombo = new Combo(shell, SWT.READ_ONLY);
		String[] instanceList = getInstanceList();
		if (instanceList.length > 0){
			instanceCombo.setItems(instanceList);
			instanceCombo.select(0);
		}
		addSpanData(instanceCombo);
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("  OK  ");
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		
		if (instanceList.length <= 0){
			ok.setEnabled(false);
			MessageDialog.openError(shell, "No running Instances of EC2", "There are no running instances of Amazon EC2. Cannot execute workflow");
			shell.close();
			return;
		}
		
		final String nodeID;
		HashMap<String, String> nodeMap = PortalUtilities.getPortalWorkflowDetails(path);
		if (nodeMap != null){
			nodeID = nodeMap.get("nid");
			titleText.setText((String)nodeMap.get("title"));
			keywordText.setText((String)nodeMap.get("keywords"));
			descText.setText(((String)nodeMap.get("description")).replaceAll("\\<.*?\\>", ""));
		}
		else nodeID = null;
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					System.out.println("Button clicked");
					EC2 amazonEC2 = new EC2();
					PortalPost portalPost = new PortalPost();
					String instanceNameTag = instanceCombo.getItem(instanceCombo.getSelectionIndex());
					String publicURL = amazonEC2.getInstancePublicURL(instanceNameTag);
					if (publicURL == null)
						throw new Exception("This instance does not have a public IP. Cannot execute workflow.");
					System.out.println(publicURL);
					Workflow workflow = new Workflow(titleText.getText(),
							descText.getText(), keywordText.getText());
					workflow.setPath(path);
					workflow.setShared(false);
					System.out.println(workflow.getJSON());
					if (nodeID != null) {
						portalPost.put(PortalUtilities.getNodeRestPoint() + "/"
								+ nodeID, workflow.getJSON());

					} else
						portalPost.post(PortalUtilities.getNodeRestPoint(),
								workflow.getJSON());
					portalPost.runCron();
					new ProgressMonitorDialog(shell).run(true, true,
							new LongRunningOperation(true, titleText.getText(),
									descText.getText(), file, folder, bucket,
									folderResource, page, publicURL));
					shell.close();
				} catch (Exception e) {
					MessageDialog.openError(shell, "Error", e.getMessage());
				}
			}
		});
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});

		shell.setSize(500, 400);

		shell.open();
	}

	private String[] getInstanceList() {
		EC2 amazonEC2 = new EC2();
		ArrayList<Instance> instances = amazonEC2.getInstances();
		String[] instanceString = new String[instances.size()];
		int count = 0;
		for (Instance instance: instances){
			List<Tag> tags = instance.getTags();
			for (Tag tag : tags) {
				if (tag.getKey().equalsIgnoreCase("name"))
					instanceString[count++] = tag.getValue();
			}
//			instanceString[count++] = instance.getKeyName();
		}
		return instanceString;
	}
	


}
