package edu.uah.itsc.cmac.programview.JSONParser;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.uah.itsc.cmac.portal.Program;
import edu.uah.itsc.cmac.programview.programsHolder.ProgramsHolder;
import edu.uah.itsc.uah.programview.programObjects.IOPOJO;
import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;


/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class ReadPrograms {

	/**
	 * This method read all the programs from the JSON Array and creates objects
	 * of each program and stores all the program objects into a list in the
	 * VariablePOJO class
	 * 
	 * @param prog
	 *            JSONArray containing JSONObjects of all the programs
	 * @throws Exception
	 */
	public void read_Programs(List<Program> prog) throws Exception {

		List<ProgramPOJO> program_List = new ArrayList<>();
		for (int i = 0; i < prog.size(); i++) {
			// get the uri of the program
			String uri = prog.get(i).getUri();
			JSONObject program_Object = new JSONObject(JsonURLReader.readUrl(uri));
			// create a new program object
			ProgramPOJO program = new ProgramPOJO();
			
			// store required values from the JSONObject to program Object
//			program.setVid(program_Object.getInt("vid"));
			program.setTitle(program_Object.getString("title"));
			program.setUri(uri);

			int start = uri.indexOf(
					program_Object.get("nid").toString());
			String path = uri.substring(0, start);

			List<IOPOJO> input_Objects = new ArrayList<IOPOJO>();
			List<IOPOJO> output_Objects = new ArrayList<IOPOJO>();
			// read and extract data from field_input_file
			if (program_Object.get("field_input_file") instanceof JSONArray) {
				// input file field in empty
				program.setInput_Count(0);
			} else if (program_Object.get("field_input_file") instanceof JSONObject) {
				JSONObject input_Object = program_Object
						.getJSONObject("field_input_file");
				JSONArray und_Array = input_Object.getJSONArray("und");
				// set input_Count
				program.setInput_Count(und_Array.length());
				// list to store input objects

				ReadIOFile file_reader = new ReadIOFile();
				for (int j = 0; j < und_Array.length(); j++) {
					IOPOJO IOPOJO_Object = file_reader.read_IOFile(
							und_Array.getJSONObject(j), path);
					input_Objects.add(IOPOJO_Object);
				}

			}
			// read and extract data from field_output_file
			if (program_Object.get("field_output_file") instanceof JSONArray) {
				// output file field in empty
				program.setOutput_Count(0);
			} else if (program_Object.get("field_output_file") instanceof JSONObject) {
				JSONObject output_Object = program_Object
						.getJSONObject("field_output_file");
				JSONArray und_Array = output_Object.getJSONArray("und");
				// set output_Count
				program.setOutput_Count(und_Array.length());
				// list to store output objects
				ReadIOFile file_reader = new ReadIOFile();
				for (int j = 0; j < und_Array.length(); j++) {
					IOPOJO IOPOJO_Object = file_reader.read_IOFile(
							und_Array.getJSONObject(j), path);
					output_Objects.add(IOPOJO_Object);
				}

			}

			// save the list of inputs and outputs
			program.setInput_List(input_Objects);
			program.setOutput_List(output_Objects);
			program_List.add(program);
		}
		// set the program list into the singleton class
		ProgramsHolder instance = ProgramsHolder.getInstance();
		instance.setPrograms_list(program_List);
	}

}
