package edu.uah.itsc.cmac.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.navigator.resources.actions.EditActionProvider;
import org.eclipse.ui.internal.registry.ActionSetRegistry;
import org.eclipse.ui.internal.registry.IActionSetDescriptor;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

/**
 * Create the Edit actions (Cut/Copy/Paste) 
 * and register then globally in the workbench using CMACEditActionProvider.
 * <p/>
 * Then, removes the Copy/Paste contributions in the pop-up menu.
 */
public class CMACEditActionProvider extends EditActionProvider {
   public void fillContextMenu(IMenuManager menu) { 
	   super.fillContextMenu(menu);

   NavigatorView view = (NavigatorView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("edu.uah.itsc.cmac.NavigatorView");
   IStructuredSelection selection = (IStructuredSelection)view.getViewer().getSelection();
   if (selection.getFirstElement() instanceof IProject){
	   IProject prj = (IProject)selection.getFirstElement();

	   if (prj.getName().equals(S3.communityBucketName) || prj.getName().equals(S3.bucketName)) {
		    menu.remove("org.eclipse.ui.CopyAction");
		   	menu.remove("org.eclipse.ui.PasteAction");
		   	menu.remove("org.eclipse.ui.DeleteResourceAction");
	   }
   }
   else if  (selection.getFirstElement() instanceof IFolder){
	   IFolder selectedFolder = (IFolder)selection.getFirstElement();
	   if (selectedFolder.getName().equals(User.username)){
		    menu.remove("org.eclipse.ui.CopyAction");
		   	menu.remove("org.eclipse.ui.PasteAction");
		   	menu.remove("org.eclipse.ui.DeleteResourceAction");		   
	   }
	   if (selectedFolder.getProject().getName().equals(S3.communityBucketName)){
		   menu.remove("org.eclipse.ui.DeleteResourceAction");
	   }
   }

  //menu.remove("org.eclipse.ui.WorkingSetActionSet");
   //menu.remove("org.eclipse.update.ui.softwareUpdates");
  // menu.remove("org.eclipse.ui.PasteAction");
   /*
   ActionSetRegistry reg = WorkbenchPlugin.getDefault().getActionSetRegistry();
   
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
   }
   */
   }
}