package edu.uah.itsc.workflow.compositePropertyWindowHandlers;

import java.util.List;

import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class CompositePropertyShellHeight {

	public int calculate_CompositePropertyShellHeight(CompositeWrapper method)
			throws Exception {
		
		
		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		final CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap().get(editorName));
		

		int shellHeight = 0;
		ProgramPOJO program = null;
		List<ProgramPOJO> programsList = dataobj.getProgram_List();
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
