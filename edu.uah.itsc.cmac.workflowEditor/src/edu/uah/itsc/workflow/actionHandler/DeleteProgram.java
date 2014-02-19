package edu.uah.itsc.workflow.actionHandler;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class DeleteProgram {
	
	String filename = "";
	

	public DeleteProgram() {
		super();
	}

	public DeleteProgram(String filename) {
		super();
		this.filename = filename;
	}




	public void delete_selected_program(CompositeWrapper method) {
		
		final CopyOfVariablePoJo dataobj;
		
		if (filename.equals("")){
		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		}else {
			dataobj = (POJOHolder.getInstance().getEditorsmap().get(filename));
		}
		
		
		
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

			
			try{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSaveAs();
				}
				catch (Exception e){
					System.out.println("No active page ... delete program");
				}
			
			System.out.println("connector detectable list "
					+ dataobj.getConnectorDetectableList()
							.size());

			List<ConnectorDetectable> cdlist = dataobj.getConnectorDetectableList();
			List<Connectors> connectorslist = dataobj.getConnectorList();

			for (int i = 0; i < cdlist.size(); i++) {
				System.out.println("cd is "
						+ i
						+ "starting composite is "
						+ cdlist.get(i).getConnector().getStartingComposite()
								.getMethodName());
				System.out.println("cd is " + i + "method name is "
						+ method.getMethodName());

				if (cdlist.get(i).getConnector().getStartingComposite()
						.getCompositeID().equals(method.getCompositeID())) {
					cdlist.get(i).setVisible(false);
					cdlist.get(i).getConnector().getStartingComposite()
							.getConnectionsMap().clear();
					cdlist.get(i).getConnector().getEndingComposite()
							.getConnectionsMap().clear();
					cdlist.remove(i);
//					int index = connectorslist.indexOf(cdlist.get(i).getConnector());
//					connectorslist.remove(index);
					i = -1;
				} else if (cdlist.get(i).getConnector().getEndingComposite()
						.getCompositeID().equals(method.getCompositeID())) {
					cdlist.get(i).setVisible(false);
					cdlist.get(i).getConnector().getStartingComposite()
							.getConnectionsMap().clear();
					cdlist.get(i).getConnector().getEndingComposite()
							.getConnectionsMap().clear();
					cdlist.remove(i);
//					connectorslist.indexOf(cdlist.get(i).getConnector());
//					connectorslist.remove(connectorslist.indexOf(cdlist.get(i).getConnector()));
					i = -1;
				}
				dataobj.setConnectorDetectableList(cdlist);
//				dataobj.setConnectorList(connectorslist);
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

		}
		
		RelayComposites rc = new RelayComposites();
		rc.reDraw();
	}
}
