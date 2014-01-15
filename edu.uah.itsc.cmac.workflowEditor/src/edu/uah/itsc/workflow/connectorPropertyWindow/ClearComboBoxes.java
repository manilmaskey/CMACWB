package edu.uah.itsc.workflow.connectorPropertyWindow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.json.JSONException;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class ClearComboBoxes {

	// left over input names, global as it will be needed to
	// populate the combo boxes
	List<String> lo_inputNames = new ArrayList<String>();

	public void clearComboBoxes(ConnectorDetectable cd,
			List<Label> outputLabels2, Map<Label, Combo> labelMap2)
			throws JSONException, Exception {
		// This process will first populate a list which can be used to fill the
		// combo boxes
		for (int i = 0; i < cd.getConnector().getEndingComposite()
				.getNumberOfInputs(); i++) {
			// get the input values that are hooked
			Collection<String> inputNames = cd.getConnector()
					.getStartingComposite().getConnectionsMap().values();
			// hooked input names
			if (inputNames.contains(cd.getConnector().getEndingComposite()
					.getProgram_inputs().get(i).getTitle())) {
				// this input is already hooked
			} else {
				lo_inputNames.add(cd.getConnector().getEndingComposite()
						.getProgram_inputs().get(i).getTitle());
			}
		}

		// when the lo_inputNames is populates store it in the cd
		cd.setLoInputNames(lo_inputNames);
		cd.getConnector().getStartingComposite().setLoInputNames(lo_inputNames);
		cd.getConnector().getEndingComposite().setLoInputNames(lo_inputNames);

		List<Label> outputLabels = outputLabels2;
		Map<Label, Combo> labelMap = labelMap2;

		// now clear all the combo boxes
		for (int i = 0; i < outputLabels.size(); i++) {
			// get combo box
			Combo box = labelMap.get(outputLabels.get(i));
			// remove all from the box
			box.removeAll();
		}

	}

	public List<String> getLo_inputNames() {
		return lo_inputNames;
	}

	public void setLo_inputNames(List<String> lo_inputNames) {
		this.lo_inputNames = lo_inputNames;
	}

}
