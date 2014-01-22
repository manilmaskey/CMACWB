package edu.uah.itsc.cmac.actions;


import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.Page;

import edu.uah.itsc.aws.RubyClient;
import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.NavigatorView;


public class ScriptAction extends Action {
	protected static String ID = "Action.script";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getText()
	{
		return "Execute";
	}

	@Override
	public void run()
		{
        final Shell shell = new Shell();
        shell.setText("Workflow Settings");
        shell.setLayout(new GridLayout(3, false));

        Label title = new Label(shell, SWT.NONE);
        title.setText("Title : ");
        final Text titleText   = new Text(shell, SWT.BORDER);
        addSpanData(titleText);

        Label description = new Label(shell, SWT.NONE);
        description.setText("Description : ");
        final Text descText = new Text(shell, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
        descText.setLayoutData(new GridData(GridData.FILL_BOTH));
        org.eclipse.swt.widgets.Button ok = new org.eclipse.swt.widgets.Button(shell, SWT.PUSH);
        ok.setText("  OK  ");
        org.eclipse.swt.widgets.Button cancel = new org.eclipse.swt.widgets.Button(shell, SWT.PUSH);
        cancel.setText("Cancel");
        //Button ok = new org.eclipse.swt.widgets.Button(parent, style)
        ok.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        		//////////
       // 		 try {
	        		 IEditorPart myeditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
  	        		 if(myeditor.isDirty())// myeditor.doSave(monitor);
  	        		   myeditor.doSaveAs();
  	        		  
  	        		 IEditorInput input = myeditor.getEditorInput();
  	    		     
  	               String myfilelocation =  ((IFileEditorInput) input).getFile().getLocation().toString();
  	               
  	               IProject proj = ((IFileEditorInput) input).getFile().getProject();
  	               
  	               File temp = new File(myfilelocation);
  	               String folderName = temp.getParentFile().getName();
  			 
        	          //new  ProgressMonitorDialog(shell).run(true, true, new LongRunningOperation(true,titleText.getText(),descText.getText(),myfilelocation,folderName,proj.getName(),proj.getFolder(folderName)));
//        	        }  
//        		    catch (InvocationTargetException e) {
//        	          MessageDialog.openError(shell, "Error", e.getMessage());
//        	        } 
//        		 catch (InterruptedException e) {
//        	          MessageDialog.openInformation(shell, "Cancelled", e.getMessage());
//        	        }
        		 shell.close();
  	             ///////
  	             ///////
  	             
        	}
        });
       	cancel.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        	 		System.out.println("cancel button pressed");
        		shell.close();
        	}
       	});
        
        shell.pack();
        shell.open();
	    }

	    private void addSpanData(Control comp)
	    {
	        GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
	        data.horizontalSpan = 2;	        
	        comp.setLayoutData(data);   	 
	}
	
}

class LongRunningOperation implements IRunnableWithProgress {
	  private static final int TOTAL_TIME = 10000;

	  private static final int INCREMENT = 500;

	  private boolean indeterminate;
	  
	  private String file;
	  
	  private String bucket;
	  
	  private String folder;
	  
	  private String title;
	  
	  private String desc;

	  private IFolder folderResource;
	  
	  private IWorkbenchPage page;
	  
	  private String publicURL;
	  
	  public LongRunningOperation(boolean indeterminate, String title, String desc, String file, String folder, String bucket, IFolder folderResource, IWorkbenchPage page, String publicURL) {
	    this.indeterminate = indeterminate;
	    this.title = title;
	    this.desc = desc;
	    this.file =file;
	    this.folder = folder;
	    this.bucket = bucket;
	    this.folderResource = folderResource;
	    this.page = page;
	    this.publicURL = publicURL;
	  }

	  public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
	    monitor.beginTask("Executing workflow...", indeterminate ? IProgressMonitor.UNKNOWN
	        : TOTAL_TIME);
        new RubyClient(title,desc,bucket,folder,file,publicURL);
         
		try{
			
	//	NavigatorView view = (NavigatorView)  page.findView("edu.uah.itsc.cmac.NavigatorView");
		S3 s3 = new S3(User.awsAccessKey,User.awsSecretKey);
		//folderResource.delete(true, monitor);
		
		s3.downloadFolder(s3.getBucketName(folderResource.getFullPath().toOSString()), s3.getS3ResourceName(folderResource.getFullPath().toOSString()));
		folderResource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		}
		catch(Exception e){
			e.printStackTrace();
		}
        
   	    monitor.done();
	    if (monitor.isCanceled())
	      throw new InterruptedException("The long running operation was cancelled");
	  }
	} 