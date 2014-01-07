package edu.uah.itsc.cmac.wsdl2Drupal.FileParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.wsdl.Definition;
import javax.wsdl.Operation;
import javax.wsdl.Part;
import javax.wsdl.PortType;
import javax.wsdl.WSDLException;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import edu.uah.itsc.cmac.portal.Parameter;
import edu.uah.itsc.programformview.views.ProgramCreator;

/**
 * 
 * @author lsamudrala
 * 
 */
public class Parser {

	// initializing required parameters for pushing program to drupal
	String title = "";
	String description = "";
	String contactInfo = "";
	String docURL = "";
	String path = "";
	String version = "";

	public void parseFile() throws WSDLException {
		
		/**
		 * http://ws3.itsc.uah.edu/mws/glider-wsdls/ImageProcessing.wsdl
		 * http://ws3.itsc.uah.edu/mws/glider-wsdls/Optimization.wsdl
		 * http://ws3.itsc.uah.edu/mws/glider-wsdls/PatternRecognition.wsdl
		 * http://ws3.itsc.uah.edu/mws/glider-wsdls/Texture.wsdl
		 * http://ws3.itsc.uah.edu/mws/glider-wsdls/Utility.wsdl
		 * http://ws3.itsc.uah.edu/mws/glider-wsdls/gsf_to_glider.wsdl
		 */

		WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
		Definition def = reader
				.readWSDL("http://ws3.itsc.uah.edu/mws/glider-wsdls/gsf_to_glider.wsdl");

		// get the port
		for (Object o : def.getAllPortTypes().values()) {
			PortType pt = (PortType) o;
			// get the operations from the port
			for (int i = 0; i < pt.getOperations().size(); i++) {
				// from operations get the title and description
				Operation op = (Operation) pt.getOperations().get(i);
				title = op.getName();
				description = op.getDocumentationElement().getTextContent();

				// lists for holding the input and the output parameters
				ArrayList<Parameter> inputparameters = new ArrayList<Parameter>();
				ArrayList<Parameter> outputparameters = new ArrayList<Parameter>();

				// in the input message get all parts
				for (int j = 0; j < op.getInput().getMessage().getParts()
						.size(); j++) {

					// create a new instance of parameter class
					Parameter parameter = new Parameter();
					Part part = (Part) op.getInput().getMessage().getParts()
							.values().toArray()[j];

					parameter.setTitle(part.getName());

					String typeQName = ((Object) part.getTypeName()).toString();
					parameter.setType(StringFormatter(typeQName));

					// parameter.setType(part.getTypeName().getLocalPart());

					// key, value pair of the extension attributes
					@SuppressWarnings("unchecked")
					Map<QName, QName> map = part.getExtensionAttributes();

					Map<String, String> map_String = getStringMap(map);

					parameter = populateParameter(map_String, parameter);

					inputparameters.add(parameter);

				}

				for (int j = 0; j < op.getOutput().getMessage().getParts()
						.size(); j++) {

					// create a new instance of parameter class
					Parameter parameter = new Parameter();

					Part part = (Part) op.getOutput().getMessage().getParts()
							.values().toArray()[j];

					parameter.setTitle(part.getName());

					@SuppressWarnings("unchecked")
					Map<QName, QName> map = part.getExtensionAttributes();

					Map<String, String> Out_String_Map = getStringMap(map);

					parameter = populateParameter(Out_String_Map, parameter);

					// for (int k = 0; k < part.getExtensionAttributes().size();
					// k++) {
					//
					// QName key = (QName) map.keySet().toArray()[k];
					// QName value = (QName) map.values().toArray()[k];
					//
					// if (key.getLocalPart().equals("description")) {
					// parameter.setBody(value.getLocalPart());
					// }
					// if (key.getLocalPart().equals("status")) {
					// parameter.setStatus(value.getLocalPart());
					// }
					// if (key.getLocalPart().equals("fileformat")) {
					// parameter.setFormat(value.getLocalPart());
					// }
					// if (key.getLocalPart().equals("commandline")) {
					// parameter.setOption(value.getLocalPart());
					// }
					// }
					outputparameters.add(parameter);
				}
				// one operation is finished, push to durpal
				Test test = new Test();
				test.print(title, description, contactInfo, docURL, path,
						version, inputparameters, outputparameters);

				ProgramCreator.createProgram(inputparameters, outputparameters,
						title, description, contactInfo, docURL, path, version);
			}
		}
	}

	/**
	 * Populates the parameter object
	 * 
	 * @param map_String
	 *            map of local parts of the extended attributes
	 * @param parameter
	 *            object of the parameter class
	 * @return returns the object of the parameter class
	 */
	private Parameter populateParameter(Map<String, String> map_String,
			Parameter parameter) {

		Iterator<String> key_iterator = map_String.keySet().iterator();
		while (key_iterator.hasNext()) {
			String key = (String) key_iterator.next();

			if (key.equals("description")) {
				parameter.setBody(map_String.get(key));
			}
			if (key.equals("status")) {
				parameter.setStatus(map_String.get(key));
			}
			if (key.equals("fileformat")) {
				parameter.setFormat(map_String.get(key));
			}
			if (key.equals("commandline")) {
				parameter.setOption(map_String.get(key));
			}
			if (key.equals("default")) {
				parameter.setDefault_value(map_String.get(key));
			}
		}
		return parameter;

	}

	/**
	 * returns map of local parts of the QNames
	 * 
	 * @param map
	 *            of QNames, QName
	 * @return map of String, String (QName.getLocalPart, QName.getLocalPart)
	 */
	private Map<String, String> getStringMap(Map<QName, QName> map) {
		Map<String, String> map_String = new HashMap<String, String>();
		ArrayList<String> key_array = new ArrayList<String>();
		ArrayList<String> value_array = new ArrayList<String>();

		// get key local part
		Iterator<QName> key_iterator = map.keySet().iterator();

		while (key_iterator.hasNext()) {

			String m_Key = StringFormatter(((Object) key_iterator.next())
					.toString());

			key_array.add(m_Key);

		}

		// get value local part
		Iterator<QName> value_iterator = map.values().iterator();

		while (value_iterator.hasNext()) {

			String m_Value = StringFormatter(((Object) value_iterator.next())
					.toString());
			value_array.add(m_Value);

		}

		for (int m = 0; m < key_array.size(); m++) {

			map_String.put(key_array.get(m), value_array.get(m));
		}
		return map_String;
	}

	/**
	 * String formatter
	 * 
	 * @param m_String
	 * @return
	 */
	private String StringFormatter(String m_String) {

		StringTokenizer st = new StringTokenizer(m_String, "}");
		String key = st.nextToken();
		String val;
		if (st.hasMoreTokens()) {
			val = st.nextToken();
		} else {
			val = key;
		}
		System.out.println(val);
		return val;
	}

}
