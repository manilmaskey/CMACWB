package edu.uah.itsc.workflow.connectorPropertyWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.json.JSONException;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class CreateWindowContents {

	/**
	 * Populate the window which opens on clicking the connector
	 * 
	 * @param leftComposite
	 * @param rightComposite
	 */
	String inputSelected;
	String outputSelected;
	String comboBox_oldText;
	Map<Label, String> IOConnections; // connections between Input and Output
	/**
	 * Map between labels and combo boxes
	 */
	Map<Label, Combo> labelMap = new HashMap<Label, Combo>();
	List<Label> outputLabels = new ArrayList<Label>();

	public void createContents(Composite leftComposite,
			Composite rightComposite, final ConnectorDetectable cd)
			throws Exception {
		
		
		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		final CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		
		

		// this will check if any output is already
		// linked to any input
		// checkMap(cd);
		IOConnections = cd.getConnector().getStartingComposite()
				.getIOConnections();
		populateInputArray(cd);
		// temporaryPouplateOutput(cd);

		int y = 41;

		// creating the starting composite output labels
		for (int i = 0; i < cd.getConnector().getStartingComposite()
				.getNumberOfOutputs(); i++) {
			/**
			 * connectionsMap has a record of any outputs that are hooked to an
			 * input. Key is the output value name and the value is the input
			 * value name
			 */
			if (cd.getConnector()
					.getStartingComposite()
					.getConnectionsMap()
					.get(cd.getConnector().getStartingComposite()
							.getProgram_outputs().get(i)) == null) {

				final Label lblNewLabel = new Label(leftComposite, SWT.NONE);
				lblNewLabel.setBounds(10, y, 244, 21);
				System.out.println(cd
						.getConnector()
						.getStartingComposite()
						.getConnectionsMap()
						.get(cd.getConnector().getStartingComposite()
								.getProgram_outputs().get(i).getTitle()));
				System.out.println("outputlabel title is "
						+ cd.getConnector().getStartingComposite()
								.getProgram_outputs().get(i).getTitle());
				lblNewLabel.setText(cd.getConnector().getStartingComposite()
						.getProgram_outputs().get(i).getTitle());

				outputLabels.add(lblNewLabel);

				// add respective combo box for the output
				final Combo comboBox = new Combo(rightComposite, SWT.BORDER);
				comboBox.setBounds(10, y, 186, 21);
				labelMap.put(lblNewLabel, comboBox);

				y = y + 27;

				// if the label is not linked
				if (!(IOConnections.containsKey(lblNewLabel))) {
					IOConnections.put(lblNewLabel, null);
					dataobj.getIOConnections()
							.put(lblNewLabel, comboBox.getText());

				}

				comboBox_oldText = comboBox.getText();
				GetOutputValues govObject = new GetOutputValues();
				govObject.getProgramsOutputValues(cd);

				// selection listener to the combo box
				comboBox.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						System.out.println(lblNewLabel.getText()
								+ " is linked to " + comboBox.getText());

						// put the value in the hash map
						cd.getConnector().getStartingComposite()
								.getConnectionsMap()
								.put(lblNewLabel.getText(), comboBox.getText());
						// test where we will put connection map in both
						// starting and ending composite
						cd.getConnector()
								.getEndingComposite()
								.setConnectionsMap(
										cd.getConnector()
												.getStartingComposite()
												.getConnectionsMap());

						FillInputValues fivObj = new FillInputValues();
						ClearComboBoxes ccbObj = new ClearComboBoxes();
						PopulateComboBoxes pcbObj = new PopulateComboBoxes();

						try {
							fivObj.fillInputValues(cd, outputLabels);
							ccbObj.clearComboBoxes(cd, outputLabels, labelMap);
							pcbObj.populateComboBoxes(cd, outputLabels,
									labelMap);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					}
				});

			} else {
				final Label lblNewLabel = new Label(leftComposite, SWT.NONE);
				lblNewLabel.setBounds(10, y, 244, 21);
				lblNewLabel.setText(cd.getConnector().getStartingComposite()
						.getProgram_outputs().get(i).getTitle());
				outputLabels.add(lblNewLabel);
				// add respective combo box for the output
				final Combo comboBox = new Combo(rightComposite, SWT.BORDER);
				comboBox.setBounds(10, y, 186, 21);
				y = y + 27;
				// getting the text of the combo box from the ioconnections map
				// comboBox_oldText = IOConnections.get(lblNewLabel);
				// comboBox.setText(comboBox_oldText);
				labelMap.put(lblNewLabel, comboBox);
				// if the label is not linked
				if (!(IOConnections.containsKey(lblNewLabel))) {
					IOConnections.put(lblNewLabel, null);
					dataobj.getIOConnections()
							.put(lblNewLabel, comboBox.getText());
				}

			}
		}

		FillInputValues fivObj = new FillInputValues();
		ClearComboBoxes ccbObj = new ClearComboBoxes();
		PopulateComboBoxes pcbObj = new PopulateComboBoxes();

		fivObj.fillInputValues(cd, outputLabels);

		ccbObj.clearComboBoxes(cd, outputLabels, labelMap);
		pcbObj.populateComboBoxes(cd, outputLabels, labelMap);
	}

	/**
	 * If no input value is entered. This method will fill the input values list
	 * with null values
	 * 
	 * @param cd
	 *            - connector detectable
	 */
	public void populateInputArray(ConnectorDetectable cd) {
		if (cd.getConnector().getEndingComposite().getInputValues().size() == 0) {
			for (int i = 0; i < cd.getConnector().getEndingComposite()
					.getNumberOfInputs(); i++) {
				cd.getConnector().getEndingComposite().getInputValues()
						.add(null);
			}
		}
	}

	public void setIOConnectionsList(ConnectorDetectable cd) {
	}

	public Map<Label, String> getIOConnections() {
		return IOConnections;
	}

	public void setIOConnections(Map<Label, String> iOConnections) {
		IOConnections = iOConnections;
	}

	public Map<Label, Combo> getLabelMap() {
		return labelMap;
	}

	public void setLabelMap(Map<Label, Combo> labelMap) {
		this.labelMap = labelMap;
	}

	public List<Label> getOutputLabels() {
		return outputLabels;
	}

	public void setOutputLabels(List<Label> outputLabels) {
		this.outputLabels = outputLabels;
	}

}
