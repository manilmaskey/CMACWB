/**
 * 
 */
package edu.uah.itsc.cmac.actions;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.LoginDialog;

/**
 * @author mmaskey
 *
 */
public class CMACActionDelegate extends ActionDelegate {

	/**
	 * 
	 */
	public CMACActionDelegate() {
		// TODO Auto-generated constructor stub
		
	if (User.sessionID == null || User.sessionID.equals("")){
		LoginDialog loginDialog = new LoginDialog(PlatformUI.createDisplay());
		loginDialog.createContents();
	}
	}


	
	
	
}
