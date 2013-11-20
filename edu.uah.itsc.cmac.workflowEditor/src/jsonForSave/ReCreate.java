package jsonForSave;

import java.util.List;

import org.eclipse.swt.widgets.Label;

import edu.uah.itsc.workflow.actionHandler.ConnectorClickHandler;
import edu.uah.itsc.workflow.connectors.ConnectorDetectable;
import edu.uah.itsc.workflow.connectors.Connectors;
import edu.uah.itsc.workflow.programDropHandler.ProgramDropHandler;
import edu.uah.itsc.workflow.variableHolder.VariablePoJo;
import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

public class ReCreate {

	CompositeWrapper methodComposite;

	public void recreateWorkFlow() throws Exception {

		for (int i = 0; i < DataPOJO.getInstance().getPrograms_data().size(); i++) {

			DataObject dobj = DataPOJO.getInstance().getPrograms_data().get(i);

			// int widthLeft = VariablePoJo.getInstance().getDisplayX();// -
			// 1300;
			// int heightLeft = VariablePoJo.getInstance().getDisplayY();// -
			// 620;
			// int x = widthLeft + dobj.getX();
			// int y = heightLeft + dobj.getY();

			int x1 = (VariablePoJo.getInstance().getChildCreatorObject()
					.getChildComposite_WorkSpace().getBounds().width - 987);
			final int width_difference = (362 - x1);
			final int height_difference = 112;
			int x = width_difference + dobj.getX();
			int y = height_difference + dobj.getY();

			Object obj = "[" + dobj.getMethodName() + "]";

			ProgramDropHandler pdh = new ProgramDropHandler();
			pdh.handleDrop(x, y, obj);

			int a = VariablePoJo.getInstance().getMethod1_IDCounter();
			a = a--;
			VariablePoJo.getInstance().setMethod1_IDCounter(a);

			CompositeWrapper method = pdh.getMethod();
			method.setCompositeID(dobj.getCompositeID());

			method.setComposite_InputsMap(dobj.getComposite_InputMap());
			method.setInputValues(dobj.getInputValues());
			method.setConnectionsMap(dobj.getConnectionsMap());

			Label in = pdh.getIn();
			Label out = pdh.getOut();

			List<Connectors> connectors = VariablePoJo.getInstance()
					.getConnectorList();
			for (int j = 0; j < connectors.size(); j++) {
				Connectors connector = VariablePoJo.getInstance()
						.getConnectorList().get(j);
				if (connector.getStartingCompositeID().equals(
						method.getCompositeID())) {
					connector.setStartingComposite(method);
					connector.setSource(out);
				} else if (connector.getEndingCompositeID().equals(
						method.getCompositeID())) {
					connector.setEndingComposite(method);
					connector.setDestination(in);
				}
			}

			for (int j = 0; j < method.getProgram_inputs().size(); j++) {
				if (dobj.getInputValues().size() != 0) {
					method.getProgram_inputs().get(j)
							.setData_Value(dobj.getInputValues().get(j));
				}
			}

			int b = VariablePoJo.getInstance().getMethod1_IDCounter();
			b = b--;
			VariablePoJo.getInstance().setMethod1_IDCounter(a);

		}
		for (int i = 0; i < VariablePoJo.getInstance()
				.getConnectorDetectableList().size(); i++) {
			ConnectorDetectable cd = VariablePoJo.getInstance()
					.getConnectorDetectableList().get(i);
			ConnectorClickHandler handlerObject = new ConnectorClickHandler();
			handlerObject.addConnectorHandlers(cd);
		}

		for (int i = 0; i < VariablePoJo.getInstance().getConnectorList()
				.size(); i++) {
			Connectors connector = VariablePoJo.getInstance()
					.getConnectorList().get(i);
			connector.getEndingComposite().setConnectionsMap(
					connector.getStartingComposite().getConnectionsMap());
		}
	}

}
