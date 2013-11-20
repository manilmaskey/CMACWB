package edu.uah.itsc.workflow.connectorPropertyWindow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class ConnectorPropertyWindow {

	/**
	 * This method will open connector property window
	 * 
	 * @param cd
	 *            is a composite which holds the starting and ending composite
	 *            data
	 * @throws Exception
	 */
	public void openConnectorPropertyWindow(final ConnectorDetectable cd)
			throws Exception {

		final Shell shell = new Shell();

		ConnectorPropertyShellHeight calculatorObject = new ConnectorPropertyShellHeight();
		int shellHeight = calculatorObject.calculate_shellHeight(cd);

		// checking the shell height
		System.out.println("shell height " + shellHeight);

		shell.setSize(500, shellHeight);
		shell.setText(cd.getConnector().getStartingComposite().getMethodName()
				+ "-" + cd.getConnector().getEndingComposite().getMethodName());

		Composite shellComposite = new Composite(shell, SWT.NONE);
		shellComposite.setBounds(0, 0, 500, shellHeight);

		// Left half of the shell for Starting composite
		Composite leftComposite = new Composite(shellComposite, SWT.BORDER
				| SWT.NO_FOCUS);
		// -34 -36
		leftComposite.setBounds(0, 0, 248, shellHeight - 70);
		Label startingLabel = new Label(leftComposite, SWT.BORDER | SWT.CENTER);
		startingLabel.setBounds(0, 0, 248, 19);
		startingLabel.setText(cd.getConnector().getStartingComposite()
				.getMethodName()
				+ "'s outputs");
		Button btnDELETE = new Button(shellComposite, SWT.PUSH | SWT.BORDER);
		btnDELETE.setText("Delete");
		btnDELETE.setBounds(164, shellHeight - 64, 68, 23);

		// Right half of the shell for ending composite
		Composite rightComposite = new Composite(shellComposite, SWT.BORDER
				| SWT.NO_FOCUS);
		rightComposite.setBounds(254, 0, 238, shellHeight - 70);
		Label endingLabel = new Label(rightComposite, SWT.BORDER | SWT.CENTER);
		endingLabel.setBounds(0, 0, 238, 20);
		endingLabel.setText(cd.getConnector().getEndingComposite()
				.getMethodName()
				+ "'s inputs");
		// populate the hash map with keys and null for value
		// this hash map will tell if an output value is already linked with an
		// input
		// only if the connector map is already not filled
		if (cd.getConnector().getStartingComposite().getConnectionsMap().size() == 0) {
			for (int i = 0; i < cd.getConnector().getStartingComposite()
					.getNumberOfOutputs(); i++) {

				cd.getConnector()
						.getStartingComposite()
						.getConnectionsMap()
						.put(cd.getConnector().getStartingComposite()
								.getProgram_outputs().get(i).getTitle(), null);
				cd.getConnector()
						.getEndingComposite()
						.setConnectionsMap(
								cd.getConnector().getStartingComposite()
										.getConnectionsMap());
			}
		}

		CreateWindowContents creatorObject = new CreateWindowContents();
		creatorObject.createContents(leftComposite, rightComposite, cd);

		shell.open();
		
		btnDELETE.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Connector_DeleteButtonHandler handlerObject = new Connector_DeleteButtonHandler();
				handlerObject.deleteFromConnectorWindow(cd, shell);
			}
		});
	}

}
