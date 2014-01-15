package edu.uah.itsc.workflow.wrapperClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.uah.itsc.uah.programview.programObjects.IOPOJO;


/**
 * CompositeWrapper class extends composite this class enables us to give added
 * functionality to our composite.
 * 
 * @author Rohith Samudrala
 * 
 */
public class CompositeWrapper extends Composite /*
												 * implements
												 * SerializableCompatibility
												 */{

	public CompositeWrapper(Composite parent, int style) {
		super(parent, style);
	}

	// Global Variables
	String compositeID;
	String type;
	String methodName;

	// Number of Inputs and Outputs in the Method
	int numberOfInputs;
	int numberOfOutputs;

	List<String> inputValues = new ArrayList<String>();
	List<String> textList = new ArrayList<String>();
	List<String> loInputNames = new ArrayList<String>();

	public List<String> getLoInputNames() {
		return loInputNames;
	}

	public void setLoInputNames(List<String> loInputNames) {
		this.loInputNames = loInputNames;
	}

	Map<String, String> composite_InputsMap = new HashMap<String, String>();

	/**
	 * Hold the information regarding the labels and their corresponding combo
	 * boxes for the outputs in a connector
	 */
	Map<Label, String> IOConnections = new HashMap<Label, String>();

	// Holds input JSONObjects
	List<IOPOJO> program_inputs = new ArrayList<IOPOJO>();
	List<IOPOJO> program_outputs = new ArrayList<IOPOJO>();

	// the connection between output name and input name
	Map<String, String> connectionsMap = new HashMap<String, String>();

	Label titleLabel;

	public Label getTitleLabel() {
		return titleLabel;
	}

	public void setTitleLabel(Label label) {
		this.titleLabel = label;
	}

	// Getters and Setters for Global Variables
	public String getCompositeID() {
		return compositeID;
	}

	public void setCompositeID(String compositeID) {
		this.compositeID = compositeID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getNumberOfInputs() {
		return numberOfInputs;
	}

	public void setNumberOfInputs(int numberOfInputs) {
		this.numberOfInputs = numberOfInputs;
	}

	public int getNumberOfOutputs() {
		return numberOfOutputs;
	}

	public void setNumberOfOutputs(int numberOfOutputs) {
		this.numberOfOutputs = numberOfOutputs;
	}

	public List<String> getInputValues() {
		return inputValues;
	}

	public void setInputValues(List<String> inputValues) {
		this.inputValues = inputValues;
	}

	public List<String> getTextList() {
		return textList;
	}

	public void setTextList(List<String> textList) {
		this.textList = textList;
	}

	public Map<Label, String> getIOConnections() {
		return IOConnections;
	}

	public void setIOConnections(Map<Label, String> iOConnections) {
		IOConnections = iOConnections;
	}

	public List<IOPOJO> getProgram_inputs() {
		return program_inputs;
	}

	public void setProgram_inputs(List<IOPOJO> list) {
		this.program_inputs = list;
	}

	public List<IOPOJO> getProgram_outputs() {
		return program_outputs;
	}

	public void setProgram_outputs(List<IOPOJO> list) {
		this.program_outputs = list;
	}

	public Map<String, String> getConnectionsMap() {
		return connectionsMap;
	}

	public void setConnectionsMap(Map<String, String> connectionsMap) {
		this.connectionsMap = connectionsMap;
	}

	public Map<String, String> getComposite_InputsMap() {
		return composite_InputsMap;
	}

	public void setComposite_InputsMap(Map<String, String> composite_InputsMap) {
		this.composite_InputsMap = composite_InputsMap;
	}

}
