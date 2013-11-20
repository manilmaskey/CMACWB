package edu.uah.itsc.workflow.connectorPropertyWindow;

import org.json.JSONObject;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class GetOutputValues {

	/**
	 * This method reads the output link of the programs and populate the local
	 * output value list with the output values of the program
	 */
	public void getProgramsOutputValues(ConnectorDetectable cd) {

		for (int i = 0; i < cd.getConnector().getStartingComposite()
				.getNumberOfOutputs(); i++) {
			String out_value = null;
			JSONObject outputObj;

		}

	}

}
