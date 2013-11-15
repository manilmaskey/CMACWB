/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("  OK  ");
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		final String nodeID;
		HashMap<String, String> nodeMap = pathExistsinPortal(path);
		if (nodeMap != null) {
			String link = (String) nodeMap.get("link");
			String[] linkParts = link.split("/");
			System.out.println("link part" + linkParts[linkParts.length - 1]);
			nodeID = linkParts[linkParts.length - 1];
			titleText.setText((String) nodeMap.get("title"));
			descText.setText(((String) nodeMap.get("description")).replaceAll(
					"\\<.*?\\>", ""));
		} else
			nodeID = null;
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {

					System.out.println("Button clicked");
					PortalPost portalPost = new PortalPost();

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
									folderResource, page));

				} catch (Exception e) {
					MessageDialog.openError(shell, "Error", e.getMessage());
				}
				shell.close();
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

	private HashMap<String, String> pathExistsinPortal(String path) {

		String xmlText = PortalUtilities.getDataFromURL(PortalUtilities
				.getWorkflowFeedURL());
		Node node, parentNode;
		NodeList childNodes;
		HashMap<String, String> nodeMap = null;
		int i = 0, j = 0;
		int listSize = 0;
		String remotePath = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(new ByteArrayInputStream(
					xmlText.getBytes("utf-8")));
			document.getDocumentElement().normalize();

			NodeList nodeList = document.getElementsByTagName("guid");
			listSize = nodeList.getLength();
			if (nodeList.getLength() <= 0)
				return null;
			for (i = 0; i < listSize; i++) {
				node = nodeList.item(i);
				remotePath = node.getTextContent();
				remotePath = remotePath.trim();
				if (remotePath.equalsIgnoreCase(path)) {
					parentNode = node.getParentNode();
					nodeMap = new HashMap<String, String>();
					// System.out.println("parentNode: " + parentNode);
					childNodes = parentNode.getChildNodes();
					// System.out.println("No. of childnodes: "
					// + childNodes.getLength());
					for (j = 0; j < childNodes.getLength(); j++) {
						if (childNodes.item(j).getNodeName()
								.equalsIgnoreCase("link"))
							nodeMap.put("link", childNodes.item(j)
									.getFirstChild().getNodeValue());
						if (childNodes.item(j).getNodeName()
								.equalsIgnoreCase("title"))
							nodeMap.put("title", childNodes.item(j)
									.getFirstChild().getNodeValue());
						if (childNodes.item(j).getNodeName()
								.equalsIgnoreCase("description"))
							nodeMap.put("description", childNodes.item(j)
									.getFirstChild().getNodeValue());
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nodeMap;
	}

}
