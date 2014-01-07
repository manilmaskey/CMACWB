package edu.uah.itsc.cmac.programview.views;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import edu.uah.itsc.cmac.portal.Program;
import edu.uah.itsc.cmac.programview.JSONParser.ReadPrograms;

/**
 * 
 * @author lsamudrala
 * 
 */
public class ReadProgramsThread extends Thread {

	// Array list of program url's
	ArrayList<Program> programsList;

	// Constructor
	public ReadProgramsThread(ArrayList<Program> programsList) {
		super();
		this.programsList = programsList;
	}

	// Download the programs
	public void run() {
		ReadPrograms program_reader = new ReadPrograms();
		try {
			program_reader.read_Programs(programsList);
			
			
			Display display = new Display();
			Shell shell = new Shell(display);

			MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION
					| SWT.OK);
			messageBox.setText("Programs ready !");
			messageBox.setMessage("Programs are downloaded ..");
			messageBox.open();
			System.out.println("Programs Read ...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
