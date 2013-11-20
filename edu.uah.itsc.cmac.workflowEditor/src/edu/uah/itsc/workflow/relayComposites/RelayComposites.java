package edu.uah.itsc.workflow.relayComposites;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible for redrawing everything after any action
 * 
 * @author Rohith Samudrala
 * 
 */
public class RelayComposites {

	// Global Variables
	CompositeWrapper parentComposite = VariablePoJo.getInstance()
			.getParentComposite();
	CompositeWrapper childComposite_WorkSpace = VariablePoJo.getInstance()
			.getChildCreatorObject().getChildComposite_WorkSpace();
	List<CompositeWrapper> compositeList = VariablePoJo.getInstance()
			.getCompositeList();
	List<Connectors> connectorList = VariablePoJo.getInstance()
			.getConnectorList();

	// Getters and Setters
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

	public List<CompositeWrapper> getCompositeList() {
		return compositeList;
	}

	public void setCompositeList(List<CompositeWrapper> compositeList) {
		this.compositeList = compositeList;
	}

	public List<Connectors> getConnectorList() {
		return connectorList;
	}

	public void setConnectorList(List<Connectors> connectorList) {
		this.connectorList = connectorList;
	}

	/**
	 * This method collects data from composite and connector lists, clears the
	 * entire composite and redraws everything
	 */
	public void reDraw() {

		if (VariablePoJo.getInstance().getSelected_composite() != null) {
			if (!(VariablePoJo.getInstance().getSelected_composite()
					.isDisposed())) {
				CompositeWrapper composite = VariablePoJo.getInstance()
						.getSelected_composite();
				composite.setBackground(VariablePoJo.getInstance()
						.getChildCreatorObject().getChildComposite_WorkSpace()
						.getDisplay()
						.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			}
		}
//		for (int i = 0; i < VariablePoJo.getInstance().getCompositeList().size(); i++){
//			CompositeWrapper composite = VariablePoJo.getInstance().getCompositeList().get(i);
//			composite.setBackground(VariablePoJo.getInstance()
//					.getChildCreatorObject().getChildComposite_WorkSpace()
//					.getDisplay()
//					.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
//		}

		GC gc = new GC(childComposite_WorkSpace);
		gc.fillRectangle(childComposite_WorkSpace.getClientArea());

		gc.setForeground(childComposite_WorkSpace.getDisplay().getSystemColor(
				SWT.COLOR_DARK_GRAY));
		gc.setLineWidth(3);

		List<ConnectorDetectable> connectorDetectableList = VariablePoJo
				.getInstance().getConnectorDetectableList();
		for (int i = 0; i < connectorDetectableList.size(); i++) {
			ConnectorDetectable cd = connectorDetectableList.get(i);
			Connectors connector = cd.getConnector();

			int dest_x = (connector.getDestination().getParent().getLocation().x)
					+ (connector.getDestination().getLocation().x);

			int dest_y = (connector.getDestination().getParent().getLocation().y)
					+ (connector.getDestination().getLocation().y)
					+ ((connector.getDestination().getBounds().height) / 2);

			int source_x = (connector.getSource().getParent().getLocation().x)
					+ (connector.getSource().getLocation().x)
					+ (connector.getSource().getBounds().width);

			int source_y = (connector.getSource().getParent().getLocation().y)
					+ (connector.getSource().getLocation().y)
					+ ((connector.getSource().getBounds().height) / 2);

			// gc.drawLine(source_x, source_y, dest_x, dest_y);

			Path p = new Path(childComposite_WorkSpace.getDisplay());
			p.moveTo(source_x, source_y);
			p.cubicTo(source_x + 120, source_y - 120, dest_x - 120,
					dest_y + 120, dest_x, dest_y);
			gc.fillPath(p);
			gc.drawPath(p);

			int midx = (source_x + dest_x) / 2;
			int midy = (source_y + dest_y) / 2;

			// Add detectable on the line
			cd.setBounds(midx - 3, midy - 2, 6, 6);
			cd.setBackground(childComposite_WorkSpace.getDisplay()
					.getSystemColor(SWT.COLOR_DARK_GREEN));
		}

	}
}
