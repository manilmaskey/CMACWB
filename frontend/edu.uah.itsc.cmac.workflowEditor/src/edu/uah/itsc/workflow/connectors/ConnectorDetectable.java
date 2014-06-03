package edu.uah.itsc.workflow.connectors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

/**
 * This class gives a detectable on the connector A connector detectable will
 * hold an object which contains the data regarding the starting and ending
 * composites
 * 
 * @author Rohith Samudrala
 * 
 */
public class ConnectorDetectable extends Composite {

	public ConnectorDetectable(Composite parent, int style) {
		super(parent, style);
	}

	Connectors connector;

	List<String> loInputNames = new ArrayList<String>();

	public Connectors getConnector() {
		return connector;
	}

	public void setConnector(Connectors connector) {
		this.connector = connector;
	}

	public List<String> getLoInputNames() {
		return loInputNames;
	}

	public void setLoInputNames(List<String> loInputNames) {
		this.loInputNames = loInputNames;
	}

}
