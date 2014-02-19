package edu.uah.itsc.workflow.childComposites;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
	ScrolledComposite sc;
	CompositeWrapper parentComposite;
	CompositeWrapper childComposite_WorkSpace;

	// Getters and setters for childComposite_WorkSpace
	public ScrolledComposite getSc() {
		return sc;
	}

	public void setSc(ScrolledComposite sc) {
		this.sc = sc;
	}
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
//		childComposite_WorkSpace = new CompositeWrapper(parentComposite,
//				SWT.NONE);
//
////		childComposite_WorkSpace.setBounds(0, 0, parentComposite.getDisplay()
////				.getBounds().width,
////				parentComposite.getDisplay().getBounds().height);
//		GridData gd_childComposite_WorkSpace = new GridData(SWT.NONE, SWT.NONE,
//				true, true, 1, 1);
//		System.out.println("parent height"
//				+ parentComposite.getDisplay().getBounds().height);
//		System.out.println("parent width"
//				+ parentComposite.getDisplay().getBounds().width);
//
//		System.out.println("ccws height "
//				+ childComposite_WorkSpace.getBounds().height);
//		System.out.println("ccws width "
//				+ childComposite_WorkSpace.getBounds().width);
//
//		// Preferred height and width are the preferred height and width of the
//		// parent composite
//		gd_childComposite_WorkSpace.heightHint = parentComposite.getDisplay()
//				.getBounds().height;
//		gd_childComposite_WorkSpace.widthHint = parentComposite.getDisplay()
//				.getBounds().width;
//		
//		childComposite_WorkSpace.setLayoutData(gd_childComposite_WorkSpace);

		
		
		childComposite_WorkSpace = new CompositeWrapper(sc,
				SWT.NONE);
		childComposite_WorkSpace.setBackground(childComposite_WorkSpace.getDisplay().getSystemColor(SWT.COLOR_WHITE));

//		childComposite_WorkSpace.setBounds(0, 0, parentComposite.getDisplay()
//				.getBounds().width,
//				parentComposite.getDisplay().getBounds().height);
		GridData gd_childComposite_WorkSpace = new GridData(SWT.NONE, SWT.NONE,
				true, true, 1, 1);
		System.out.println("parent height"
				+ parentComposite.getDisplay().getBounds().height);
		System.out.println("parent width"
				+ parentComposite.getDisplay().getBounds().width);

		System.out.println("ccws height "
				+ childComposite_WorkSpace.getBounds().height);
		System.out.println("ccws width "
				+ childComposite_WorkSpace.getBounds().width);

		gd_childComposite_WorkSpace.heightHint = parentComposite.getDisplay()
				.getBounds().height;
		gd_childComposite_WorkSpace.widthHint = parentComposite.getDisplay()
				.getBounds().width;
		childComposite_WorkSpace.setLayoutData(gd_childComposite_WorkSpace);
	}

}
