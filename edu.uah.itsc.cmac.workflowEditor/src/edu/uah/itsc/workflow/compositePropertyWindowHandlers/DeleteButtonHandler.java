package edu.uah.itsc.workflow.compositePropertyWindowHandlers;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 *
 */
public class DeleteButtonHandler {

	public void closeShell(Shell shell) {

		// variable pojo instance
		VariablePoJo instance = VariablePoJo.getInstance();

		// close the shell
		shell.close();

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

	public void deletetest(CompositeWrapper method, Shell shell) {

		// There are connectors attached, get user confirmation to delete
		MessageBox dialog = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK
				| SWT.CANCEL);
		dialog.setText("Confirmation");
		dialog.setMessage("If there are any connectors attached to the composite"
				+ " they will also be deleted. Are you sure you want to continue ?");
		int returnCode = dialog.open();

		if (returnCode != SWT.OK) {
			closeShell(shell);
		} else {

			System.out.println("connector detectable list "
					+ VariablePoJo.getInstance().getConnectorDetectableList()
							.size());

			List<ConnectorDetectable> cdlist = VariablePoJo.getInstance()
					.getConnectorDetectableList();
			
			for (int i = 0; i < cdlist.size(); i++) {

				if (cdlist.get(i).getConnector().getStartingComposite()
						.getCompositeID().equals(method.getCompositeID())) {
					cdlist.get(i).setVisible(false);
					cdlist.get(i).getConnector().getStartingComposite()
							.getConnectionsMap().clear();
					cdlist.get(i).getConnector().getEndingComposite()
							.getConnectionsMap().clear();
					cdlist.remove(i);
					i = -1;
				} else if (cdlist.get(i).getConnector().getEndingComposite()
						.getCompositeID().equals(method.getCompositeID())) {
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

			/**
			 * Now that all the deleting is done close the shell and refresh the
			 * workspace
			 */
			closeShell(shell);

		}
	}

}
