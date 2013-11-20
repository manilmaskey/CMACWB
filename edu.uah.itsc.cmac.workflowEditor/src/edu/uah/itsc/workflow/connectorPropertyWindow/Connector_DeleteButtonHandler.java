package edu.uah.itsc.workflow.connectorPropertyWindow;

import java.util.List;

import org.eclipse.swt.widgets.Shell;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class Connector_DeleteButtonHandler {

	/**
	 * Deletes the connector and closes the window from which the delete option
	 * was selected
	 * 
	 * @param method
	 * @param shell
	 */
	public void deleteFromConnectorWindow(ConnectorDetectable cd, Shell shell) {
		deleteConnector(cd);
		// close the shell
		shell.close();
		redraw_WorkFlow();
	}

	public void deleteFromConnector(ConnectorDetectable cd) {
		deleteConnector(cd);
		redraw_WorkFlow();
	}

	public void redraw_WorkFlow() {

		// variable pojo instance
		VariablePoJo instance = VariablePoJo.getInstance();

		// instance.getConnectorDetectableList().indexOf(arg0);

		// refresh the workspace
		RelayComposites relayObject = new RelayComposites();
		relayObject.setChildComposite_WorkSpace(instance
				.getChildCreatorObject().getChildComposite_WorkSpace());
		relayObject.setParentComposite(instance.getParentComposite());
		relayObject.setCompositeList(instance.getCompositeList());
		relayObject.setConnectorList(VariablePoJo.getInstance()
				.getConnectorList());

		relayObject.reDraw();
	}

	public void deleteConnector(ConnectorDetectable cd) {
		List<ConnectorDetectable> cdlist = VariablePoJo.getInstance()
				.getConnectorDetectableList();
		// This will always be true as the delete option is selected from the
		// connector.
		// Get the index of the cd in the cdlist. Then that particular cd's
		// visibility is set to false and then connector map is made empty in
		// both the starting and ending composite and finally the cd is deleted
		// from the list.
		if (cdlist.contains(cd)) {
			int i = cdlist.indexOf(cd);
			cdlist.get(i).setVisible(false);
			cdlist.get(i).getConnector().getStartingComposite()
					.getConnectionsMap().clear();
			cdlist.get(i).getConnector().getEndingComposite()
					.getConnectionsMap().clear();
			cdlist.remove(i);
			i = -1;
		}
		VariablePoJo.getInstance().setConnectorDetectableList(cdlist);
	}

}
