package edu.uah.itsc.cmac.wsdl2Drupal.FileParser;

import java.util.List;

import edu.uah.itsc.cmac.portal.Parameter;

public class Test {

	public void print(String title, String description, String contactInfo,
			String docURL, String path, String version, List<Parameter> inputs,
			List<Parameter> outputs) {
		System.out.println("title " + title);
		System.out.println("description " + description);
		System.out.println("contactInfo " + contactInfo);
		System.out.println("docURL " + docURL);
		System.out.println("path " + path);
		System.out.println("version " + version);
		
		for (int i = 0; i < inputs.size(); i++){
			System.out.println("input title " + inputs.get(i).getTitle());
			System.out.println("type " + inputs.get(i).getType());
			System.out.println("option " + inputs.get(i).getOption());
			System.out.println("format " + inputs.get(i).getFormat());
			System.out.println("status " + inputs.get(i).getStatus());
			System.out.println("body " + inputs.get(i).getBody());
		}
		
		for (int i = 0; i < outputs.size(); i++){
			System.out.println("output title " + outputs.get(i).getTitle());
			System.out.println("type " + outputs.get(i).getType());
			System.out.println("option " + outputs.get(i).getOption());
			System.out.println("format " + outputs.get(i).getFormat());
			System.out.println("status " + outputs.get(i).getStatus());
			System.out.println("body " + outputs.get(i).getBody());
		}
	}
}
