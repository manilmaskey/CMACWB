package edu.uah.itsc.cmac.actions;

import java.io.File;
import java.net.URI;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

public class EditCommandHandler extends AbstractHandler {
	private IStructuredSelection selection = StructuredSelection.EMPTY;
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        selection = (IStructuredSelection) HandlerUtil.getCurrentSelectionChecked(event);
    	
        Object object =  selection.getFirstElement();
        Job job = new Job("Edit..."){
        	protected IStatus run(IProgressMonitor monitor){
        		if(selection.size() == 1) {
        			
        			 Object firstElement = selection.getFirstElement();
        			 
        			 if(firstElement instanceof IFile  ) {
        				 IFile data = (IFile) firstElement;
        				 
        				 IFile propertiesFile = data;
        		
        				// gets URI for EFS.
        				URI uri = propertiesFile.getLocationURI();

        				// what if file is a link, resolve it.
        				if(propertiesFile.isLinked()){
        				   uri = propertiesFile.getRawLocationURI();
        				}

        				// Gets native File using EFS
        				File selectedFile = null;
        				try{
        				 selectedFile = EFS.getStore(uri).toLocalFile(0, new NullProgressMonitor());
        				}
        				catch (CoreException coreex){
        					coreex.printStackTrace();
        				}

        				S3 s3 = new S3 (User.awsAccessKey, User.awsSecretKey);
        				s3.uploadFile(selectedFile);
        				}
        			 else if (firstElement instanceof IFolder){
        					S3 s3 = new S3(User.awsAccessKey, User.awsSecretKey);
        					s3.uploadFolder((IFolder)firstElement);
        			 }

        		}
        		monitor.done();
        		return Status.OK_STATUS;
        	}
        		};
        job.setUser(true);
        job.schedule();

        return object;
    }
}