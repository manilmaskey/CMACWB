package edu.uah.itsc.workflow.variableHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Label;

import piworkflow.editors.MultiPageEditor;
import edu.uah.itsc.cmac.programview.programsHolder.ProgramsHolder;
import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;
import edu.uah.itsc.workflow.childComposites.ChildCompositeCreator;
import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * Singleton Class which will hold the global variable of the multi-page editor
 * class
 * 
 * @author Rohith Samudrala
 * 
 */
public class CopyOfVariablePoJo /* implements SerializableCompatibility */{

	// Global Variables
	Connectors connectorObj;
	CompositeWrapper startingComposite;
	CompositeWrapper endingComposite;
	
	CompositeWrapper parentComposite;
	ScrolledComposite parent;
	
	ChildCompositeCreator childCreatorObject;
	CompositeWrapper selected_composite;
	Label titleLabel;

	boolean isCreated = false;
	boolean isFile;

	int displayX;
	int displayY;

	// Variables for composite ID's
	// int outputID = 0;
	int method1_IDCounter = 0;
	// int method2_IDCounter = 0;
	// int inputID = 0;

	// list of programs read from save
	List<CompositeWrapper> programsFromSave = new ArrayList<CompositeWrapper>();

	// list of composite on the editor
	List<CompositeWrapper> compositeList = new ArrayList<CompositeWrapper>();
	// list of connectors between the editors
	List<Connectors> connectorList = new ArrayList<Connectors>();
	// list of the connector detectable
	List<ConnectorDetectable> connectorDetectableList = new ArrayList<ConnectorDetectable>();

	// list of connections for the input values
	Map<Label, String> IOConnections = new HashMap<Label, String>();
	List<String> inputsHooked = new ArrayList<String>();

	// List containing objects of all the programs.
	List<ProgramPOJO> program_List;

//	MultiPageEditor editor;
	
	ArrayList<CompositeWrapper> childcompositewrappers = new ArrayList<CompositeWrapper>();

	// -------------------------------------------------

	// Getters and Setters for the global variables

	public CompositeWrapper getSelected_composite() {
		return selected_composite;
	}
	
	

//	public MultiPageEditor getEditor() {
//		return editor;
//	}
//
//	public void setEditor(MultiPageEditor editor) {
//		this.editor = editor;
//	}

	public ScrolledComposite getParent() {
		return parent;
	}



	public void setParent(ScrolledComposite parent) {
		this.parent = parent;
	}



	public ArrayList<CompositeWrapper> getChildcompositewrappers() {
		return childcompositewrappers;
	}

	public void setChildcompositewrappers(
			ArrayList<CompositeWrapper> childcompositewrappers) {
		this.childcompositewrappers = childcompositewrappers;
	}

	public void setSelected_composite(CompositeWrapper selected_composite) {
		this.selected_composite = selected_composite;
	}

	public int getDisplayX() {
		return displayX;
	}

	public void setDisplayX(int displayX) {
		this.displayX = displayX;
	}

	public int getDisplayY() {
		return displayY;
	}

	public void setDisplayY(int displayY) {
		this.displayY = displayY;
	}

	public Connectors getConnectorObj() {
		return connectorObj;
	}

	public int getMethod1_IDCounter() {
		return method1_IDCounter;
	}

	public void setMethod1_IDCounter(int method1_IDCounter) {
		this.method1_IDCounter = method1_IDCounter;
	}

	public void setConnectorObj(Connectors connectorObj) {
		this.connectorObj = connectorObj;
	}

	public CompositeWrapper getStartingComposite() {
		return startingComposite;
	}

	public void setStartingComposite(CompositeWrapper startingComposite) {
		this.startingComposite = startingComposite;
	}

	public CompositeWrapper getEndingComposite() {
		return endingComposite;
	}

	public void setEndingComposite(CompositeWrapper endingComposite) {
		this.endingComposite = endingComposite;
	}

	public List<CompositeWrapper> getCompositeList() {
		return compositeList;
	}

	public void setCompositeList(List<CompositeWrapper> compositeList) {
		this.compositeList = compositeList;
	}

	public List<Connectors> getConnectorList() {
		return connectorList;
	}

	public void setConnectorList(List<Connectors> connectorList) {
		this.connectorList = connectorList;
	}

	public CompositeWrapper getParentComposite() {
		return parentComposite;
	}

	public void setParentComposite(CompositeWrapper parentComposite) {
		this.parentComposite = parentComposite;
	}

	public ChildCompositeCreator getChildCreatorObject() {
		return childCreatorObject;
	}

	public void setChildCreatorObject(ChildCompositeCreator childCreatorObject) {
		this.childCreatorObject = childCreatorObject;
	}

	public List<ConnectorDetectable> getConnectorDetectableList() {
		return connectorDetectableList;
	}

	public void setConnectorDetectableList(
			List<ConnectorDetectable> connectorDetectableList) {
		this.connectorDetectableList = connectorDetectableList;
	}

	public Map<Label, String> getIOConnections() {
		return IOConnections;
	}

	public void setIOConnections(Map<Label, String> iOConnections) {
		IOConnections = iOConnections;
	}

	public List<String> getInputsHooked() {
		return inputsHooked;
	}

	public void setInputsHooked(List<String> inputsHooked) {
		this.inputsHooked = inputsHooked;
	}

	public List<ProgramPOJO> getProgram_List() {
		return program_List;
	}

	// modified to read program objects from sree's program view
	public void setProgram_List() {
		this.program_List = ProgramsHolder.getInstance().getPrograms_list();
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

	public List<CompositeWrapper> getProgramsFromSave() {
		return programsFromSave;
	}

	public void setProgramsFromSave(List<CompositeWrapper> programsFromSave) {
		this.programsFromSave = programsFromSave;
	}

	public Label getTitleLabel() {
		return titleLabel;
	}

	public void setTitleLabel(Label titleLabel) {
		this.titleLabel = titleLabel;
	}

	public boolean isCreated() {
		return isCreated;
	}

	public void setCreated(boolean isCreated) {
		this.isCreated = isCreated;
	}

}
