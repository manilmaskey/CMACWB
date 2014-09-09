package edu.uah.itsc.cmac.ui.preferencewizard;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.cmac.util.FileUtility;

public class AmazonPreferencesWizardPage extends WizardPage {
	private Composite				container;
	private Label					communityBucketLabel;
	private Text					communityBucketText;
	private Label					awsAdminAccessKeyLabel;
	private Text					awsAdminAccessKeyText;
	private Label					awsAdminSecretKeyLabel;
	private Text					awsAdminSecretKeyText;
	private Label					awsAdminUserIDLabel;
	private Text					awsAdminUserIDText;
	private Label					backendExecuteURLLabel;
	private Text					backendExecuteURLText;
	private String					backendPrefix	= "http://[url]:[port]/";
	private HashMap<String, String>	map;
	private PreferenceWizard		preferenceWizard;

	protected AmazonPreferencesWizardPage(PreferenceWizard preferenceWizard) {
		super("Set Amazon Credentials");

		this.preferenceWizard = preferenceWizard;
		setTitle("AWS Credentials");
		setDescription("Provide your Amazon Web Services Credentials");

	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		GridData gridData = new GridData();
		gridData.widthHint = 300;

		communityBucketLabel = createLabel(container, "Community Bucket name");
		communityBucketText = createText(container, gridData, "Community bucket name. E.g. cmac-community", null);

		awsAdminAccessKeyLabel = createLabel(container, "AWS Admin Access Key");
		awsAdminAccessKeyText = createText(container, gridData, "AWS Access key provided by Amazon", null);

		awsAdminSecretKeyLabel = createLabel(container, "AWS Admin Secret Key");
		awsAdminSecretKeyText = createText(container, gridData, "AWS Secret key provided by Amazon", null);

		awsAdminUserIDLabel = createLabel(container, "AWS Admin User ID");
		awsAdminUserIDText = createText(container, gridData, "AWS user ID provided by Amazon", null);

		backendExecuteURLLabel = createLabel(container, "CWB Backend URL");
		Composite backendComposite = new Composite(container, SWT.NONE);
		backendComposite.setLayout(new GridLayout(2, false));
		backendExecuteURLLabel = createLabel(backendComposite, backendPrefix);

		GridData backendExecuteURLTextGridData = new GridData();
		backendExecuteURLTextGridData.widthHint = 195;
		backendExecuteURLText = createText(backendComposite, backendExecuteURLTextGridData, "Backend service URL",
			"Backend service URL where your applications will be executed. Eg. cmacBackend/services/execute");

		Button importButton = new Button(container, SWT.PUSH);
		importButton.setText("Import from file");
		importButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(container.getShell());
				String fileName = dialog.open();
				if (fileName != null) {
					try {
						String content = FileUtility.readTextFile(fileName);
						JSONObject preferenceObj = new JSONObject(content);
						JSONObject s3JsonObj = preferenceObj.getJSONObject("s3");
						JSONObject portalJsonObj = preferenceObj.getJSONObject("portal");
						HashMap<String, String> s3Map = new HashMap<String, String>();
						HashMap<String, String> portalMap = new HashMap<String, String>();
						Iterator<String> iter = s3JsonObj.keys();
						while (iter.hasNext()) {
							String key = (String) iter.next();
							s3Map.put(key, s3JsonObj.getString(key));
						}
						iter = portalJsonObj.keys();
						while (iter.hasNext()) {
							String key = (String) iter.next();
							portalMap.put(key, portalJsonObj.getString(key));
						}
						map = s3Map;
						preferenceWizard.setS3Map(s3Map);
						preferenceWizard.setPortalMap(portalMap);

						System.out.println(s3Map);
						System.out.println(portalMap);
						fillData();
					}
					catch (JSONException e) {
						e.printStackTrace();
					}
				}

			}
		});

		fillData();

		setControl(container);
		setPageComplete(false);

	}

	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(text);
		return label;
	}

	private Text createText(Composite parent, GridData gridData, String message, String toolTip) {
		final Text text = new Text(parent, SWT.BORDER);
		text.setLayoutData(gridData);
		text.setMessage(message);
		if (toolTip != null)
			text.setToolTipText(toolTip);
		else
			text.setToolTipText(message);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (checkData())
					setPageComplete(true);
				else
					setPageComplete(false);
			}
		});
		return text;
	}

	private boolean checkData() {
		if (isValid(communityBucketText.getText()) && isValid(awsAdminAccessKeyText.getText())
			&& isValid(awsAdminSecretKeyText.getText()) && isValid(awsAdminUserIDText.getText())
			&& isValid(backendExecuteURLText.getText()))
			return true;
		else
			return false;
	}

	private boolean isValid(String text) {
		if (text.isEmpty() || text.trim().isEmpty())
			return false;
		else
			return true;
	}

	public HashMap<String, String> getData() {
		if (map == null)
			map = new HashMap<String, String>();
		map.put("community_bucket_name", communityBucketText.getText());
		map.put("aws_admin_access_key", awsAdminAccessKeyText.getText());
		map.put("aws_admin_secret_key", awsAdminSecretKeyText.getText());
		map.put("aws_user_id", awsAdminUserIDText.getText());
		map.put("backend_execute_url_suffix", backendExecuteURLText.getText());
		map.put("backend_execute_url", backendPrefix + backendExecuteURLText.getText());
		return map;
	}

	public void setData(HashMap<String, String> map) {
		this.map = map;
	}

	public void fillData() {
		if (map == null)
			return;

		communityBucketText.setText(map.get("community_bucket_name"));
		awsAdminAccessKeyText.setText(map.get("aws_admin_access_key"));
		awsAdminSecretKeyText.setText(map.get("aws_admin_secret_key"));
		awsAdminUserIDText.setText(map.get("aws_user_id"));
		backendExecuteURLText.setText(map.get("backend_execute_url_suffix"));

	}

}
