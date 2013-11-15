package edu.uah.itsc.programformview.views;

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

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Program;

public class ProgramFormView extends ViewPart {
	private FormToolkit toolkit;
	private Form form;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.setText("Programs");
		toolkit.decorateFormHeading(form);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		Dialog.applyDialogFont(form.getBody());
		Section s2 = createTableSection(form, toolkit, "Maintain Programs");
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
		description.setText("<form><p>Create a new <b>program</b></p></form>",
				true, false);
		section.setDescriptionControl(description);

		Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		// layout.numColumns = 2;

		client.setLayout(layout);

		Label titleLabel = toolkit.createLabel(client, "Title");
		final Text titleText = toolkit.createText(client, "");
		titleText.setLayoutData(new GridData(200, 15));

		Label descriptionLabel = toolkit.createLabel(client, "Description");
		final Text descriptionText = toolkit.createText(client, "", SWT.MULTI);
		descriptionText.setLayoutData(new GridData(200, 150));

		Label pathLabel = toolkit.createLabel(client, "Path");
		final Text pathText = toolkit.createText(client, "");
		pathText.setLayoutData(new GridData(200, 15));

		// Label programsLabel = toolkit.createLabel(client, "Programs");
		// final Text programsText = toolkit.createText(client, "");
		// programsText.setLayoutData(new GridData(200, 15));

		Label urlLabel = toolkit.createLabel(client, "Doc URL");
		final Text urlText = toolkit.createText(client, "");
		urlText.setLayoutData(new GridData(200, 15));

		Label contactLabel = toolkit.createLabel(client, "Contact Info");
		final Text contactText = toolkit.createText(client, "");
		contactText.setLayoutData(new GridData(200, 15));

		Label keywordsLabel = toolkit.createLabel(client, "Keywords");
		final Text keywordsText = toolkit.createText(client, "");
		keywordsText.setLayoutData(new GridData(200, 15));

		Label versionLabel = toolkit.createLabel(client, "Version");
		final Text versionText = toolkit.createText(client, "");
		versionText.setLayoutData(new GridData(200, 15));

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		Button submitButton = toolkit.createButton(client, "Submit Program",
				SWT.PUSH);
		submitButton.setLayoutData(gd);
		submitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String response = null;
				Program program = new Program();
				program.setContactInfo(contactText.getText());
				program.setCreator(User.username);
				program.setDescription(descriptionText.getText());
				program.setDocURL(urlText.getText());
				program.setKeywords(keywordsText.getText());
				program.setPath(pathText.getText());
				program.setSubmittor(User.username);
				program.setTitle(titleText.getText());
				program.setVersion(versionText.getText());
				PortalPost portalPost = new PortalPost();
				MessageBox message = new MessageBox(form2.getShell());
				try {
					response = portalPost.post(
							PortalUtilities.getNodeRestPoint(),
							program.getJSON());
					if (response != null) {

						message.setMessage("Added a program successfully.");
						message.setText("Success");
						message.open();
						contactText.setText("");
						descriptionText.setText("");
						// programsText.setText("");
						urlText.setText("");
						keywordsText.setText("");
						pathText.setText("");
						titleText.setText("");
						versionText.setText("");
					} else {
						message.setMessage("Could not add the program.");
						message.open();
						message.setText("Error");

					}
				} catch (JSONException e1) {
					message.setMessage("Could not add the program.");
					message.setText("Error");
					message.open();
					e1.printStackTrace();
				}
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

}