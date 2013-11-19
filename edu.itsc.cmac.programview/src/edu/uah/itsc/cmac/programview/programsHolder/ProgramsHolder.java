package edu.uah.itsc.cmac.programview.programsHolder;

import java.util.ArrayList;
import java.util.List;

import edu.uah.itsc.uah.programview.programObjects.ProgramPOJO;

/**
 * A Singleton class which holds the list of all Programs.
 * 
 * @author Rohith Samudrala
 * 
 */
public class ProgramsHolder {
	private static ProgramsHolder instance = null;

	// Protected constructor
	protected ProgramsHolder() {
		// Exists only to defeat instantiation.
	}

	public static ProgramsHolder getInstance() {
		if (instance == null) {
			instance = new ProgramsHolder();
		}
		return instance;
	}

	List<ProgramPOJO> programs_list = new ArrayList<>();

	public List<ProgramPOJO> getPrograms_list() {
		return programs_list;
	}

	public void setPrograms_list(List<ProgramPOJO> programs_list) {
		this.programs_list = programs_list;
	}

}
