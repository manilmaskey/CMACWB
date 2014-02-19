package edu.uah.itsc.workflow.actionHandler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.workflow.compositePropertyWindowHandlers.CompositePropertyShellHeight;
import edu.uah.itsc.workflow.compositePropertyWindowHandlers.PopulateCompositePropertyWindow;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
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

	public void handleCompositeClick(int i, Point point) throws Exception {
		
		
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
		Shell shell = new Shell(methodComposite.getDisplay());
		Display display = methodComposite.getDisplay();
		
		
		//$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

		// call method to calculate the height of the shell
		CompositePropertyShellHeight calculatorObject = new CompositePropertyShellHeight();
		int shellHeight = calculatorObject
				.calculate_CompositePropertyShellHeight(methodComposite);

		// decide where the shell is going to be visible
//		int x = methodComposite.getDisplay().getBounds().x;
//		int y = methodComposite.getDisplay().getBounds().y;
//		int height = methodComposite.getDisplay().getBounds().height;
//		int width = methodComposite.getDisplay().getBounds().width;
		
//		Rectangle screenSize = display.getPrimaryMonitor().getBounds();
//		shell.setBounds(0, 0, 500, shellHeight);
//		shell.setLocation((screenSize.width - shell.getBounds().width) / 2, (screenSize.height - shell.getBounds().height) / 2);
//		
//		int cx = width/2;
//		int cy = (height/2)-150;
//		
//		CompositeWrapper parent = POJOHolder.getInstance().getEditorsmap().get(editorName).getChildCreatorObject().getChildComposite_WorkSpace();
//		int x1 = methodComposite.getLocation().x;
//		int y1 = methodComposite.getLocation().y;
//		System.out.println("x1 = " + x1 + " y1 = " + y1);
//		
//		CopyOfVariablePoJo pojo = (POJOHolder.getInstance().getEditorsmap().get(editorName));
//		System.out.println("x" + pojo.getParentComposite().getLocation().x);
//		System.out.println("y"+ pojo.getParentComposite().getLocation().y);
//		System.out.println("width"+ pojo.getDisplayX());
//		System.out.println("height"+ pojo.getDisplayY());
//		
//		int a=methodComposite.getParent().getBounds().x;
//		int b=methodComposite.getParent().getBounds().y; 
//		int c=methodComposite.getParent().getBounds().width; 
//		int d=methodComposite.getParent().getBounds().height;
				

		// Set Size
		// shell.setSize(500, shellHeight);
		
		int x = methodComposite.getBounds().x;
		int y = methodComposite.getBounds().y;

		// point get the location of the cursor at the event with respect to the screen.
		// we cushion it a little using the x,y coordinates of the method 
		shell.setBounds(point.x, point.y, 500, shellHeight);

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
