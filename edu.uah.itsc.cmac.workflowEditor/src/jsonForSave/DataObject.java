package jsonForSave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataObject {

	int x;
	int y;
	int width;
	int height;
	int numberOfInputs;
	int numberOfOutputs;
	String type, compositeID, methodName;
	List<String> inputValues = new ArrayList<String>();
	Map<String, String> connectionsMap = new HashMap<String, String>();
	Map<String, String> composite_InputMap = new HashMap<String, String>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompositeID() {
		return compositeID;
	}

	public void setCompositeID(String compositeID) {
		this.compositeID = compositeID;
	}

	public List<String> getInputValues() {
		return inputValues;
	}

	public void setInputValues(List<String> inputValues) {
		this.inputValues = inputValues;
	}

	public Map<String, String> getConnectionsMap() {
		return connectionsMap;
	}

	public void setConnectionsMap(Map<String, String> connectionsMap) {
		this.connectionsMap = connectionsMap;
	}

	public Map<String, String> getComposite_InputMap() {
		return composite_InputMap;
	}

	public void setComposite_InputMap(Map<String, String> composite_InputMap) {
		this.composite_InputMap = composite_InputMap;
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

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
