package edu.uah.itsc.uah.programview.programObjects;

import java.util.ArrayList;
import java.util.List;

public class ProgramPOJO {
	
//	// Global Variables
//	// Name of the Method
//	String methodName;
//	String description = "";
//	
//	// Number of Inputs and Outputs in the Method
//	int numberOfInputs;
//	int numberOfOutputs;
//	
//	// Lists for input/output names and values
//	List<String> inputNames = new ArrayList<String>();
//	List<String> outputNames = new ArrayList<String>();
//	List<Integer> inputValues = new ArrayList<Integer>(Collections.nCopies(numberOfInputs, 0));
//	List<Integer> outputValues = new ArrayList<Integer>();
//	
//	
//	// Getters and Setters for the Global Variables
//	public String getMethodName() {
//		return methodName;
//	}
//	public void setMethodName(String methodName) {
//		this.methodName = methodName;
//	}
//	public int getNumberOfInputs() {
//		return numberOfInputs;
//	}
//	public void setNumberOfInputs(int numberOfInputs) {
//		this.numberOfInputs = numberOfInputs;
//	}
//	public int getNumberOfOutputs() {
//		return numberOfOutputs;
//	}
//	public void setNumberOfOutputs(int numberOfOutputs) {
//		this.numberOfOutputs = numberOfOutputs;
//	}
//	public List<String> getInputNames() {
//		return inputNames;
//	}
//	public void setInputNames(List<String> inputNames) {
//		this.inputNames = inputNames;
//	}
//	public List<String> getOutputNames() {
//		return outputNames;
//	}
//	public void setOutputNames(List<String> outputNames) {
//		this.outputNames = outputNames;
//	}
//	public List<Integer> getInputValues() {
//		return inputValues;
//	}
//	public void setInputValues(List<Integer> inputValues) {
//		this.inputValues = inputValues;
//	}
//	public List<Integer> getOutputValues() {
//		return outputValues;
//	}
//	public void setOutputValues(List<Integer> outputValues) {
//		this.outputValues = outputValues;
//	}
//	public String getDescription() {
//		return description;
//	}
//	public void setDescription(String description) {
//		this.description = description;
//	}
//	

	int vid;
	int input_Count;
	int output_Count;
	String title;
	String uri;
	List<IOPOJO> input_List = new ArrayList<>();
	List<IOPOJO> output_List = new ArrayList<>();
	
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getInput_Count() {
		return input_Count;
	}
	public void setInput_Count(int input_Count) {
		this.input_Count = input_Count;
	}
	public int getOutput_Count() {
		return output_Count;
	}
	public void setOutput_Count(int output_Count) {
		this.output_Count = output_Count;
	}
	public List<IOPOJO> getInput_List() {
		return input_List;
	}
	public void setInput_List(List<IOPOJO> input_List) {
		this.input_List = input_List;
	}
	public List<IOPOJO> getOutput_List() {
		return output_List;
	}
	public void setOutput_List(List<IOPOJO> output_List) {
		this.output_List = output_List;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
}
