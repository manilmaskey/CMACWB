package edu.uah.itsc.cmac.glm;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "edu.uah.itsc.cmac.glm.Perspective";

	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}
 
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	public IAdaptable getDefaultPageInput() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}
	
}
