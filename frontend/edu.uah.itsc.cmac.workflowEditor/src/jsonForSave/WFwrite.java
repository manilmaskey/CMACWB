package jsonForSave;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.PlatformUI;

import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.variableHolder.CopyOfVariablePoJo;
import edu.uah.itsc.workflow.variableHolder.POJOHolder;

public class WFwrite {

	public List<Connectors> writecontents (){
		CopyOfVariablePoJo ins = POJOHolder.getInstance().getEditorsmap().get(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle());
		int a = ins.getCompositeList().size();
		int b = ins.getConnectorList().size();
		System.out.println("prog " + a);
		System.out.println("conne " + b);
		
		List<Connectors> newconnectorslist = new ArrayList<Connectors>();
		
		for (int i = 0; i < ins.getConnectorList().size(); i++){
			Connectors connector = ins.getConnectorList().get(i);
			if (ins.getCompositeList().contains(connector.getStartingComposite())){
				if (ins.getCompositeList().contains(connector.getEndingComposite())){
					newconnectorslist.add(connector);
				}
			}
		}
		return newconnectorslist;
	}
}
