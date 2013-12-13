package edu.uah.itsc.cmac.experimentformview.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Experiment;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;

public class ExperimentFormView extends ViewPart {
	private FormToolkit toolkit;
	private Form form;
	private S3 s3;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.setText("Experiments");
		toolkit.decorateFormHeading(form);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		Dialog.applyDialogFont(form.getBody());
		Section s2 = createTableSection(form, toolkit, "Maintain Experiments");
		// This call is needed for all the children
		Dialog.applyDialogFont(form.getBody());
	}

	private Section createTableSection(final Form form2, FormToolkit toolkit,
			String title) {
		GridData gd;
		Section section = toolkit.createSection(form2.getBody(),
				Section.TWISTIE | Section.TITLE_BAR);
		section.setActiveToggleColor(toolkit.getHyperlinkGroup()
				.getActiveForeground());
		section.setToggleColor(toolkit.getColors().getColor(
				IFormColors.SEPARATOR));
		FormText description = toolkit.createFormText(section, false);
		description.setText(
				"<form><p>Create a new <b>experiment</b></p></form>", true,
				false);
		section.setDescriptionControl(description);

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		client.setLayout(layout);

		Label titleLabel = toolkit.createLabel(client, "Title");
		final Text titleText = toolkit.createText(client, "");
		titleText.setLayoutData(new GridData(200, 15));

		Label descriptionLabel = toolkit.createLabel(client, "Description");
		final Text descriptionText = toolkit.createText(client, "", SWT.MULTI);
		descriptionText.setLayoutData(new GridData(200, 150));

		// Label creatorLabel = toolkit.createLabel(client, "Creator");
		// final Text creatorText = toolkit.createText(client, "");
		// creatorText.setLayoutData(new GridData(200, 15));

		// Label workflowsLabel = toolkit.createLabel(client, "Workflows");
		// final Text workflowsText = toolkit.createText(client, "");
		// workflowsText.setLayoutData(new GridData(200, 15));

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		Button submitButton = toolkit.createButton(client, "Submit Experiment",
				SWT.PUSH);
		submitButton.setLayoutData(gd);
		submitButton.addSelectionListener(new SelectionListener() {
			HttpResponse response;
			PortalPost portalPost = new PortalPost();
			Experiment experiment = new Experiment();

			@Override
			public void widgetSelected(SelectionEvent e) {
				final MessageBox message = new MessageBox(form2.getShell());
				experiment.setTitle(titleText.getText());
				experiment.setDescription(descriptionText.getText());
				experiment.setCreator(User.portalUserID);
				// experiment.setWorkflows(workflowsText.getText());
				try {
					final JSONObject jsonExperiment = experiment.getJSON();
					Job job = new Job("Creating experiment..") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							s3 = new S3();
							AmazonS3 amazonS3Service = s3.getAmazonS3Service();
							try {
								String bucketName = jsonExperiment.get("title")
										.toString();
								amazonS3Service.createBucket(bucketName);
								s3.addGroupPolicy("cmac_collaborators",
										"policy_cmac_collaborators",
										getPolicyToAdd(bucketName));
							} catch (AmazonServiceException e) {
								e.printStackTrace();
								message.setMessage("Could not add the experiment.\n"
										+ e.getMessage());
								message.setText("Error");
								return Status.CANCEL_STATUS;
							} catch (AmazonClientException e) {
								e.printStackTrace();
								message.setMessage("Could not add the experiment.\n"
										+ e.getMessage());
								message.setText("Error");
								return Status.CANCEL_STATUS;
							} catch (JSONException e) {
								e.printStackTrace();
								message.setMessage("Could not add the experiment.\n"
										+ e.getMessage());
								message.setText("Error");
								return Status.CANCEL_STATUS;
							}
							monitor.worked(100);
							response = portalPost.post(
									PortalUtilities.getNodeRestPoint(),
									jsonExperiment);
							String stringResponse = response.toString();
							if (response == null
									|| !stringResponse
											.matches("^HTTP/\\d\\.\\d\\s200\\sOK.*")) {
								return Status.CANCEL_STATUS;
							}
							monitor.worked(50);
							monitor.done();
							return Status.OK_STATUS;
						}
					};
					job.setUser(true);
					job.schedule();
					job.join();
				} catch (Exception execption) {
					execption.printStackTrace();
					message.setMessage("Could not add the experiment.");
					message.setText("Error");
					message.open();
				}
				if (message.getText().length() > 0)
					message.open();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		// gd = new GridData(GridData.FILL_BOTH);
		// Table t = toolkit.createTable(client, SWT.NULL);
		// gd.heightHint = 200;
		// gd.widthHint = 100;
		// gd.horizontalSpan = 2;
		// t.setLayoutData(gd);
		toolkit.paintBordersFor(client);
		section.setText(title);
		section.setClient(client);
		section.setExpanded(true);
		// section.addExpansionListener(new ExpansionAdapter() {
		// public void expansionStateChanged(ExpansionEvent e) {
		// // ((IManagedForm) form2).reflow(false);
		// }
		// });
		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		return section;
	}

	@Override
	public void setFocus() {

	}

	private String getPolicyToAdd(String bucketName) {
		BufferedReader bufferedReader = null;
		String fileContent = "";
		String line = "";
		try {
			InputStream stream = this.getClass().getClassLoader()
					.getResourceAsStream("policy.template");
			bufferedReader = new BufferedReader(new InputStreamReader(stream));
			while ((line = bufferedReader.readLine()) != null) {
				fileContent = fileContent + "\n" + line;
			}
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		fileContent = fileContent.replace("%cmac-bucketName-cmac%", bucketName);
		return fileContent;
	}

}