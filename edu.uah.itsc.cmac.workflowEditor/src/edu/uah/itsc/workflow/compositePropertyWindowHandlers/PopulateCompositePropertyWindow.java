package edu.uah.itsc.workflow.compositePropertyWindowHandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import edu.uah.itsc.workflow.wrapperClasses.CompositeWrapper;

/**
 * 
 * @author Rohith Samudrala
 * 
 */
public class PopulateCompositePropertyWindow {

	public void populateWindow(final CompositeWrapper method,
			final Shell shell, Composite composite) throws Exception {

		int y = -17;
		int seperator = 22;
		// list will hold the input names that are not hooked to any output in
		// the connector window
		List<String> inputsNotHooked = new ArrayList<>();
		// check if the input has a value assigned to it in the connector window
		// if not value is assigned to it then add the input name to the list.
		// if there is a value assigned then check if the value is <Select> if
		// the value is select then add the input name to the list as well
		for (int i = 0; i < method.getNumberOfInputs(); i++) {
			// if the input dose not have a value assigned to it in the
			// connections map then add that input name to the list
			if (!(method.getConnectionsMap().containsValue(method
					.getProgram_inputs().get(i).getTitle()))) {
				inputsNotHooked.add(method.getProgram_inputs().get(i)
						.getTitle());
			}
		}

		// will have the programs input name and value
		Map<String, String> compInputValues = method.getComposite_InputsMap();
		final Map<Label, Text> newCompInputValues = new HashMap<Label, Text>();
		//
		for (int i = 0; i < method.getNumberOfInputs(); i++) {
			if (inputsNotHooked.contains(method.getProgram_inputs().get(i)
					.getTitle())) {
				Label lblNewLabel = new Label(composite, SWT.NONE);
				y = (y + seperator);
				lblNewLabel.setBounds(10, y, 200, 19); //104
				lblNewLabel.setText(method.getProgram_inputs().get(i)
						.getTitle());

				Text text = new Text(composite, SWT.BORDER);
				text.setBounds(220, y, 216, 19); //312
				// check to see if the compositeInputValues list has a key with
				// that input (it will have the key if the input was assigned a
				// value
				// in the composite window earlier)
				if (compInputValues.containsKey(method.getProgram_inputs()
						.get(i).getTitle())) {
					text.setText(compInputValues.get(method.getProgram_inputs()
							.get(i).getTitle()));
				} else {
					text.setText("");
				}
				newCompInputValues.put(lblNewLabel, text);
			}
		}

		Button btnOK = new Button(composite, SWT.NONE);
		y = y + seperator;
		btnOK.setBounds(150, y, 68, 23);
		btnOK.setToolTipText("This will save any data entered");
		btnOK.setText("OK");

		Button btnCANCEL = new Button(composite, SWT.NONE);
		btnCANCEL.setBounds(230, y, 68, 23);
		btnCANCEL.setText("CANCEL");

		Button btnDelete = new Button(composite, SWT.NONE);
		btnDelete.setToolTipText("This button will delete the composite. "
				+ "If the composite is a part of any connection it"
				+ " will also delete the connection");
		btnDelete.setBounds(341, y, 68, 23);
		btnDelete.setText("Delete");

		btnOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				Map<String, String> newMap = new HashMap<String, String>();
				for (int i = 0; i < newCompInputValues.size(); i++) {
					Label newlbl = (Label) newCompInputValues.keySet()
							.toArray()[i];
					Text newtxt = (Text) newCompInputValues.values().toArray()[i];
					newMap.put(newlbl.getText(), newtxt.getText());
				}
				method.setComposite_InputsMap(newMap);
				OKButtonHandler okHandlerObj = new OKButtonHandler();
				okHandlerObj.okButtonHandler(method, newMap);
				// method.setDescription(text_2.getText());
				shell.close();
			}
		});

		btnCANCEL.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});

		btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DeleteButtonHandler obj = new DeleteButtonHandler();
				obj.deletetest(method, shell);

			}
		});
		
	}

}
