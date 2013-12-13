package edu.uah.itsc.programformview.views;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Parameter;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Program;

public class ProgramFormView extends ViewPart {
	private FormToolkit toolkit;
	private ScrolledForm form;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Programs");
		// toolkit.decorateFormHeading(form);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);
		Dialog.applyDialogFont(form.getBody());
		Section s2 = createTableSection(form, toolkit, "Maintain Programs");

		// This call is needed for all the children
		Dialog.applyDialogFont(form.getBody());
	}

	private Section createTableSection(final ScrolledForm form2,
			final FormToolkit toolkit, String title) {
		GridData gd;
		final Section section = toolkit.createSection(form2.getBody(),
				Section.TWISTIE | Section.TITLE_BAR);
		section.setActiveToggleColor(toolkit.getHyperlinkGroup()
				.getActiveForeground());
		section.setToggleColor(toolkit.getColors().getColor(
				IFormColors.SEPARATOR));
		FormText description = toolkit.createFormText(section, false);
		description.setText("<form><p>Create a new <b>program</b></p></form>",
				true, false);
		section.setDescriptionControl(description);

		final Composite client = toolkit.createComposite(section, SWT.WRAP);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

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

		final Button parameterButton = toolkit.createButton(client,
				"This program has parameters", SWT.CHECK);
		final Button addNewParameterButton = toolkit.createButton(client,
				"Add a parameter", SWT.PUSH);
		addNewParameterButton.setVisible(false);

		final ArrayList<Text> parametersText = new ArrayList<Text>();
		final ArrayList<Label> parametersLabel = new ArrayList<Label>();
		final ArrayList<Text> optionsText = new ArrayList<Text>();
		final ArrayList<Label> optionsLabel = new ArrayList<Label>();
		final ArrayList<Combo> ioCombos = new ArrayList<Combo>();
		final ArrayList<Label> ioCombosLabel = new ArrayList<Label>();

		addNewParameterButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				Label ioComboLabel = toolkit
						.createLabel(client, "Input/Output");
				Combo io = new Combo(client, SWT.DROP_DOWN | SWT.READ_ONLY);
				io.add("Input Parameter", 0);
				io.add("Output Parameter", 1);
				Label parameterLabel = toolkit.createLabel(client,
						"Parameter Name");
				Text parameterText = toolkit.createText(client, "");
				Label optionLabel = toolkit.createLabel(client, "Option");
				Text optionText = toolkit.createText(client, "");

				parametersText.add(parameterText);
				parametersLabel.add(parameterLabel);
				optionsText.add(optionText);
				optionsLabel.add(optionLabel);
				ioCombos.add(io);
				ioCombosLabel.add(ioComboLabel);

				parameterText.setLayoutData(new GridData(200, 15));
				form.reflow(true);
				section.layout(true);
				form.layout(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		parameterButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean hasParameters = parameterButton.getSelection();
				System.out.println(hasParameters);
				addNewParameterButton.setVisible(hasParameters);
				if (!hasParameters && parametersText != null
						&& parametersText.size() > 0) {
					disposeParameters(section, parametersText, parametersLabel,
							optionsText, optionsLabel, ioCombos, ioCombosLabel);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		Button submitButton = toolkit.createButton(client, "Submit Program",
				SWT.PUSH);
		submitButton.setLayoutData(gd);
		submitButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ArrayList<Parameter> inputParameters = new ArrayList<Parameter>();
				ArrayList<Parameter> outputParameters = new ArrayList<Parameter>();
				for (int i = 0; i < parametersText.size(); i++) {
					// if (parametersText.get(i).getText().trim().equals(""))
					// continue;
					Parameter parameter = new Parameter();
					parameter.setTitle(parametersText.get(i).getText());
					parameter.setOption(optionsText.get(i).getText());

					System.out.println("Combo value: "
							+ ioCombos.get(i).getText()
							+ ioCombos.get(i).getSelectionIndex());
					if (ioCombos.get(i).getSelectionIndex() == 0)
						inputParameters.add(parameter);
					else
						outputParameters.add(parameter);
				}
				HttpResponse response = createProgram(inputParameters,
						outputParameters, titleText.getText(),
						descriptionText.getText(), contactText.getText(),
						urlText.getText(), pathText.getText(),
						versionText.getText());

				MessageBox message = new MessageBox(form2.getShell());
				if (response.getStatusLine().getStatusCode() == 200) {

					message.setMessage("Added a program successfully.");
					message.setText("Success");
					message.open();
					contactText.setText("");
					descriptionText.setText("");
					urlText.setText("");
					keywordsText.setText("");
					pathText.setText("");
					titleText.setText("");
					versionText.setText("");
					disposeParameters(section, parametersText, parametersLabel,
							optionsText, optionsLabel, ioCombos, ioCombosLabel);
					addNewParameterButton.setVisible(false);
					parameterButton.setSelection(false);
				} else {
					message.setMessage("Could not add the program.\n"
							+ response.toString());
					message.open();
					message.setText("Error");

				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});
		toolkit.paintBordersFor(client);
		section.setText(title);
		section.setClient(client);
		section.setExpanded(true);
		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
		return section;
	}

	/**
	 * @param section
	 * @param parametersText
	 * @param parametersLabel
	 * @param optionsText
	 * @param optionsLabel
	 * @param ioCombos
	 * @param ioCombosLabel
	 */
	private void disposeParameters(final Section section,
			final ArrayList<Text> parametersText,
			final ArrayList<Label> parametersLabel,
			final ArrayList<Text> optionsText,
			final ArrayList<Label> optionsLabel,
			final ArrayList<Combo> ioCombos,
			final ArrayList<Label> ioCombosLabel) {
		int i = 0;
		for (Text text : parametersText) {
			text.dispose();
		}
		for (Text text : optionsText) {
			text.dispose();
		}
		for (Combo ioCombo : ioCombos) {
			ioCombo.dispose();
		}
		for (Label label : parametersLabel) {
			label.dispose();
		}
		for (Label label : optionsLabel) {
			label.dispose();
		}
		for (Label label : ioCombosLabel) {
			label.dispose();
		}
		for (i = 0; i < parametersText.size(); i++) {
			parametersText.remove(i);
		}
		for (i = 0; i < optionsText.size(); i++) {
			optionsText.remove(i);
		}
		for (i = 0; i < ioCombos.size(); i++) {
			ioCombos.remove(i);
		}
		for (i = 0; i < optionsLabel.size(); i++) {
			optionsLabel.remove(i);
		}
		for (i = 0; i < ioCombosLabel.size(); i++) {
			ioCombosLabel.remove(i);
		}
		section.layout(true, true);
		form.layout(true);
	}

	private HttpResponse createProgram(ArrayList<Parameter> inputParameters,
			ArrayList<Parameter> outputParameters, String title,
			String description, String contactInfo, String docURL, String path,
			String version) {
		HttpResponse response = null;
		PortalPost portalPost = new PortalPost();
		ArrayList<Parameter> allParameters = new ArrayList<Parameter>();
		allParameters.addAll(inputParameters);
		allParameters.addAll(outputParameters);

		for (Parameter parameter : allParameters) {

			try {
				response = portalPost.post(PortalUtilities.getNodeRestPoint(),
						parameter.getJSON());
				if (response.getStatusLine().getStatusCode() != 200) {
					// TODO: Check the error status from Drupal
					return null;
				} else {
					byte[] byteResponse = new byte[(int) response.getEntity()
							.getContentLength()];
					int length = response.getEntity().getContent()
							.read(byteResponse);
					String stringResponse = new String(byteResponse);
					System.out.println(length + "\n" + stringResponse);
					JSONParser jsonParser = new JSONParser();
					JSONObject jsonResponse = (JSONObject) jsonParser
							.parse(stringResponse);
					String nidParameter = (String) jsonResponse.get("nid");
					System.out.println(nidParameter);
					parameter.setNid(nidParameter);
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Program program = new Program();
		program.setContactInfo(contactInfo);
		program.setCreator(User.username);
		program.setDescription(description);
		program.setDocURL(docURL);
		program.setPath(path);
		program.setSubmittor(User.username);
		program.setTitle(title);
		program.setVersion(version);
		program.setInputParameters(inputParameters);
		program.setOutputParameters(outputParameters);
		response = portalPost.post(
				PortalUtilities.getNodeRestPoint(),
				program.getQueryString());

		return response;

	}

	@Override
	public void setFocus() {

	}

}