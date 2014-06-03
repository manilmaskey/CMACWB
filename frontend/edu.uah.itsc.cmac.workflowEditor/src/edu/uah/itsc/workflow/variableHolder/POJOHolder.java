package edu.uah.itsc.workflow.variableHolder;

import java.util.HashMap;
import java.util.Map;

public class POJOHolder {

	private static POJOHolder instance = null;

	// Protected constructor
	protected POJOHolder() {
		// Exists only to defeat instantiation.
	}

	public static POJOHolder getInstance() {
		if (instance == null) {
			instance = new POJOHolder();
		}
		return instance;
	}

	Map<String, CopyOfVariablePoJo> editorsmap = new HashMap<String, CopyOfVariablePoJo>();

	public Map<String, CopyOfVariablePoJo> getEditorsmap() {
		return editorsmap;
	}

	public void setEditorsmap(Map<String, CopyOfVariablePoJo> editorsmap) {
		this.editorsmap = editorsmap;
	}

}
