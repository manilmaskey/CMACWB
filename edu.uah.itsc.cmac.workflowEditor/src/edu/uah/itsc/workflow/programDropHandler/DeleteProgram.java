package edu.uah.itsc.workflow.programDropHandler;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class DeleteProgram {

	public void delete_selected_program(CompositeWrapper method) {
		// There are connectors attached, get user confirmation to delete
		MessageBox dialog = new MessageBox(method.getShell(), SWT.ICON_QUESTION
				| SWT.OK | SWT.CANCEL);
		dialog.setText("Confirmation");
		dialog.setMessage("If there are any connectors attached to the composite"
				+ " they will also be deleted. Are you sure you want to continue ?");
		int returnCode = dialog.open();

		if (returnCode != SWT.OK) {
			// the user decides to canel do not remove the program
		} else {

			System.out.println("connector detectable list "
					+ VariablePoJo.getInstance().getConnectorDetectableList()
							.size());

			List<ConnectorDetectable> cdlist = VariablePoJo.getInstance()
					.getConnectorDetectableList();

			for (int i = 0; i < cdlist.size(); i++) {
				System.out.println("cd is "
						+ i
						+ "starting composite is "
						+ cdlist.get(i).getConnector().getStartingComposite()
								.getMethodName());
				System.out.println("cd is " + i + "method name is "
						+ method.getMethodName());

				if (cdlist.get(i).getConnector().getStartingComposite()
						.getMethodName().equals(method.getMethodName())) {
					cdlist.get(i).setVisible(false);
					cdlist.get(i).getConnector().getStartingComposite()
							.getConnectionsMap().clear();
					cdlist.get(i).getConnector().getEndingComposite()
							.getConnectionsMap().clear();
					cdlist.remove(i);
					i = -1;
				} else if (cdlist.get(i).getConnector().getEndingComposite()
						.getMethodName().equals(method.getMethodName())) {
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
			/**
			 * Now when all the connections are removed .. remove the composite
			 */
			for (int j = 0; j < VariablePoJo.getInstance().getCompositeList()
					.size(); j++) {
				if (method.getCompositeID().equals(
						VariablePoJo.getInstance().getCompositeList().get(j)
								.getCompositeID())) {
					VariablePoJo.getInstance().getCompositeList().get(j)
							.setVisible(false);
					VariablePoJo.getInstance().getCompositeList().remove(j);
				}
			}

		}
		
		RelayComposites rc = new RelayComposites();
		rc.reDraw();
	}
}
