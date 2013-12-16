/**
 * 
 */
package edu.uah.itsc.programformview.views;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.uah.itsc.aws.User;
import edu.uah.itsc.cmac.portal.Parameter;
import edu.uah.itsc.cmac.portal.PortalPost;
import edu.uah.itsc.cmac.portal.PortalUtilities;
import edu.uah.itsc.cmac.portal.Program;

/**
 * @author sshrestha
 *
 */
public class ProgramCreator {
	public static HttpResponse createProgram(ArrayList<Parameter> inputParameters,
			ArrayList<Parameter> outputParameters, String title,
			String description, String contactInfo, String docURL, String path,
			String version) {
		HttpResponse response = null;
		PortalPost portalPost = new PortalPost();
		ArrayList<Parameter> allParameters = new ArrayList<Parameter>();
		allParameters.addAll(inputParameters);
		allParameters.addAll(outputParameters);

		for (Parameter parameter : allParameters) {

			try {
				response = portalPost.post(PortalUtilities.getNodeRestPoint(),
						parameter.getJSON());
				if (response.getStatusLine().getStatusCode() != 200) {
					// TODO: Check the error status from Drupal
					return null;
				} else {
					byte[] byteResponse = new byte[(int) response.getEntity()
							.getContentLength()];
					int length = response.getEntity().getContent()
							.read(byteResponse);
					String stringResponse = new String(byteResponse);
					System.out.println(length + "\n" + stringResponse);
					JSONParser jsonParser = new JSONParser();
					JSONObject jsonResponse = (JSONObject) jsonParser
							.parse(stringResponse);
					String nidParameter = (String) jsonResponse.get("nid");
					System.out.println(nidParameter);
					parameter.setNid(nidParameter);
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Program program = new Program();
		program.setContactInfo(contactInfo);
		program.setCreator(User.username);
		program.setDescription(description);
		program.setDocURL(docURL);
		program.setPath(path);
		program.setSubmittor(User.username);
		program.setTitle(title);
		program.setVersion(version);
		program.setInputParameters(inputParameters);
		program.setOutputParameters(outputParameters);
		response = portalPost.post(
				PortalUtilities.getNodeRestPoint(),
				program.getQueryString());

		return response;

	}
}
