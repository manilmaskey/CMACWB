/**
 * 
 */
package edu.uah.itsc.cmac.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * @author sshrestha
 * 
 */
public class FileUtility {
	public static String readTextFile(String fileName) {
		StringBuffer contents = null;
		String line = null;
		FileReader fileReader = null;
		BufferedReader reader = null;
		try {
			fileReader = new FileReader(fileName);
			reader = new BufferedReader(fileReader);
			contents = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				contents.append(line);
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IO Error");
			e.printStackTrace();
		}
		finally {
			try {
				if (fileReader != null)
					fileReader.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return contents.toString();
	}

	public static void writeTextFile(String fileName, String fileContent) {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(fileName, "UTF-8");
			writer.print(fileContent);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		finally {
			if (writer != null)
				writer.close();
		}
	}
}
