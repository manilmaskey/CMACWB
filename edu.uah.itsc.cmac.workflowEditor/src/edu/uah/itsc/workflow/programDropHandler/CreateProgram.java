package edu.uah.itsc.workflow.programDropHandler;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;
import edu.uah.itsc.workflow.actionHandler.CompositeClickHandler;
import edu.uah.itsc.workflow.actionHandler.ConnectorClickHandler;
import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.relayComposites.RelayComposites;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible for creating the methods
 * 
 * @author Rohith Samudrala
 * 
 */
public class CreateProgram {

	/**
	 * Global Variables
	 */
	CompositeWrapper methodComposite;
	Label inflow;
	Label outflow;

	public Label getInflow() {
		return inflow;
	}

	public void setInflow(Label inflow) {
		this.inflow = inflow;
	}

	public Label getOutflow() {
		return outflow;
	}

	public void setOutflow(Label outflow) {
		this.outflow = outflow;
	}

	public CompositeWrapper getMethodComposite() {
		return methodComposite;
	}

	public void setMethodComposite(CompositeWrapper methodComposite) {
		this.methodComposite = methodComposite;
	}

	/**
	 * This method is responsible for creating the methods dragged and dropped
	 * onto the work space from the methods view
	 * 
	 * @param x
	 *            - x- coordinate of where the drop occurred
	 * @param y
	 *            - y - coordinate of the where the drop occurred
	 * @param obj
	 *            - The object that was dropped onto the workspace
	 * @throws Exception
	 */
	public void createMethod(int x, int y, Object obj) throws Exception {

		/**
		 * Identify the object that was dropped onto the work space
		 */
		List<CompositeWrapper> compositesList = VariablePoJo.getInstance()
				.getCompositeList(); // List of composites

		List<ProgramPOJO> programsList = VariablePoJo.getInstance()
				.getProgram_List();
		ProgramPOJO program = null;

		for (int i = 0; i < programsList.size(); i++) {
			if (obj.toString().equals(
					"[" + programsList.get(i).getTitle() + "]")) {
				program = programsList.get(i);
			}
		}

		/**
		 * Create the method composite
		 */
		// Get the workspace where the method composite is to be created
		final CompositeWrapper ChildComposite_WorkSpace = VariablePoJo
				.getInstance().getChildCreatorObject()
				.getChildComposite_WorkSpace();

		// Create a new method Composite
		methodComposite = new CompositeWrapper(ChildComposite_WorkSpace,
				SWT.NONE);

		// Height/Width of the parent composite - Height/Width of the
		// Child Work Space Composite
		System.out.println("the child composite dimentions x,y,w,h"
				+ ChildComposite_WorkSpace.getBounds().x + ","
				+ ChildComposite_WorkSpace.getBounds().y + ","
				+ ChildComposite_WorkSpace.getBounds().width + ","
				+ ChildComposite_WorkSpace.getBounds().height);
		System.out.println("the parent composite dimentions w,h"
				+ VariablePoJo.getInstance().getDisplayX() + ","
				+ VariablePoJo.getInstance().getDisplayY());

		int x1 = (ChildComposite_WorkSpace.getBounds().width - 987);
		final int width_difference = (362 - x1);
		final int height_difference = 112;

		methodComposite.setBounds(x - width_difference, y - height_difference,
				240, 40);

		methodComposite.setBackground(ChildComposite_WorkSpace.getDisplay()
				.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));

		// setting the composite id
		int method1_IDCounter = VariablePoJo.getInstance()
				.getMethod1_IDCounter();
		String methodID = "Method1 " + method1_IDCounter; // method1 id
		method1_IDCounter++; // increment the id counter

		// setting back the ID counter
		VariablePoJo.getInstance().setMethod1_IDCounter(method1_IDCounter);
		methodComposite.setCompositeID(methodID); // setting the method id

		// Set the name of method as method type
		methodComposite.setType(program.getTitle());

		// Label for the method
		Label label = new Label(methodComposite, SWT.CENTER);
		methodComposite.setTitleLabel(label);
		Color black = new Color(ChildComposite_WorkSpace.getDisplay(), 0, 0, 0);
		label.setForeground(black);
		label.setBounds(0, 0, 240, 15);
		label.setText(program.getTitle());

		label.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event e) {
				CompositeClickHandler handlerObject = new CompositeClickHandler();
				for (int i = 0; i < VariablePoJo.getInstance()
						.getCompositeList().size(); i++) {
					if (VariablePoJo.getInstance().getCompositeList().get(i)
							.getCompositeID()
							.equals(methodComposite.getCompositeID())) {
						try {
							handlerObject.handleCompositeClick(i);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		label.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event e) {

				if (VariablePoJo.getInstance().getSelected_composite() != null) {

					if (VariablePoJo.getInstance().getSelected_composite()
							.getCompositeID()
							.equals(methodComposite.getCompositeID())) {
						CompositeWrapper composite = VariablePoJo.getInstance()
								.getSelected_composite();
						// Label label = VariablePoJo.getInstance()
						// .getTitleLabel();
						composite
								.setBackground(VariablePoJo
										.getInstance()
										.getChildCreatorObject()
										.getChildComposite_WorkSpace()
										.getDisplay()
										.getSystemColor(
												SWT.COLOR_WIDGET_NORMAL_SHADOW));
						VariablePoJo.getInstance().setSelected_composite(null);
					} else {
						CompositeWrapper composite = VariablePoJo.getInstance()
								.getSelected_composite();
						// Label label = VariablePoJo.getInstance()
						// .getTitleLabel();
						composite
								.setBackground(VariablePoJo
										.getInstance()
										.getChildCreatorObject()
										.getChildComposite_WorkSpace()
										.getDisplay()
										.getSystemColor(
												SWT.COLOR_WIDGET_NORMAL_SHADOW));
					}
				}

				methodComposite.forceFocus();
				if (methodComposite.forceFocus() == true) {
					System.out.println("force focus = " + true);
				} else {
					System.out.println("force focus = " + false);
				}

				methodComposite.setBackground(VariablePoJo.getInstance()
						.getChildCreatorObject().getChildComposite_WorkSpace()
						.getDisplay()
						.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));

				VariablePoJo.getInstance().setSelected_composite(
						methodComposite);
				VariablePoJo.getInstance().setTitleLabel(
						methodComposite.getTitleLabel());

			}
		});

		// incoming label for drop target
		final Label inComing = new Label(methodComposite, SWT.BORDER);
		inComing.setBounds(0, 15, 14, 13);
		inComing.setText(".");
		inComing.setBackground(ChildComposite_WorkSpace.getDisplay()
				.getSystemColor(SWT.COLOR_DARK_RED));
		// will be used by save option to recreate the work flow
		inflow = inComing;

		// outgoing label for drag source
		final Label outGoing = new Label(methodComposite, SWT.BORDER);
		outGoing.setBounds(227, 15, 14, 13);
		outGoing.setText(".");
		outGoing.setBackground(ChildComposite_WorkSpace.getDisplay()
				.getSystemColor(SWT.COLOR_BLUE));
		// will be used by save option to recreate the work flow
		outflow = outGoing;

		// Set a drag source
		DragSource ds = new DragSource(outGoing, DND.DROP_MOVE | DND.DROP_COPY
				| DND.DROP_LINK);
		ds.setTransfer(new Transfer[] { TextTransfer.getInstance() });

		// Set a drop target
		DropTarget dt = new DropTarget(inComing, DND.DROP_MOVE | DND.DROP_COPY
				| DND.DROP_LINK);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });

		/**
		 * Handle drag source
		 */
		ds.addDragListener(new DragSourceAdapter() {
			public void dragSetData(DragSourceEvent event) {
				// Set Data
				event.data = outGoing.getText();

				// Create a new connector object
				Connectors connectorObj = VariablePoJo.getInstance()
						.getConnectorObj();
				connectorObj = new Connectors();

				// Set the starting composite into the connector
				CompositeWrapper startingComposite = methodComposite;
				connectorObj.setStartingComposite(startingComposite);

				// Setting the source
				connectorObj.setSource(outGoing);

				// get the starting composite id from the starting composite
				// and set it to the connector object
				connectorObj.setStartingCompositeID(startingComposite
						.getCompositeID());

				// Set the connector object into a list of connectors
				VariablePoJo.getInstance().setConnectorObj(connectorObj);

			}
		});

		/**
		 * Handle drop target
		 */
		dt.addDropListener(new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				// Get the connector object initiated
				// by the drag source
				Connectors connectorObj = VariablePoJo.getInstance()
						.getConnectorObj();

				// Set the ending composite in to the connector
				CompositeWrapper endingComposite = methodComposite;
				connectorObj.setEndingComposite(endingComposite);

				// Setting the destination
				connectorObj.setDestination(inComing);

				// set the ending composite id into the connector
				connectorObj.setEndingCompositeID(endingComposite
						.getCompositeID());

				// add the connector object to the connector list
				VariablePoJo.getInstance().getConnectorList().add(connectorObj);

				// Save the connector object in a Connector Detectable
				// object
				CompositeWrapper childCompositeWorkSpace = VariablePoJo
						.getInstance().getChildCreatorObject()
						.getChildComposite_WorkSpace();

				/**
				 * a new cd is added when drop occurs then cd is added to the
				 * list of cd's
				 */
				ConnectorDetectable cd = new ConnectorDetectable(
						childCompositeWorkSpace, SWT.NONE);
				cd.setConnector(connectorObj);
				// 2nd instance of adding cd
				VariablePoJo.getInstance().getConnectorDetectableList().add(cd);

				// Add handlers for composite detectable
				ConnectorClickHandler handlerObject = new ConnectorClickHandler();
				handlerObject.addConnectorHandlers(cd);

				/**
				 * Redraw everything
				 */
				RelayComposites relayCompositesObject = new RelayComposites();
				relayCompositesObject.reDraw();

			}
		});

		/**
		 * Get the method object structure (properties) and store it in the
		 * composite
		 */
		methodComposite.setMethodName(program.getTitle());
		// Store input and output objects
		methodComposite.setProgram_inputs(program.getInput_List());
		methodComposite.setProgram_outputs(program.getOutput_List());
		methodComposite.setNumberOfInputs(program.getInput_Count());
		methodComposite.setNumberOfOutputs(program.getOutput_Count());

		compositesList.add(methodComposite);
	}

}
