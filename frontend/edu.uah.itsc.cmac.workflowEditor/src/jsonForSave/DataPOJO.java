package jsonForSave;

import java.util.ArrayList;
import java.util.List;

public class DataPOJO {

	private static DataPOJO instance = null;

	// Protected constructor
	protected DataPOJO() {
		// Exists only to defeat instantiation.
	}

	public static DataPOJO getInstance() {
		if (instance == null) {
			instance = new DataPOJO();
		}
		return instance;
	}

	List<DataObject> programs_data = new ArrayList<DataObject>();

	public List<DataObject> getPrograms_data() {
		return programs_data;
	}

	public void setPrograms_data(List<DataObject> programs_data) {
		this.programs_data = programs_data;
	}

}
