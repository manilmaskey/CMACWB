package edu.uah.itsc.cmac.util;

public class Program {

	private String	name;
	private String	options;

	public Program(String name, String options) {
		super();
		this.name = name;
		this.options = options;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

}
