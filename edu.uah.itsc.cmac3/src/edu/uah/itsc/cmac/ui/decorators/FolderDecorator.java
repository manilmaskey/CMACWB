package edu.uah.itsc.cmac.ui.decorators;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

public class FolderDecorator extends LabelProvider implements ILabelDecorator {

    public FolderDecorator() {
        super();
    }

    @Override
    public Image decorateImage(Image image, Object element) {
//return the image
    	IFolder folder = (IFolder)element;
    	S3 s3 = new S3();
    	if (folder.getProject().getName().equals(s3.getCommunityBucketName()))
    		return image;
    	else{
    		IProject communityProject = ResourcesPlugin.getWorkspace().getRoot().getProject(s3.getCommunityBucketName());
    		
    		if (folder.getName().equals(User.username)){
    			return AbstractUIPlugin.imageDescriptorFromPlugin("edu.uah.itsc.cmac3","icons/user-folder-16x16.png").createImage();
    		}
    		else{
    			if (communityProject.getFolder(User.username).exists()){
	    			IFolder communityUserFolder = communityProject.getFolder(User.username);
	    			if (communityUserFolder.getFolder(folder.getName()).exists())
	    			  return AbstractUIPlugin.imageDescriptorFromPlugin("edu.uah.itsc.cmac3","icons/shared-16x16.png").createImage();
	    			else
	    				return image;
	    		}
	
	    		else	
	    			return image;
    		}
    	}
    	
        
    }
    
    // Method to decorate Text 
    public String decorateText(String label, Object object) 
    { 
      // return null to specify no decoration 
    	IFolder folder = (IFolder)object;
    	S3 s3 = new S3();
    	if (folder.getProject().getName().equals(s3.getCommunityBucketName()))
    		return label;
    	else{
    		IProject communityProject = ResourcesPlugin.getWorkspace().getRoot().getProject(s3.getCommunityBucketName());
    		
    		if (folder.getName().equals(User.username)){
    			return label;
    		}
    		else{
    			if (communityProject.getFolder(User.username).exists()){
	    			IFolder communityUserFolder = communityProject.getFolder(User.username);
	    			if (communityUserFolder.getFolder(folder.getName()).exists())
	    			  return label + " - Shared";
	    			else
	    				return label;
	    		}
	
	    		else	
	    			return label;
    		}
    	}
    } 
}