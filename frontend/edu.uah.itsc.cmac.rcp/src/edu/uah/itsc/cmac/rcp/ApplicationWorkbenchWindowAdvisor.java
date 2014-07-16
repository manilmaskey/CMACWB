package edu.uah.itsc.cmac.rcp;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import edu.uah.itsc.aws.User;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		// configurer.setInitialSize(new Point(1200, 1000));
		// configurer.setShowCoolBar(false);
		// configurer.setShowStatusLine(false);
		configurer.setTitle("Collaborative Workbench - " +  ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + " - " +  User.username + " [" + User.userEmail + "]");
	}
	
	/**
	* Overriden to maximize the window when shwon.
	*/
	@Override
	public void postWindowCreate() {
	IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
	IWorkbenchWindow window = configurer.getWindow();
	window.getShell().setMaximized(true);
	}
}
