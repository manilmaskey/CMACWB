package edu.uah.itsc.workflow.actionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.workflow.compositePropertyWindowHandlers.CompositePropertyShellHeight;
import edu.uah.itsc.workflow.compositePropertyWindowHandlers.PopulateCompositePropertyWindow;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
//import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
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
		
		
		try{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().doSaveAs();
			}
			catch (Exception e){
				System.out.println("No active page ...(in CompositeClickHandler)");
			}

		//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
//		CompositeWrapper methodComposite = VariablePoJo.getInstance()
//				.getCompositeList().get(i);
//		// create shell
//		Shell shell = new Shell(VariablePoJo.getInstance()
//				.getChildCreatorObject().getChildComposite_WorkSpace()
//				.getDisplay());
		
		
		
		String editorName = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle();
		
		CompositeWrapper methodComposite = (POJOHolder.getInstance().getEditorsmap().get(editorName)).getCompositeList().get(i);
		
		Shell shell = new Shell((POJOHolder.getInstance().getEditorsmap().get(editorName)).getChildCreatorObject().getChildComposite_WorkSpace().getDisplay());
		
		
		//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

		// call method to calculate the height of the shell
		CompositePropertyShellHeight calculatorObject = new CompositePropertyShellHeight();
		int shellHeight = calculatorObject
				.calculate_CompositePropertyShellHeight(methodComposite);

		// decide where the shell is going to be visible
		int x = methodComposite.getBounds().x;
		int y = methodComposite.getBounds().y;

		// Set Size
		// shell.setSize(500, shellHeight);

		shell.setBounds(x + 500, y + 200, 500, shellHeight);

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
