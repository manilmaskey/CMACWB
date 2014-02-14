package edu.uah.itsc.cmac.ui.decorators;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import edu.uah.itsc.aws.S3;

public class ProjectDecorator extends LabelProvider implements ILabelDecorator {

    public ProjectDecorator() {
        super();
    }

    @Override
    public Image decorateImage(Image image, Object element) {
//return the image
    	IProject project = (IProject)element;
    	String icon;
    	S3 s3 = new S3();
    	if (project.getName().equals(s3.getCommunityBucketName()))
    		icon = "icons/cloud-16x16.png";
    	else
    		icon = "icons/lock-16x16.png";	
        return AbstractUIPlugin.imageDescriptorFromPlugin("edu.uah.itsc.cmac3",icon).createImage();
    }
    
    // Method to decorate Text 
    public String decorateText(String label, Object object) 
    { 
      // return null to specify no decoration 
    	IProject project = (IProject)object;
    	String text;
    	S3 s3 = new S3();
    	if (project.getName().equals(s3.getCommunityBucketName()))
    		text = " - Publicly Shared";
    	else
    		text = " - Personal Sandbox";	   	
      return label+ text; 
    } 
}