package edu.uah.itsc.workflow.movementTrackers;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tracker;

import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class tracks the movement of method composite
 * 
 * @author Rohith Samudrala
 * 
 */
public class MethodCompositeTracker {

	CompositeWrapper methodComposite;
	CompositeWrapper parentComposite;
	CompositeWrapper childComposite_WorkSpace;
	List<Connectors> connectorList;
	List<CompositeWrapper> compositeList;

	// getters and setters
	public CompositeWrapper getParentComposite() {
		return parentComposite;
	}

	public void setParentComposite(CompositeWrapper parentComposite) {
		this.parentComposite = parentComposite;
	}

	public CompositeWrapper getChildComposite_WorkSpace() {
		return childComposite_WorkSpace;
	}

	public void setChildComposite_WorkSpace(
			CompositeWrapper childComposite_WorkSpace) {
		this.childComposite_WorkSpace = childComposite_WorkSpace;
	}

	public List<Connectors> getConnectorList() {
		return connectorList;
	}

	public void setConnectorList(List<Connectors> connectorList) {
		this.connectorList = connectorList;
	}

	// getters and setters for method composite
	public CompositeWrapper getMethodComposite() {
		return methodComposite;
	}

	public void setMethodComposite(CompositeWrapper methodComposite) {
		this.methodComposite = methodComposite;
	}

	// getters and setters for composite list
	public List<CompositeWrapper> getCompositeList() {
		return compositeList;
	}

	public void setCompositeList(List<CompositeWrapper> compositeList) {
		this.compositeList = compositeList;
	}

	public void methodTracker() {
		// add tracker to track the mouse movement
		Tracker tracker = new Tracker(methodComposite.getParent(), SWT.NONE);
		tracker.setStippled(true);
		Rectangle rect = methodComposite.getBounds();
		tracker.setRectangles(new Rectangle[] { rect });
		if (tracker.open()) {
			Rectangle after = tracker.getRectangles()[0];
			// checking through the composite array list to
			// reset the bounds
			for (int i = 0; i < compositeList.size(); i++) {
				CompositeWrapper tempCompositeObj = compositeList.get(i);
				if (tempCompositeObj.getLocation().x == rect.x
						&& tempCompositeObj.getLocation().y == rect.y) {
					tempCompositeObj.setBounds(after);
				}
			}
			// redraw everything
			RelayComposites relayCompositesObject = new RelayComposites();
			relayCompositesObject
					.setChildComposite_WorkSpace(childComposite_WorkSpace);
			relayCompositesObject.setCompositeList(compositeList);
			relayCompositesObject.setConnectorList(connectorList);
			relayCompositesObject.setParentComposite(parentComposite);
			relayCompositesObject.reDraw();

		}
		tracker.dispose();
	}
}
