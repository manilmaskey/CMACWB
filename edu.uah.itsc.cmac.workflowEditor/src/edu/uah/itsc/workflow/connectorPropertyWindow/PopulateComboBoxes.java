package edu.uah.itsc.workflow.connectorPropertyWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.json.JSONException;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;

//get the lists and the tables from the 
public class PopulateComboBoxes {

	public void populateComboBoxes(ConnectorDetectable cd,
			List<Label> outputLabels, Map<Label, Combo> labelMap2)
			throws JSONException, Exception {
		// conWin_inputNames = left over input names .. the names that are not
		// hooked by the
		// connector window
		List<String> conWin_inputNames = cd.getLoInputNames();
		// of the inputs left unhooked by the connector window check if any of
		// them is assigned a value in the composite window. If a value is
		// assigned then do not add it to the list. This is a list of the input
		// names that will be added to the combo boxes
		List<String> lo_inputNames = new ArrayList<>();
		// This a map of all the input names and the values assigned to it in
		// the composite window
		Map<String, String> comp_inputValue = cd.getConnector()
				.getEndingComposite().getComposite_InputsMap();
		for (int i = 0; i < conWin_inputNames.size(); i++) {
			// if the map of the input names and the values assigned to them in
			// the composite window contains the ith input name in the
			// conWin_inputNames (which is a list of input names how hooked in
			// the connector window)
			if (comp_inputValue.containsKey(conWin_inputNames.get(i))) {
				// lo_inputNames list is the list which will hold all the input
				// names that are not hooked in the connector window and well as
				// not assigned a value in the composite window. These are the
				// input names that will added to the combo boxes
				if ((comp_inputValue.get(conWin_inputNames.get(i)).equals(""))) {
					lo_inputNames.add(conWin_inputNames.get(i));
				}
			} else {
				lo_inputNames.add(conWin_inputNames.get(i));
			}
		}

		// get the label, combo map
		Map<Label, Combo> labelMap = labelMap2;
		// get the output labels list
		List<Label> outputLables = outputLabels;
		// get the io connections map
		Map<String, String> connectionsMap = cd.getConnector()
				.getStartingComposite().getConnectionsMap();

		// get each combo box and populate it
		for (int i = 0; i < labelMap.size(); i++) {
			Combo box = labelMap.get(outputLables.get(i));
			// add all the left over input names to the box
			for (int j = 0; j < lo_inputNames.size(); j++) {
				box.add(lo_inputNames.get(j));
			}
			// check if the label is hooked
			if (connectionsMap.get(outputLables.get(i).getText()) != null) {
				if (!(connectionsMap.get(outputLables.get(i).getText())
						.equals("<Select>"))) {
					// if hooked add the input
					box.add(connectionsMap.get(outputLables.get(i).getText()));
					box.setText(connectionsMap.get(outputLables.get(i)
							.getText()));
				} else {
					box.setText("<Select>");
				}

			}

			box.add("<Select>");
		}

	}
}
