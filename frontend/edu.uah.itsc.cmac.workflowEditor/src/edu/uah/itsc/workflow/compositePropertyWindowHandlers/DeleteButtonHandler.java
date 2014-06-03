package edu.uah.itsc.workflow.compositePropertyWindowHandlers;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 *
 */
public class DeleteButtonHandler {

	public void closeShell(Shell shell) {
		
		
		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		final CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		

		// variable pojo instance
//		VariablePoJo instance = VariablePoJo.getInstance();

		// close the shell
		shell.close();

		// instance.getConnectorDetectableList().indexOf(arg0);

		// refresh the workspace
		RelayComposites relayObject = new RelayComposites();
		relayObject.setChildComposite_WorkSpace(dataobj
				.getChildCreatorObject().getChildComposite_WorkSpace());
		relayObject.setParentComposite(dataobj.getParentComposite());
		relayObject.setCompositeList(dataobj.getCompositeList());
		relayObject.setConnectorList(dataobj.getConnectorList());

		relayObject.reDraw();
	}

	public void deletetest(CompositeWrapper method, Shell shell) {
		
		try{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSaveAs();
			}
			catch (Exception e){
				System.out.println("No active page ... delete button handler");
			}
		
		
		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		final CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		

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
					+ dataobj.getConnectorDetectableList()
							.size());

			List<ConnectorDetectable> cdlist = dataobj.getConnectorDetectableList();
			
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
//				dataobj.setConnectorDetectableList(cdlist);
			}
			/**
			 * Now when all the connections are removed .. remove the composite
			 */
			for (int j = 0; j < dataobj.getCompositeList()
					.size(); j++) {
				if (method.getCompositeID().equals(
						dataobj.getCompositeList().get(j)
								.getCompositeID())) {
					dataobj.getCompositeList().get(j)
							.setVisible(false);
					dataobj.getCompositeList().remove(j);
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
