package edu.uah.itsc.workflow.wrapperClasses;

import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class LabelWrapper extends Label{

	public LabelWrapper(Composite parent, int style) {
		super(parent, style);
	}
	
	DropTarget dt;
	DragSource ds;
	
	// getters and setters for drag source and drag target
	public DropTarget getDt() {
		return dt;
	}
	public void setDt(DropTarget dt) {
		this.dt = dt;
	}
	public DragSource getDs() {
		return ds;
	}
	public void setDs(DragSource ds) {
		this.ds = ds;
	}

}
