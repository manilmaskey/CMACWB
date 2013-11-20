package edu.uah.itsc.workflow.childComposites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible to create the child composite There are two child
 * composites 1) child composite for cool bar = childComposite_coolBar 2) child
 * composite for workspace = childComposite_WorkSpace
 * 
 * @author Rohith Samudrala
 * 
 */
public class ChildCompositeCreator {

	// Global Variables
	CompositeWrapper parentComposite;
	CompositeWrapper childComposite_WorkSpace;

	// Getters and setters for childComposite_WorkSpace
	public CompositeWrapper getChildComposite_WorkSpace() {
		return childComposite_WorkSpace;
	}

	public void setChildComposite_WorkSpace(
			CompositeWrapper childComposite_WorkSpace) {
		this.childComposite_WorkSpace = childComposite_WorkSpace;
	}

	// Getters and setters for parentComposite
	public CompositeWrapper getParentComposite() {
		return parentComposite;
	}

	public void setParentComposite(CompositeWrapper parentComposite) {
		this.parentComposite = parentComposite;
	}

	/**
	 * Creates Child Composite using the parent composites
	 */
	public void createChildComposites() {
		childComposite_WorkSpace = new CompositeWrapper(parentComposite,
				SWT.NONE);
		childComposite_WorkSpace.setBounds(0, 0, 768, 1300);
		GridData gd_childComposite_WorkSpace = new GridData(SWT.NONE, SWT.NONE,
				true, true, 1, 1);
		System.out.println("parent height"
				+ parentComposite.getDisplay().getBounds().height);

		// Preferred height and width are the preferred height and width of the
		// parent composite
		gd_childComposite_WorkSpace.heightHint = 768;
		gd_childComposite_WorkSpace.widthHint = 1366;
		childComposite_WorkSpace.setLayoutData(gd_childComposite_WorkSpace);

	}

}
