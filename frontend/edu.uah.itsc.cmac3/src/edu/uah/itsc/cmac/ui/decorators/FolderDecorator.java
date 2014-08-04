package edu.uah.itsc.cmac.ui.decorators;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.ui.SharedWorkflowView;

public class FolderDecorator extends LabelProvider implements ILabelDecorator {

	public FolderDecorator() {
		super();
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		// return the image
		IFolder folder = (IFolder) element;
		String folderName = folder.getName();
		boolean isShared = SharedWorkflowView.isSharedWorkflow(folder.getProject().getName(), folderName);
		if (isShared)
			return AbstractUIPlugin.imageDescriptorFromPlugin("edu.uah.itsc.cmac3", "icons/shared-16x16.png")
				.createImage();
		else
			return image;
	}

	// Method to decorate Text
	public String decorateText(String label, Object element) {
		IFolder folder = (IFolder) element;
		String folderName = folder.getName();
		boolean isShared = SharedWorkflowView.isSharedWorkflow(folder.getProject().getName(), folderName);
		if (isShared)
			return label + " - Shared";
		else if (!S3.getWorkflowOwner(folder.getLocation().toString()).equalsIgnoreCase(User.username))
			return label + " - Imported";
		else
			return label;
	}

}