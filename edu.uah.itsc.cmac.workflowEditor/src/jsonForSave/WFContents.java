package jsonForSave;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.uah.programview.programObjects.IOPOJO;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

public class WFContents {

	public String getcontent() {

		String content = "";

		String title = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor().getTitle();

		POJOHolder instance = POJOHolder.getInstance();
		CopyOfVariablePoJo object = instance.getEditorsmap().get(title);

		List<CompositeWrapper> programslist = object.getCompositeList();
		
		List<Connectors> connectorslist = new ArrayList<Connectors>();
		
		for (int i = 0; i < object.getConnectorDetectableList().size(); i++){
			connectorslist.add(object.getConnectorDetectableList().get(i).getConnector());
		}
		
		

//		for (int i = 0; i < programslist.size(); i++) {
//			CompositeWrapper program = programslist.get(i);
//			content = content + "Title:\t" + program.getMethodName() + "\n";
//			content = content + "Number of Inputs:\t" + program.getNumberOfInputs() + "\n";
//			content = content + "Number of Outputs:\t" + program.getNumberOfOutputs() + "\n";

//			List<IOPOJO> programinputs = program.getProgram_inputs();

//			for (int j = 0; j < programinputs.size(); j++) {
//				IOPOJO parameter = programinputs.get(j);
//				content = content + "Parameter Title:\t" + parameter.getTitle() + "\n";
//				content = content + "Parameter Type:\t" + parameter.getType() + "\n";
//				content = content + "Parameter Status:\t" + parameter.getStatus() + "\n";
//				content = content + "Parameter Option:\t" + parameter.getOption() + "\n";
//				content = content + "Parameter Format:\t" + parameter.getFormat() + "\n";
//			}
			
//			List<IOPOJO> programoutputs = program.getProgram_outputs();
			
//			for (int j = 0; j < programoutputs.size(); j++){
//				IOPOJO parameter = programoutputs.get(j);
//				content = content + "Parameter Title:\t" + parameter.getTitle() + "\n";
//				content = content + "Parameter Type:\t" + parameter.getType() + "\n";
//				content = content + "Parameter Status:\t" + parameter.getStatus() + "\n";
//				content = content + "Parameter Option:\t" + parameter.getOption() + "\n";
//				content = content + "Parameter Format:\t" + parameter.getFormat() + "\n";
//			}
//			
//			content = content + "\n";

//		}
		
		// get programs from the connectors list 
		List<Connectors> startingconnectors = new ArrayList<Connectors>();
		
		// Take a connector and check if its starting id matches any other connectors ending id
		for (int i = 0; i < connectorslist.size(); i++){
			Connectors connector = connectorslist.get(i);
			
			for (int j = 0; j < connectorslist.size(); j++){
				Connectors testconnectors = connectorslist.get(j);
				
				System.out.println("st id " + connector.getStartingCompositeID());
				System.out.println("ed id " + testconnectors.getEndingCompositeID());
				
				if ((connector.getStartingCompositeID()).equals(testconnectors.getEndingCompositeID())){
					connector = testconnectors;
					j = -1;
				}
			}
			// a starting connector is found
			if (!(startingconnectors.contains(connector))){
			startingconnectors.add(connector);}
		}
		
		// Check the size of the starting connector list. If greater than one then incorrect workflow
		System.out.println("the size of the starting connectors list is " + startingconnectors.size());
		
		// if only one program is there 
				List<CompositeWrapper> startingprograms = new ArrayList<CompositeWrapper>();
				if (programslist.size() == 1){
					startingprograms.add(programslist.get(0));
				}
		
//		if (startingconnectors.size() == 1){
		if (programslist.size() > 0){
			// only one starting connector. Therefore correct workflow
			for (int i = 0; i < startingconnectors.size(); i++){
				System.out.println("starting connector is " + startingconnectors.get(i).getStartingComposite().getMethodName());
				
				content = createContents (startingconnectors, connectorslist, startingprograms);
			}
			for (int i = 0; i < startingprograms.size(); i++){
				content = createContents (startingconnectors, connectorslist, startingprograms);
			}
		}else{
			// incorrect workflow
			System.out.println("test1");
		}
		
		
		
		
//		// check if any programs are left
//		for (int i = 0; i < programslist.size(); i++){
//			boolean found = false;
//			CompositeWrapper program = programslist.get(i);
//			for (int j = 0; j < connectorslist.size(); j++){
//				if ((program.getCompositeID()).equals(connectorslist.get(j).getStartingCompositeID())){
//					// if found match then dont do anything 
//					System.out.println("match found");
//					found = true;
//				}else if ((program.getCompositeID()).equals(connectorslist.get(j).getEndingCompositeID())){
//					// if found match then dont do anything 
//					System.out.println("match found");
//					found = true;
//				}
//			}
//			if (found == false){
//				startingprograms.add(program);
//			}
//		}
//		
//		for (int i = 0; i < startingprograms.size(); i++){
//			System.out.println("starting programs are " + startingprograms.get(i).getMethodName());
//		}
		
		
		

		return content;
	}

	private String createContents(List<Connectors> startingconnectors, List<Connectors> connectorslist, List<CompositeWrapper> startingprograms) {
		
		ArrayList<ArrayList<CompositeWrapper>> programsets = new ArrayList<ArrayList<CompositeWrapper>>();
		
		if (startingprograms.size() != 0){
			ArrayList<CompositeWrapper> program = new ArrayList<CompositeWrapper>();
			program.add(startingprograms.get(0));
			programsets.add(program);
		}
		
		// first grab the connector list 
		for (int i = 0; i < startingconnectors.size(); i++){
			Connectors connector = startingconnectors.get(i);
			System.out.println();
			ArrayList<CompositeWrapper> set = new ArrayList<CompositeWrapper>();
			
			set.add(connector.getStartingComposite());
			set.add(connector.getEndingComposite());
			
			// Check the connector list 
			WFwrite wfw = new WFwrite();
			List<Connectors> newConnectorsList = wfw.writecontents();
			
			Connectors cn = connector;
			for (int j = 0; j < newConnectorsList.size(); j++){
				Connectors tempconnector = newConnectorsList.get(j);
				if ((cn.getEndingCompositeID()).equals(tempconnector.getStartingCompositeID())){
					// next in the sequence found 
//					set.add(tempconnector.getStartingComposite());
					set.add(tempconnector.getEndingComposite());
					cn = tempconnector;
					j = 0;
				}
			}
			
			programsets.add(set);
		}
		
//		for (int i = 0; i < startingprograms.size(); i++){
//			ArrayList<CompositeWrapper> set = new ArrayList<CompositeWrapper>();
//			set.add(startingprograms.get(i));
//			programsets.add(set);
//		}
		
		// Check the number of program sets. If more than one then in correct work flow
		System.out.println("program sets size " + programsets.size());
		String content = "";
//		if (programsets.size() == 1){
			// checking the number of programs in a set (for testing)
			
			for (int i = 0 ; i < programsets.size(); i++){
				System.out.println("program set " + i + "size = " + programsets.get(i).size());
			}
		
			content = writedata(programsets);
		
//		}else{
//			// more than one program set. Therefore incorrect workflow
//			System.out.println("test 2");
//		}
		
		
		
		
		return content;
	}

	private String writedata(ArrayList<ArrayList<CompositeWrapper>> programsets) {
		
		String content = "";
		for (int i = 0; i < programsets.size(); i ++){
			ArrayList<CompositeWrapper> programs = programsets.get(i);
			
//			content = content + "---------------Begin---------------" + "\n";
//			
//			for (int j = 0; j < programs.size(); j ++){
//				CompositeWrapper program = programs.get(j);
//				
//				content = content + "\nTitle:\t" + program.getMethodName() + "\n";
//				content = content + "Number of Inputs:\t" + program.getNumberOfInputs() + "\n";
//				content = content + "Number of Outputs:\t" + program.getNumberOfOutputs() + "\n";
//
//				List<IOPOJO> programinputs = program.getProgram_inputs();
//
//				for (int k = 0; k < programinputs.size(); k++) {
//					IOPOJO parameter = programinputs.get(k);
//					content = content + "Parameter Title:\t" + parameter.getTitle() + "\n";
//					content = content + "Parameter Type:\t" + parameter.getType() + "\n";
//					content = content + "Parameter Status:\t" + parameter.getStatus() + "\n";
//					content = content + "Parameter Option:\t" + parameter.getOption() + "\n";
//					content = content + "Parameter Format:\t" + parameter.getFormat() + "\n";
//				}
//				
//				List<IOPOJO> programoutputs = program.getProgram_outputs();
//				
//				for (int k = 0; k < programoutputs.size(); k++){
//					IOPOJO parameter = programoutputs.get(k);
//					content = content + "Parameter Title:\t" + parameter.getTitle() + "\n";
//					content = content + "Parameter Type:\t" + parameter.getType() + "\n";
//					content = content + "Parameter Status:\t" + parameter.getStatus() + "\n";
//					content = content + "Parameter Option:\t" + parameter.getOption() + "\n";
//					content = content + "Parameter Format:\t" + parameter.getFormat() + "\n";
//				}
//				
//			}
//			
//			content = content +"---------------End---------------" + "\n";
			
			for (int j = 0; j < programs.size(); j++){
				CompositeWrapper program = programs.get(j);
				
				content = content + program.getMethodName();
				
				for (int k = 0; k < program.getProgram_inputs().size(); k++){
					IOPOJO iop = program.getProgram_inputs().get(k);
					System.out.println(iop.getTitle() + " " + iop.getOption() + " " + iop.getData_Value());
					String value = iop.getData_Value();
					System.out.println("value = " + value);
					if ((value == (null)) || iop.getData_Value().equals("")){
						content = content + " " + iop.getOption() + " " + "";
					}else{
						content = content + " " + iop.getOption() + " " + iop.getData_Value();
					}
				}
				
				content = content + "\n";
			}
			
			
			
			
			
		}
		
		
		
		return content;
	}

}
