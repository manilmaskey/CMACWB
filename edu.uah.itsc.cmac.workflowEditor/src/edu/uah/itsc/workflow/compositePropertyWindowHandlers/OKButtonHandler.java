package edu.uah.itsc.workflow.compositePropertyWindowHandlers;

import java.util.Map;

import org.eclipse.swt.widgets.Button;

import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 *
 */
public class OKButtonHandler {

	Button btnOK;

	public Button getBtnOK() {
		return btnOK;
	}

	public void setBtnOK(Button btnOK) {
		this.btnOK = btnOK;
	}

	public void okButtonHandler(CompositeWrapper method, Map<String, String> newMap
			) {

		/**
		 * get the composite_inputsMap .. this map contain all the labels, texts
		 * in the composite window. Read the data and then store them
		 */
		for (int i = 0; i < newMap.size(); i++) {
			for (int j = 0; j < method.getNumberOfInputs(); j++){
				if (method.getProgram_inputs().get(j).getTitle().equals(newMap.keySet().toArray()[i])){
					method.getProgram_inputs().get(j).setData_Value((String) newMap.values().toArray()[i]);
				}
			}
		}

	}
}
