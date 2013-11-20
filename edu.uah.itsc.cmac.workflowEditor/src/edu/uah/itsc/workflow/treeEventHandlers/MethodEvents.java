package edu.uah.itsc.workflow.treeEventHandlers;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.uah.itsc.workflow.actionHandler.CompositeClickHandler;
import edu.uah.itsc.workflow.actionHandler.MethodHandlerUpdated;
import edu.uah.itsc.workflow.movementTrackers.MethodCompositeTracker;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible for handling any action on method's view panel
 * 
 * @author Rohith Samudrala
 * 
 */
public class MethodEvents {

	int x;
	int y;

	public MethodEvents(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	/**
	 * This method checks the event on view panel and calls the respective
	 * handler
	 * 
	 * @param obj
	 *            - This object contains the data of the action occurred
	 */
	@SuppressWarnings("unused")
	public void MethodEventsHandler(Object obj) {

		// Get the list of all methods
		final List<edu.uah.itsc.uah.programview.programObjects.ProgramPOJO> methodsList = VariablePoJo
				.getInstance().getProgram_List();
		// Is the selected a method
		loop: for (int i = 0; i < methodsList.size(); i++) {
			if (obj.toString()
					.equals("[" + methodsList.get(i).getTitle() + "]")) {
				System.out.println(obj.toString() + " found");
				System.out.println("It has "
						+ methodsList.get(i).getInput_Count() + " inputs "
						+ methodsList.get(i).getOutput_Count() + " outputs");
				System.out.println("The location of method is (" + x + "," + y
						+ ")");

				MethodHandlerUpdated handlerObject = new MethodHandlerUpdated();
				handlerObject.createMethod(x, y, obj);

				// Add tracker to the method
				final CompositeWrapper methodComposite = handlerObject
						.getMethodComposite();
				methodComposite.addListener(SWT.MouseDown, new Listener() {
					public void handleEvent(Event e) {

						MethodCompositeTracker methodTrackerObject = new MethodCompositeTracker();
						methodTrackerObject.setCompositeList(VariablePoJo
								.getInstance().getCompositeList());
						methodTrackerObject.setMethodComposite(methodComposite);
						methodTrackerObject
								.setChildComposite_WorkSpace(VariablePoJo
										.getInstance().getChildCreatorObject()
										.getChildComposite_WorkSpace());
						methodTrackerObject.setParentComposite(VariablePoJo
								.getInstance().getParentComposite());
						methodTrackerObject.setConnectorList(VariablePoJo
								.getInstance().getConnectorList());
						methodTrackerObject.methodTracker();
					}
				});

				/**
				 * Double click listener for loading the data
				 */
				methodComposite.addMouseListener(new MouseListener() {

					@Override
					public void mouseUp(MouseEvent e) {
					}

					@Override
					public void mouseDown(MouseEvent e) {
					}

					@Override
					public void mouseDoubleClick(MouseEvent e) {

						CompositeClickHandler handlerObject = new CompositeClickHandler();
						try {
							for (int i = 0; i < VariablePoJo.getInstance()
									.getCompositeList().size(); i++) {
								if (VariablePoJo
										.getInstance()
										.getCompositeList()
										.get(i)
										.getCompositeID()
										.equals(methodComposite
												.getCompositeID())) {
									handlerObject.handleCompositeClick(i);
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				});
			}
		}
	}

}
