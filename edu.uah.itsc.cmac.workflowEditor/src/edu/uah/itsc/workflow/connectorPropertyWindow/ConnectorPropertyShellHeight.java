package edu.uah.itsc.workflow.connectorPropertyWindow;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class ConnectorPropertyShellHeight {

	public int calculate_shellHeight(ConnectorDetectable cd) {
		// just number of output because we will only have that many labels
		int maximum = cd.getConnector().getStartingComposite()
				.getNumberOfOutputs();

		/**
		 * heading label height + gap between start of label and shell = 31
		 * height of labels = 21 Separator distance between labels = 27 height
		 * of labels = 23 gap between end of button and shell = 10
		 */
		int height = (maximum * 21) + (maximum * 27) + 33 + 35 + 21;

		return height;
	}

}
