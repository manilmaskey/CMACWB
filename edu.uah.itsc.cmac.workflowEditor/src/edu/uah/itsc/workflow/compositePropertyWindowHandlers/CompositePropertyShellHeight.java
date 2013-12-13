package edu.uah.itsc.workflow.compositePropertyWindowHandlers;

import java.util.List;

import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class CompositePropertyShellHeight {

	public int calculate_CompositePropertyShellHeight(CompositeWrapper method)
			throws Exception {

		int shellHeight = 0;
		ProgramPOJO program = null;
		List<ProgramPOJO> programsList = VariablePoJo.getInstance()
				.getProgram_List();
		System.out.println(method.getMethodName());
		for (int i = 0; i < programsList.size(); i++) {
			if (programsList.get(i).getTitle().equals(method.getMethodName())) {
				program = programsList.get(i);
			}
		}

		shellHeight = (method.getNumberOfInputs() * 13);

		// add the seperator distance to the shell height
		shellHeight = (shellHeight + method.getNumberOfInputs() * 22);

		// add the height of the buttons = 23 and a little padding = 35
		shellHeight = (shellHeight + 23 + 45);

		return shellHeight;
	}

}
