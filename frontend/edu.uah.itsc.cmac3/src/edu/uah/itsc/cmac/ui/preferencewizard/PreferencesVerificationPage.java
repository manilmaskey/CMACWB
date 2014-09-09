package edu.uah.itsc.cmac.ui.preferencewizard;

import java.util.HashMap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.cmac.util.FileUtility;

public class PreferencesVerificationPage extends WizardPage {
	private Composite			container;
	HashMap<String, String>		s3Map;
	HashMap<String, String>		portalMap;
	Text						preferencesText;
	private PreferenceWizard	preferenceWizard;

	protected PreferencesVerificationPage(PreferenceWizard preferenceWizard) {
		super("Verify details");

		this.preferenceWizard = preferenceWizard;
		setTitle("Verify Details");
		setDescription("Verify your credentials and details");
	}

	public void setData(HashMap<String, String> s3Map, HashMap<String, String> portalMap) {
		this.s3Map = s3Map;
		this.portalMap = portalMap;
		modifyPreferencesText();
	}

	private void modifyPreferencesText() {
		if (s3Map != null && portalMap != null) {
			String details = s3Map.toString() + ",\n" + portalMap.toString();
			details = details.replaceAll(",", ",\n\n").replaceAll("\\{", "").replaceAll("\\}", "");
			preferencesText.setText(details);
		}
		else
			preferencesText.setText("Please provide all the details");

	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 300;
		gridData.widthHint = 500;

		preferencesText = new Text(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		preferencesText.setEditable(false);
		preferencesText.setLayoutData(gridData);

		Button exportButton = new Button(container, SWT.PUSH);
		exportButton.setText("Export preferences to a file");
		exportButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(container.getShell());
				String fileName = dialog.open();
				if (fileName != null) {

					try {
						JSONObject s3MapJsonObj = new JSONObject(s3Map);
						JSONObject portalMapJsonObj = new JSONObject(portalMap);
						JSONObject preferenceObj = new JSONObject();
						preferenceObj.put("s3", s3MapJsonObj);
						preferenceObj.put("portal", portalMapJsonObj);
						FileUtility.writeTextFile(fileName, preferenceObj.toString(4));
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});

		setControl(container);
		setPageComplete(true);
	}

}
