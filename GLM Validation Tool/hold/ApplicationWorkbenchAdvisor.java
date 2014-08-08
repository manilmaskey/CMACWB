package hold;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
//import org.eclipse.ui.ide.IDE;
//import org.eclipse.ui.internal.WorkbenchPlugin;
//import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
//import org.eclipse.ui.internal.registry.ActionSetRegistry;
//import org.eclipse.ui.internal.ide.model.WorkbenchAdapterBuilder;
import org.osgi.framework.Bundle;

import edu.uah.itsc.glmvalidationtool.views.DataView;
import edu.uah.itsc.glmvalidationtool.views.ValidationView;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "glm_validation_tool.views.Perspective";

    public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
//        return new ApplicationWorkbenchWindowAdvisor(configurer);
        return new WorkbenchWindowAdvisor(configurer);
    }

	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}
	public IAdaptable getDefaultPageInput() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}
	
	public void initialize(IWorkbenchConfigurer configurer) {
		 
		org.eclipse.ui.ide.IDE.registerAdapters();
//		WorkbenchAdapterBuilder.registerAdapters();

		final String ICONS_PATH = "icons/full/";
		final String PATH_OBJECT = ICONS_PATH + "obj16/";
		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);
		declareWorkbenchImage(configurer, ideBundle,
				IDE.SharedImages.IMG_OBJ_PROJECT, PATH_OBJECT + "prj_obj.gif",
				true);
		declareWorkbenchImage(configurer, ideBundle,
				IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT
						+ "cprj_obj.gif", true);

		// supposed to remove actions causing errors on launch, but not working...

		
	    ActionSetRegistry reg = WorkbenchPlugin.getDefault()
        .getActionSetRegistry();

	    
		 DataView dataView;
		 ValidationView validationView;
		 while((dataView = (DataView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("DataView"))==null);
//		 dataView.linkValidationView();
		 System.out.println("found data View ");
		 while ((validationView = (ValidationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("ValidationView"))==null);
		 validationView.linkDataView();
		 System.out.println("found validation View ");

/*
		IActionSetDescriptor[] actionSets = reg.getActionSets();
		String[] removeActionSets = new String[] {
		    "org.eclipse.ui.cheatsheets.actionSet",
		    "org.eclipse.ui.edit.text.actionSet.annotationNavigation",
		   "org.eclipse.ui.edit.text.actionSet.convertLineDelimitersTo",
		      "org.eclipse.ui.WorkingSetActionSet",
		    "org.eclipse.update.ui.softwareUpdates", };

		for (int i = 0; i < actionSets.length; i++)
		{
		    boolean found = false;
		    for (int j = 0; j < removeActionSets.length; j++)
		    {
		        if (removeActionSets[j].equals(actionSets[i].getId()))
		            found = true;
		    }
		
		
		    if (!found)
		        continue;
		    IExtension ext = actionSets[i].getConfigurationElement()
		            .getDeclaringExtension();
		    reg.removeExtension(ext, new Object[] { actionSets[i] });
		    System.out.println("removing action " + actionSets[i].getId());
		}
		
*/		
	}
 
	private void declareWorkbenchImage(IWorkbenchConfigurer configurer_p,
			Bundle ideBundle, String symbolicName, String path, boolean shared) {
		URL url = ideBundle.getEntry(path);
		
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		configurer_p.declareImage(symbolicName, desc, shared);
	}	
	
}
