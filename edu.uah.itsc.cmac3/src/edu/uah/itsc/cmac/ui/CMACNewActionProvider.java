package edu.uah.itsc.cmac.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.navigator.resources.actions.NewActionProvider;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

/**
 * Create the New actions
 * and register then globally in the workbench using CMACEditActionProvider.
 * <p/>
 * Then, removes the contributions in the pop-up menu.
 */
public class CMACNewActionProvider extends NewActionProvider {
   public void fillContextMenu(IMenuManager menu) { 
	  // super.fillContextMenu(menu);
	   menu.remove("group.port");	
  	   menu.remove("group.build");
  	   menu.remove("group.reorganize");
	   menu.remove("group.properties");
	   
	   NavigatorView view = (NavigatorView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView("edu.uah.itsc.cmac.NavigatorView");
	   IStructuredSelection selection = (IStructuredSelection)view.getViewer().getSelection();
	   if (selection.getFirstElement() instanceof IProject){
//		   IProject prj = (IProject)selection.getFirstElement();
		// Remove this menu for every project
//		   if (prj.getName().equals(S3.communityBucketName) || prj.getName().equals(S3.bucketName)) 
		       menu.remove("group.new");
	   }
	   else if (selection.getFirstElement() instanceof IFolder ){
		   IFolder selectedFolder = (IFolder)selection.getFirstElement();

		   if (selectedFolder.getProject().getName().equals(S3.communityBucketName)) 
		       menu.remove("group.new");
		   
		   
		   if (selectedFolder.getName().equals(User.username)){
			  // menu.remove
		   }
	   }
   }
}