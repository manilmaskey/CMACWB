package edu.uah.itsc.workflow.actionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.workflow.connectorPropertyWindow.ConnectorPropertyWindow;
import edu.uah.itsc.workflow.connectorPropertyWindow.Connector_DeleteButtonHandler;
import edu.uah.itsc.workflow.connectors.ConnectorDetectable;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class ConnectorClickHandler {

	/**
	 * This method will add handlers to handler the click on the connector.
	 */
	public void addConnectorHandlers(final ConnectorDetectable cd) {

		cd.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					Connector_DeleteButtonHandler cdh = new Connector_DeleteButtonHandler();
					cdh.deleteFromConnector(cd);
				}
			}
		});

		cd.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try{
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSaveAs();
					}
					catch (Exception e){
						System.out.println("No active page ...");
					}
				cd.forceFocus();
			}
		});

		/**
		 * at the accept of drop a new connector detectable is created and the
		 * this method is called to add handler this method will take the
		 * detectable to which it is adding handler as parameter
		 */
		cd.addListener(SWT.MouseDoubleClick, new Listener() {

			@Override
			public void handleEvent(Event event) {
				System.out.println("Click Detected");
				ConnectorPropertyWindow object = new ConnectorPropertyWindow();
				// try {
				try {
					object.openConnectorPropertyWindow(cd, event.display.getCursorLocation());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		cd.addListener(SWT.MouseHover, new Listener() {

			@Override
			public void handleEvent(Event event) {
				cd.setToolTipText(cd.getConnector().getStartingComposite()
						.getMethodName()
						+ "-"
						+ cd.getConnector().getEndingComposite()
								.getMethodName());
			}
		});
	}
	// }

}
