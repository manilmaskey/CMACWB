package edu.uah.itsc.workflow.connectorPropertyWindow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Label;
import org.json.JSONException;

import edu.uah.itsc.uah.programview.programObjects.IOPOJO;
import edu.uah.itsc.workflow.connectors.ConnectorDetectable;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class FillInputValues {

	public void fillInputValues(ConnectorDetectable cd, List<Label> outputLabels)
			throws JSONException, Exception {

		// inputs that did not get an input value from the composite window
		List<IOPOJO> inputsNotHooked = new ArrayList<>();
		for (int i = 0; i < cd.getConnector().getEndingComposite()
				.getComposite_InputsMap().size(); i++) {
			// if the map which contains the input name and the value given to
			// it in the composite window dose not contain the ith input name
			// then put the ith input object in the inputsNotHooked list
			if (((cd.getConnector().getEndingComposite()
					.getComposite_InputsMap().keySet()).contains(cd
					.getConnector().getEndingComposite().getProgram_inputs()
					.get(i)))) {
				inputsNotHooked.add(cd.getConnector().getEndingComposite()
						.getProgram_inputs().get(i));
			}
		}

		// get the connection map between inputs and outputs
		Map<String, String> connectionsMap = cd.getConnector()
				.getStartingComposite().getConnectionsMap();
		// list all the input names
		Collection<String> inputNames = connectionsMap.values();
		// get the input in the program with the input name in the list
		// and populate the respective input value
		for (int i = 0; i < inputsNotHooked.size(); i++) {
			// check to see if the input name is there in the list
			if (inputNames.contains(inputsNotHooked.get(i).getTitle())) {
				// if its there populate the respective input value
				for (int j = 0; j < inputNames.toArray().length; j++) {
					if (inputNames.toArray()[j] != null) {
						if (inputNames.toArray()[j].equals(inputsNotHooked.get(
								j).getTitle())) {
							// get respective output name
							String outputName = (String) connectionsMap
									.keySet().toArray()[j];
							// get the index of the output name
							int index = 0;
							for (int k = 0; k < outputLabels.size(); k++) {
								if ((outputLabels.get(k).getText())
										.equals(outputName)) {
									index = k;
								}
							}
							// give input value the value from the output values
							// list
							// with the index
							cd.getConnector()
									.getEndingComposite()
									.getProgram_inputs()
									.get(index)
									.setData_Value(
											cd.getConnector()
													.getStartingComposite()
													.getProgram_outputs()
													.get(index).getData_Value());
						}
						// if any of the value is <Select> replace the
						// respective input value with null
						if (inputNames.contains("<Select>")) {
							// check the input value which has select and
							// replace it with null
							for (int k = 0; k < inputNames.size(); k++) {
								System.out.println("inputName is "
										+ inputNames.toArray()[k]);
								if (inputNames.toArray()[k].equals("<Select>")) {
									cd.getConnector().getEndingComposite()
											.getInputValues().add(k, null);
								}
							}
						}
					}
				}
			}

		}
	}
}
