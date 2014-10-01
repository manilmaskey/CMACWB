package edu.uah.itsc.cmac.ui.preferencewizard;

import java.util.HashMap;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PortalPreferencesWizardPage extends WizardPage {

	private Composite				container;
	private Label					domainLabel;
	private Text					domainText;
	private Label					siteLabel;
	private Text					siteText;
	private Label					restURLLabel;
	private Text					restURLText;
	private Label					cronURLLabel;
	private Text					cronURLText;
	private HashMap<String, String>	map;
	private PreferenceWizard		preferenceWizard;

	protected PortalPreferencesWizardPage(PreferenceWizard preferenceWizard) {
		super("Set Portal Details");

		this.preferenceWizard = preferenceWizard;
		setTitle("Portal Details");
		setDescription("Provide your Portal Details");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		GridData gridData = new GridData();
		gridData.widthHint = 300;

		domainLabel = createLabel(container, "Portal domain");
		domainText = createText(container, gridData, "Portal domain. E.g. 54.208.76.40 or itsc.uah.edu", null);

		siteLabel = createLabel(container, "Portal URL");
		siteText = createText(container, gridData, "Portal URL. E.g. http://54.208.76.40/d7/cmac", null);

		restURLLabel = createLabel(container, "Portal REST URL");
		restURLText = createText(container, gridData, "E.g. http://54.208.76.40/d7/cmac/rest", null);

		cronURLLabel = createLabel(container, "Portal CRON URL");
		cronURLText = createText(container, gridData,
			"Portal CRON URL. E.g. http://54.208.76.40/d7/cmac/cron.php?cron_key=xxxxxxxxxxxxxxxxxxxxxxx", null);

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
		if (isValid(domainText.getText()) && isValid(siteText.getText()) && isValid(cronURLText.getText())
			&& isValid(restURLText.getText()))
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
		String domain = domainText.getText();
		String siteURL = siteText.getText();
		String restURL = restURLText.getText();
		String cronURL = cronURLText.getText();

		if (siteURL.endsWith("/"))
			siteURL = siteURL.substring(0, siteURL.length() - 1);

		if (restURL.endsWith("/"))
			restURL = restURL.substring(0, restURL.length() - 1);

		String workflowURL = siteURL + "/workflow";
		String tokenURL = siteURL + "/services/session/token";
		String userListURL = siteURL + "/user_view";
		String notificationURL = siteURL + "/notification";

		String nodeRestURL = restURL + "/node";
		String termRestURL = restURL + "/term";
		String experimentURL = siteURL + "/experiment";
		String loginURL = restURL + "/user/login";
		String userURL = restURL + "/user";
		String searchURL = restURL + "/search_node/retrieve.json";

		map.put("portal_domain", domain);
		map.put("portal_cron_url", cronURL);
		map.put("portal_url", siteURL);
		map.put("portal_rest_url", restURL);

		map.put("workflow_url", workflowURL);
		map.put("token_url", tokenURL);
		map.put("user_list_url", userListURL);
		map.put("notification_url", notificationURL);

		map.put("node_rest_url", nodeRestURL);
		map.put("term_rest_url", termRestURL);
		map.put("experiment_url", experimentURL);
		map.put("portal_login_url", loginURL);
		map.put("portal_user_url", userURL);
		map.put("search_url", searchURL);

		return map;
	}

	public void setData(HashMap<String, String> map) {
		this.map = map;
		fillData();
	}

	public void fillData() {
		if (map == null)
			return;

		domainText.setText(map.get("portal_domain"));
		siteText.setText(map.get("portal_url"));
		restURLText.setText(map.get("portal_rest_url"));
		cronURLText.setText(map.get("portal_cron_url"));

	}
}
