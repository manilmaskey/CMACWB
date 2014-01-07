package edu.uah.itsc.workflow.actionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import edu.uah.itsc.workflow.compositePropertyWindowHandlers.CompositePropertyShellHeight;
import edu.uah.itsc.workflow.compositePropertyWindowHandlers.PopulateCompositePropertyWindow;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible for populating and opening a new window when double
 * click is detected on method
 * 
 * @author Rohith Samudrala
 * 
 */
public class CompositeClickHandler {

	public void handleCompositeClick(int i) throws Exception {

		CompositeWrapper methodComposite = VariablePoJo.getInstance()
				.getCompositeList().get(i);
		// create shell
		Shell shell = new Shell(VariablePoJo.getInstance()
				.getChildCreatorObject().getChildComposite_WorkSpace()
				.getDisplay());

		// call method to calculate the height of the shell
		CompositePropertyShellHeight calculatorObject = new CompositePropertyShellHeight();
		int shellHeight = calculatorObject
				.calculate_CompositePropertyShellHeight(methodComposite);

		// Set Size
		shell.setSize(500, shellHeight);
		// Set Title
		shell.setText(methodComposite.getMethodName() + "'s Inputs");
		// Set Layout
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		// New Composite
		Composite composite = new Composite(shell, SWT.NONE);

		// Call method to populate the window
		PopulateCompositePropertyWindow populatorObject = new PopulateCompositePropertyWindow();
		populatorObject.populateWindow(methodComposite, shell, composite);

		shell.open();

	}
}
