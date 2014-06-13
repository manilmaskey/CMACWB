/**
 * 
 */
package edu.uah.itsc.cmac.util;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author sshrestha
 * 
 */
public class PropertyUtility {
	private Properties	properties;
	private String		propertyFileName;

	/**
	 * 
	 * @param propertyFileName
	 *            - It must be absolute path to the property file
	 */

	public PropertyUtility(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}

	public String getValue(String key) {
		if (properties != null && properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(new FileReader(propertyFileName));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties.getProperty(key);
	}

	public void setValue(String key, String value) {
		OutputStream output = null;
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(new FileReader(propertyFileName));
				properties.setProperty(key, value);
				output = new FileOutputStream(propertyFileName);
				properties.store(output, null);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					output.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
