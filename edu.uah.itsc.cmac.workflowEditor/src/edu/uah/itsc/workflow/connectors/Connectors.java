package edu.uah.itsc.workflow.connectors;

import org.eclipse.swt.widgets.Label;

import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This is the class for connectors. A connector between two compositeWrapper
 * objects hold the information about the starting, ending composites and ID's
 * as well as the information about the source and destination labels
 * 
 * @author Rohith Samudrala
 * 
 */
public class Connectors {

	// Global Variables
	CompositeWrapper startingComposite;
	CompositeWrapper endingComposite;
	String startingCompositeID;
	String endingCompositeID;

	Label source;
	Label destination;

	// getters and setters for source and destinations
	public Label getSource() {
		return source;
	}

	public void setSource(Label source) {
		this.source = source;
	}

	public Label getDestination() {
		return destination;
	}

	public void setDestination(Label destination) {
		this.destination = destination;
	}

	// Getters and setters for starting point of composite
	public CompositeWrapper getStartingComposite() {
		return startingComposite;
	}

	public void setStartingComposite(CompositeWrapper startingComposite) {
		this.startingComposite = startingComposite;
	}

	// Getter and setter for ending point of composite
	public CompositeWrapper getEndingComposite() {
		return endingComposite;
	}

	public void setEndingComposite(CompositeWrapper endingComposite) {
		this.endingComposite = endingComposite;
	}

	public String getStartingCompositeID() {
		return startingCompositeID;
	}

	public void setStartingCompositeID(String startingCompositeID) {
		this.startingCompositeID = startingCompositeID;
	}

	public String getEndingCompositeID() {
		return endingCompositeID;
	}

	public void setEndingCompositeID(String endingCompositeID) {
		this.endingCompositeID = endingCompositeID;
	}

}
