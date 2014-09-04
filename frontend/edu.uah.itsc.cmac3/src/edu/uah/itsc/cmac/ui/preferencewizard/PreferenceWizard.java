package edu.uah.itsc.cmac.ui.preferencewizard;

import java.util.HashMap;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.osgi.service.prefs.BackingStoreException;

import edu.uah.itsc.cmac.Utilities;

public class PreferenceWizard extends Wizard {

	private AmazonPreferencesWizardPage	amazonPreferencesWizardPage;
	private PortalPreferencesWizardPage	portalPreferencesWizardPage;
	private PreferencesVerificationPage	preferencesValidationPage;
	private HashMap<String, String>		s3Map;
	private HashMap<String, String>		portalMap;

	public PreferenceWizard() {
		super();
		setNeedsProgressMonitor(true);

	}

	@Override
	public String getWindowTitle() {
		return "Set preferences Wizard";
	}

	@Override
	public void addPages() {
		amazonPreferencesWizardPage = new AmazonPreferencesWizardPage();
		portalPreferencesWizardPage = new PortalPreferencesWizardPage();
		preferencesValidationPage = new PreferencesVerificationPage();

		addPage(amazonPreferencesWizardPage);
		addPage(portalPreferencesWizardPage);
		addPage(preferencesValidationPage);

	}

	@Override
	public IWizardPage getNextPage(IWizardPage currentPage) {
		if (currentPage == amazonPreferencesWizardPage)
			return portalPreferencesWizardPage;
		else if (currentPage == portalPreferencesWizardPage) {
			s3Map = amazonPreferencesWizardPage.getData();
			portalMap = portalPreferencesWizardPage.getData();
			preferencesValidationPage.setData(s3Map, portalMap);
			return preferencesValidationPage;
		}
		return null;
	}

	@Override
	public boolean performFinish() {
		if (s3Map == null || portalMap == null)
			return false;
		try {
			Utilities.savePreferences("s3", s3Map);
			Utilities.savePreferences("portal", portalMap);
		}
		catch (BackingStoreException e) {
			System.out.println("Cannot save the preferences.");
			e.printStackTrace();
		}

		return true;
	}

}
