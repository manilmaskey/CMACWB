package jsonForSave;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * This class is responsible for reading the JASON File and recreating the
 * PIWorkFlow Work Space
 * 
 * @author Rohith Samudrala
 * 
 */
public class JSONRead {

	boolean isFile;

	public boolean readJSONFile(String name) {

		JSONParser parser = new JSONParser();

		try {

			// Object obj = parser.parse(new FileReader(
			// "C:\\Users\\Rohith Samudrala\\workspace\\PIWorkFlow" + name
			// + ".json"));
			Object obj = parser.parse(new FileReader(name));

			// parent object which gets the entire file
			JSONObject fileObject = (JSONObject) obj;

			// instance of variable pojo
			VariablePoJo vp_instance = VariablePoJo.getInstance();
			DataPOJO dp_instance = DataPOJO.getInstance();

			// array of programs
			JSONArray programs = (JSONArray) fileObject.get("programs");

			// workspace for work flow
			CompositeWrapper childComposite_WorkSpace = vp_instance
					.getChildCreatorObject().getChildComposite_WorkSpace();

			for (int i = 0; i < programs.size(); i++) {
				// ith program object
				JSONObject progObject = (JSONObject) programs.get(i);

				DataObject composite = new DataObject();

				int x = Integer.parseInt(progObject.get("x").toString());
				int y = Integer.parseInt(progObject.get("y").toString());
				int width = Integer
						.parseInt(progObject.get("width").toString());
				int height = Integer.parseInt(progObject.get("height")
						.toString());

				composite.setX(x);
				composite.setY(y);
				composite.setWidth(width);
				composite.setHeight(height);

				composite.setMethodName((String) progObject.get("methodName"));

				composite.setNumberOfInputs(Integer.parseInt(progObject.get(
						"numberOfInputs").toString()));
				composite.setNumberOfOutputs(Integer.parseInt(progObject.get(
						"numberOfOutputs").toString()));
				composite.setType((String) progObject.get("type"));
				composite
						.setCompositeID((String) progObject.get("compositeID"));

				JSONArray inputs = (JSONArray) progObject.get("inputs");
				List<String> values = new ArrayList<>();
				for (int j = 0; j < inputs.size(); j++) {
					JSONObject input = (JSONObject) inputs.get(j);
					values.add((String) input.get("data_value"));
				}
				composite.setInputValues(values);

				JSONArray textList = (JSONArray) progObject.get("textList");
				List<String> textValues = new ArrayList<>();
				for (int j = 0; j < textList.size(); j++) {
					String txt = (String) textList.get(i);
					textValues.add(txt);
				}

				// connections map out
				JSONArray connectionsMO = (JSONArray) progObject
						.get("connectionsMapOut");
				List<String> connetionsMapOut = new ArrayList<>();
				for (int j = 0; j < connectionsMO.size(); j++) {
					String txt = (String) connectionsMO.get(j);
					connetionsMapOut.add(txt);
				}

				// connections map in
				JSONArray connectionsMI = (JSONArray) progObject
						.get("connectionsMapIn");
				List<String> connetionsMapIn = new ArrayList<>();
				for (int j = 0; j < connectionsMI.size(); j++) {
					String txt = (String) connectionsMI.get(j);
					connetionsMapIn.add(txt);
				}

				// join both connections map out and in lists to make the
				// connections map
				for (int j = 0; j < connetionsMapOut.size(); j++) {
					composite.getConnectionsMap().put(connetionsMapOut.get(j),
							connetionsMapIn.get(j));
				}

				System.out.println("connection "
						+ composite.getConnectionsMap().get("Test_ImageOut"));

				// composite input names (these are the input names which are
				// given a value in the composite window)
				JSONArray compositeIN = (JSONArray) progObject
						.get("composite_Inputs");
				List<String> composite_Inputs = new ArrayList<>();
				for (int j = 0; j < compositeIN.size(); j++) {
					String txt = (String) compositeIN.get(j);
					composite_Inputs.add(txt);
				}

				// composite input values
				JSONArray compositeVALUES = (JSONArray) progObject
						.get("composite_Values");
				List<String> composite_Values = new ArrayList<>();
				for (int j = 0; j < compositeVALUES.size(); j++) {
					String txt = (String) compositeVALUES.get(j);
					composite_Values.add(txt);
				}

				// join the composite input names and the composite values to
				// make the composite_InputMap
				for (int j = 0; j < composite_Inputs.size(); j++) {
					composite.getComposite_InputMap().put(
							composite_Inputs.get(j), composite_Values.get(j));
				}

				// after entering all the values into the composite just add the
				// composite into the composites list in the variable pojo
				dp_instance.getPrograms_data().add(composite);
			}
			// ---------------new composite list has all the programs

			JSONArray CDList = (JSONArray) fileObject.get("CDList");
			List<ConnectorDetectable> cd = new ArrayList<>();
			List<Connectors> connectors = new ArrayList<>();
			for (int i = 0; i < CDList.size(); i++) {
				Connectors connector = new Connectors();
				JSONObject cdObject = (JSONObject) CDList.get(i);
				List<String> loInputNames = new ArrayList<>();
				ConnectorDetectable newcd = new ConnectorDetectable(
						childComposite_WorkSpace, SWT.NONE);

				String starting = (String) cdObject.get("starting");
				String ending = (String) cdObject.get("ending");
				connector.setStartingCompositeID(starting);
				connector.setEndingCompositeID(ending);
				for (int j = 0; j < vp_instance.getCompositeList().size(); j++) {
					if (vp_instance.getCompositeList().get(j).getCompositeID()
							.equals(starting)) {
						connector.setStartingComposite(vp_instance
								.getCompositeList().get(j));
					} else if (vp_instance.getCompositeList().get(j)
							.getCompositeID().equals(ending)) {
						connector.setEndingComposite(vp_instance
								.getCompositeList().get(j));
					}
				}
				connectors.add(connector);
				// -------------connector is ready
				JSONArray loi = (JSONArray) cdObject.get("LeftOverInputs");
				for (int j = 0; j < loi.size(); j++) {
					loInputNames.add((String) loi.get(j));
				}
				// ------------- left over input names list is ready

				newcd.setLoInputNames(loInputNames);
				newcd.setConnector(connector);
				cd.add(newcd);
			}
			vp_instance.setConnectorDetectableList(cd);
			vp_instance.setConnectorList(connectors);

			// -----------the above code will populate both connector detectable
			// list as well as connectors list4

			JSONObject methodID = (JSONObject) fileObject.get("methodID");
			vp_instance.setMethod1_IDCounter(Integer.parseInt(methodID.get(
					"MethodID").toString()));

			List<String> inputsHooked = new ArrayList<>();
			JSONArray iHooked = (JSONArray) fileObject.get("inputsHooked");
			for (int i = 0; i < iHooked.size(); i++) {
				inputsHooked.add((String) iHooked.get(i));
			}
			vp_instance.setInputsHooked(inputsHooked);
			// input hooked list is ready

			isFile = true;
			return isFile;

		} catch (FileNotFoundException e) {
			isFile = false;
			return isFile;
		} catch (IOException e) {
			isFile = false;
			return isFile;
		} catch (ParseException e) {
			isFile = false;
			return isFile;
		}
	}
}