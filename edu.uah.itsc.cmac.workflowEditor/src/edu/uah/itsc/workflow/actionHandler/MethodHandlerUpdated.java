package edu.uah.itsc.workflow.actionHandler;

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
import org.eclipse.swt.widgets.Label;

import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;
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
public class MethodHandlerUpdated {

	/**
	 * Global Variables
	 */
	CompositeWrapper methodComposite;
	
	/**
	 * Getters and Setters for Global Variables
	 */
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
	 */
	public void createMethod(int x, int y, Object obj) {

		/**
		 * Identify the object that was dropped onto the work space
		 */
		List<ProgramPOJO> methodsList = VariablePoJo.getInstance().getProgram_List();

		List<CompositeWrapper> compositesList = VariablePoJo.getInstance()
				.getCompositeList();	// List of composites

		ProgramPOJO methodObject = null;

		for (int i = 0; i < methodsList.size(); i++) {
			if (("[" + methodsList.get(i).getTitle()+ "]").equals(obj
					.toString())) {
				methodObject = methodsList.get(i); // Match the object

			}
		}

		/**
		 * Create the method composite
		 */
		// Get the workspace where the method composite is to be created
		CompositeWrapper ChildComposite_WorkSpace = VariablePoJo.getInstance()
				.getChildCreatorObject().getChildComposite_WorkSpace();

		// Create a new method Composite
		methodComposite = new CompositeWrapper(
				ChildComposite_WorkSpace, SWT.NONE);
		
		// Height/Width of the parent composite - Height/Width of the
		// Child Work Space Composite
		int widthLeft = VariablePoJo.getInstance().getDisplayX() - 1300;
		int heightLeft = VariablePoJo.getInstance().getDisplayY() - 550;
		int ycoord = y - heightLeft;
		int xcoord = x - widthLeft;

		methodComposite.setBounds(xcoord, ycoord, 60, 60);
		methodComposite.setBackground(ChildComposite_WorkSpace.getDisplay()
				.getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));

		// setting the composite id
		int method1_IDCounter = VariablePoJo.getInstance()
				.getMethod1_IDCounter();
		String methodID = "Method1 " + method1_IDCounter; // method1 id
		method1_IDCounter++; // increment the id counter

		// setting back the ID counter
		VariablePoJo.getInstance().setMethod1_IDCounter(method1_IDCounter);
		methodComposite.setCompositeID(methodID); // setting the method id

		// Set the name of method as method type
		methodComposite.setType(methodObject.getTitle());

		// Label for the method
		Label label = new Label(methodComposite, SWT.CENTER | SWT.BORDER
				| SWT.HORIZONTAL);
		Color white = new Color(ChildComposite_WorkSpace.getDisplay(), 255,
				255, 255);
		Color black = new Color(ChildComposite_WorkSpace.getDisplay(), 0,
				0, 0);
		label.setForeground(black);
		label.setBackground(white);
		label.setText(methodObject.getTitle());
		label.pack();
		
		// incoming label for drop target
		final Label inComing = new Label(methodComposite, SWT.BORDER);
		inComing.setBounds(0, 25, 14, 13);
		inComing.setText(">");
		inComing.setBackground(white);
		
		// outgoing label for drag source
		final Label outGoing = new Label(methodComposite, SWT.BORDER);
		outGoing.setBounds(47, 25, 14, 13);
		outGoing.setText(">");
		outGoing.setBackground(white);
		
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
				event.data =outGoing.getText();
				
				// Create a new connector object
				Connectors connectorObj = VariablePoJo
						.getInstance().getConnectorObj();
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
				
				//Set the connector object into a list of connectors
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
				Connectors connectorObj = VariablePoJo
						.getInstance().getConnectorObj();

				// Set the ending composite in to the connector
				CompositeWrapper endingComposite = methodComposite;
				connectorObj.setEndingComposite(endingComposite);

				// Setting the destination
				connectorObj.setDestination(inComing);

				// set the ending composite id into the connector
				connectorObj.setEndingCompositeID(endingComposite
						.getCompositeID());

				// add the connector object to the connector list
				VariablePoJo.getInstance().getConnectorList()
						.add(connectorObj);
				
				// Save the connector object in a Connector Detectable
				// object
				CompositeWrapper childCompositeWorkSpace = VariablePoJo
						.getInstance().getChildCreatorObject()
						.getChildComposite_WorkSpace();
			
				/**
				 * a new cd is added when drop occurs
				 * then cd is added to the list of cd's
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
				VariablePoJo variablePoJoInstance = VariablePoJo
						.getInstance();
				
				/**
				 * Set all the required data
				 */
				relayCompositesObject
						.setChildComposite_WorkSpace(variablePoJoInstance
								.getChildCreatorObject()
								.getChildComposite_WorkSpace());
				relayCompositesObject.setCompositeList(variablePoJoInstance
						.getCompositeList());
				relayCompositesObject
						.setConnectorList(variablePoJoInstance
								.getConnectorList());
				relayCompositesObject
						.setParentComposite(variablePoJoInstance
								.getParentComposite());
				relayCompositesObject.reDraw();
				
			}
		});
		
		/**
		 * Get the method object structure (properties)
		 * and store it in the composite
		 */
		methodComposite.setMethodName(methodObject.getTitle());
		methodComposite.setNumberOfInputs(methodObject.getInput_Count());
		methodComposite.setNumberOfOutputs(methodObject.getOutput_Count());

		compositesList.add(methodComposite);
	}

}
