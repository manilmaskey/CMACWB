package edu.uah.itsc.workflow.relayComposites;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Path;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
//import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible for redrawing everything after any action
 * 
 * @author Rohith Samudrala
 * 
 */
public class RelayComposites {
	
	String filename = "";
	
	
	
	public RelayComposites() {
		super();
	}

	public RelayComposites(String filename) {
		super();
		this.filename = filename;
	}

	// Global Variables
	CompositeWrapper parentComposite;
	CompositeWrapper childComposite_WorkSpace;
	List<CompositeWrapper> compositeList;
	List<Connectors> connectorList;

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
		
		CopyOfVariablePoJo dataobj = setdata ();

		IWorkbenchPage[] pagelist = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getPages();
		IWorkbenchPage page = pagelist[0];

		IEditorReference[] referencelist = page.getEditorReferences();
		IEditorPart editorPart = referencelist[0].getEditor(false);

		if (dataobj.getSelected_composite() != null) {
			if (!(dataobj.getSelected_composite()
					.isDisposed())) {
				CompositeWrapper composite = dataobj
						.getSelected_composite();
				composite.setBackground(dataobj
						.getChildCreatorObject().getChildComposite_WorkSpace()
						.getDisplay()
						.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			}
		}
		// for (int i = 0; i <
		// VariablePoJo.getInstance().getCompositeList().size(); i++){
		// CompositeWrapper composite =
		// VariablePoJo.getInstance().getCompositeList().get(i);
		// composite.setBackground(VariablePoJo.getInstance()
		// .getChildCreatorObject().getChildComposite_WorkSpace()
		// .getDisplay()
		// .getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		// }

		GC gc = new GC(childComposite_WorkSpace);
		gc.fillRectangle(childComposite_WorkSpace.getClientArea());

		gc.setForeground(childComposite_WorkSpace.getDisplay().getSystemColor(
				SWT.COLOR_DARK_GRAY));
		gc.setLineWidth(3);

		List<ConnectorDetectable> connectorDetectableList = dataobj.getConnectorDetectableList();
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

	private CopyOfVariablePoJo setdata() {
		
		final CopyOfVariablePoJo dataobj;
		
		if (filename.equals("")){
		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		}else {
			dataobj = (POJOHolder.getInstance().getEditorsmap().get(filename));
		}
		
		parentComposite = dataobj
				.getParentComposite();
		childComposite_WorkSpace = dataobj
				.getChildCreatorObject().getChildComposite_WorkSpace();
		compositeList = dataobj
				.getCompositeList();
		connectorList = dataobj
				.getConnectorList();
		
		return dataobj;
	}
}
