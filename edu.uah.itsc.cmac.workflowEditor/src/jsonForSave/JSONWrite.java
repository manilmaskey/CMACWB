package jsonForSave;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.refresh.IRefreshMonitor;
import org.eclipse.core.resources.refresh.IRefreshResult;
import org.eclipse.core.resources.refresh.RefreshProvider;
import org.eclipse.ui.PlatformUI;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible for creating a JSON file The created JSON file
 * comprises of all the data required to recreate the PIWorkFlow work space This
 * is trigger by the action handler for Save button
 * 
 * @author Rohith Samudrala
 * 
 */
public class JSONWrite extends RefreshProvider {

	/**
	 * This method is responsible for creating the JSON file
	 * 
	 * @param filepath
	 *            is the name of the file
	 */
	@SuppressWarnings("unchecked")
	public void createJSONFile(String filepath, String filename) {

		// parent JSONObject this will have 2 keys the first key is a programs
		// list and the second key is an object which will hold all the data in
		// VariablePOJO class
		JSONObject obj = new JSONObject();

		String editorName = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActiveEditor()
				.getTitle();
		CopyOfVariablePoJo dataobj = (POJOHolder.getInstance().getEditorsmap()
				.get(editorName));

		// VariablePoJo instance = VariablePoJo.getInstance();
		// -----------------------------------------------------------

		// Composites List
		// programs list is composites list
		// need bounds
		JSONArray programsList = new JSONArray();
		List<CompositeWrapper> compositeList = dataobj.getCompositeList();

		for (int i = 0; i < compositeList.size(); i++) {

			JSONObject programObject = new JSONObject();
			CompositeWrapper program = compositeList.get(i);

			// composite id
			String compositeID = program.getCompositeID();
			// type
			String type = program.getType();
			// method name
			String methodName = program.getMethodName();
			// number of inputs
			int numberOfInputs = program.getNumberOfInputs();
			// number of outputs
			int numberOfOutputs = program.getNumberOfOutputs();

			int x = program.getBounds().x;
			int y = program.getBounds().y;
			int width = program.getBounds().width;
			int height = program.getBounds().height;

			// inputs
			// int vid = program.getProgram_inputs().get(j).getVid(); changes i
			// to j
			JSONArray inputs = new JSONArray();
			for (int j = 0; j < program.getProgram_inputs().size(); j++) {
				JSONObject inputObject = new JSONObject();
				int vid = program.getProgram_inputs().get(j).getVid();
				String title = program.getProgram_inputs().get(j).getTitle();
				String data_Type = program.getProgram_inputs().get(j)
						.getData_Type();
				String data_Value = program.getProgram_inputs().get(j)
						.getData_Value();
				inputObject.put("vid", vid);
				inputObject.put("title", title);
				inputObject.put("data_type", data_Type);
				inputObject.put("data_value", data_Value);
				inputs.add(inputObject);
			}

			// input values
			JSONArray iValues = new JSONArray();
			List<String> inputValues = program.getInputValues();
			for (int j = 0; j < inputValues.size(); j++) {
				iValues.add(inputValues.get(j));
			}

			// text list
			JSONArray tList = new JSONArray();
			List<String> textList = program.getTextList();
			for (int j = 0; j < textList.size(); j++) {
				tList.add(textList.get(j));
			}

			// cim = composite input map will be written as 2 arrays the will be
			// joined to make a map while reading the json file
			JSONArray ciminputNames = new JSONArray();
			JSONArray ciminputvalues = new JSONArray();
			Map<String, String> composite_InputsMap = program
					.getComposite_InputsMap();
			for (int j = 0; j < composite_InputsMap.keySet().size(); j++) {
				ciminputNames.add(composite_InputsMap.keySet().toArray()[j]);
			}
			for (int j = 0; j < composite_InputsMap.values().size(); j++) {
				ciminputvalues.add(composite_InputsMap.values().toArray()[j]);
			}

			// cm = connection map .. like composite map this map will also be
			// written as two arrays and will be joined when read the json file
			Map<String, String> connectionsMap = program.getConnectionsMap();
			JSONArray cmOutputName = new JSONArray();
			JSONArray cmInputNames = new JSONArray();
			for (int j = 0; j < connectionsMap.size(); j++) {
				cmOutputName.add(connectionsMap.keySet().toArray()[j]);
			}
			for (int j = 0; j < connectionsMap.size(); j++) {
				cmInputNames.add(connectionsMap.values().toArray()[j]);
			}

			programObject.put("compositeID", compositeID);
			programObject.put("type", type);
			programObject.put("methodName", methodName);
			programObject.put("numberOfInputs", numberOfInputs);
			programObject.put("numberOfOutputs", numberOfOutputs);
			programObject.put("x", x);
			programObject.put("y", y);
			programObject.put("width", width);
			programObject.put("height", height);
			programObject.put("inputs", inputs);
			programObject.put("inputValues", iValues);
			programObject.put("textList", tList);
			programObject.put("composite_Inputs", ciminputNames);
			programObject.put("composite_Values", ciminputvalues);
			programObject.put("connectionsMapOut", cmOutputName);

			programObject.put("connectionsMapIn", cmInputNames);

			programsList.add(programObject);
		}
		// ---------------------------------------------------------------------
		// methodId

		JSONObject methodId = new JSONObject();
		// Variables for composite ID's
		int method1_IDCounter = dataobj.getMethod1_IDCounter();
		methodId.put("MethodID", method1_IDCounter);
		// ---------------------------------------------------------------------
		// // connector list
		// JSONArray connectors = new JSONArray();
		// List<Connectors> connectorList = instance.getConnectorList();
		// for (int j = 0; j < connectorList.size(); j++){
		// JSONObject newConnector = new JSONObject();
		// String starting = connectorList.get(j).getStartingCompositeID();
		// String ending = connectorList.get(j).getEndingCompositeID();
		// newConnector.put("Starting", starting);
		// newConnector.put("Ending", ending);
		// connectors.add(newConnector);
		// }
		// //---------------------------------------------------------------------
		// connector detectable
		JSONArray cdList = new JSONArray();
		List<ConnectorDetectable> connectorDetectableList = dataobj
				.getConnectorDetectableList();
		for (int i = 0; i < connectorDetectableList.size(); i++) {
			JSONObject newCD = new JSONObject();
			ConnectorDetectable cd = connectorDetectableList.get(i);
			String starting = cd.getConnector().getStartingCompositeID();
			String ending = cd.getConnector().getEndingCompositeID();
			List<String> loInputNames = cd.getLoInputNames();
			JSONArray leftOverInputs = new JSONArray();
			for (int k = 0; k < loInputNames.size(); k++) {
				leftOverInputs.add(loInputNames.get(k));
			}
			newCD.put("starting", starting);
			newCD.put("ending", ending);
			newCD.put("LeftOverInputs", leftOverInputs);
			cdList.add(newCD);
		}
		// ----------------------------------------------------------------------
		// inputs hooked
		List<String> inputsHooked = dataobj.getInputsHooked();
		JSONArray inputs_hooked = new JSONArray();
		for (int j = 0; j < inputsHooked.size(); j++) {
			inputs_hooked.add(inputsHooked.get(j));
		}
		// ----------------------------------------------------------------------

		obj.put("programs", programsList);
		obj.put("methodID", methodId);
		obj.put("CDList", cdList);
		obj.put("inputsHooked", inputs_hooked);

		try {

			String path = filepath;
			FileWriter file = new FileWriter(path);
			file.write(obj.toJSONString());
			file.flush();
			file.close();

			/*
			 * uncomment to create .wf files
			 */
			// wf_fileCreator(path);

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.print(obj);

	}

	/**
	 * Creates a .wf file in the same folder as the .json file
	 * 
	 * @param path
	 */
	private void wf_fileCreator(String filename) {

		try {

			String key = StringFormatter(filename);

			File file = new File(key + ".wf");

			System.out.println("file is " + file);

			if (file.createNewFile()) {
				System.out.println("File is created!");
			} else {
				System.out.println("File already exists.");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// CreateWFfile cwff = new CreateWFfile();
		// cwff.createfile(filename);

	}

	/**
	 * Removes the .json extension from the file name
	 * 
	 * @param path
	 *            name of the file
	 * @return the file name with out any extensions
	 */
	private String StringFormatter(String path) {

		StringTokenizer st = new StringTokenizer(path, ".");
		String key = st.nextToken();
		System.out.println(key);
		return key;
	}

	@Override
	public IRefreshMonitor installMonitor(IResource resource,
			IRefreshResult result) {
		// TODO Auto-generated method stub
		return null;
	}
}