/**
 * 
 */
package edu.uah.itsc.cmac.backend.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uah.itsc.cmac.backend.util.FileUtility;
import edu.uah.itsc.cmac.backend.util.GITUtility;
import edu.uah.itsc.cmac.model.ExecuteCommand;

/**
 * @author sshrestha
 * 
 */
@Path("/action")
public class ExecutionService {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response testGet() {
		return Response.status(200).entity("{\"result\":\"hello world\"}").build();
	}

	@POST
	@Path("/execute")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response execute(String jsonExecuteCommand) {
		ExecuteCommand execCommand = (ExecuteCommand) ExecuteCommand.toObject(jsonExecuteCommand);
		return execute(execCommand);
	}

	private Response execute(ExecuteCommand execCommand) {
		System.out.println("Preparing to execute the file here");

		String userName = execCommand.getUserName();
		String userEmail = execCommand.getUserEmail();
		String comment = execCommand.getComment();
		String repoName = execCommand.getRepoName();
		String bucketName = execCommand.getBucketName();
		String accessKey = execCommand.getAccessKey();
		String secretKey = execCommand.getSecretKey();
		String fileName = execCommand.getFileName();
		String repoOwner = execCommand.getRepoOwner();
		boolean isSharedRepo = execCommand.isSharedRepo();

		String repoRemotePath = "amazon-s3://.jgit@";
		String userDirPath = "/home/ec2-user/cmac_backend_environment/s3/";
		// String userDirPath = "/home/ubuntu/cmac_backend_environment/s3/";
		// String userDirPath = "C:/cmac_backend_environment/s3/";
		if (isSharedRepo) {
			userDirPath = userDirPath + "cmac-community/";
			repoRemotePath = repoRemotePath + "cmac-community/";
		}
		userDirPath = userDirPath + bucketName + "/" + userName;
		repoRemotePath = repoRemotePath + bucketName + "/";
		if (repoOwner != null && !repoOwner.isEmpty())
			repoRemotePath = repoRemotePath + repoOwner;
		else
			repoRemotePath = repoRemotePath + userName;

		// Steps
		// 0. Delete .jgit file if it exists and create it based upon new credentials
		String userHomeDirectory = System.getProperty("user.home");
		File jgitFile = new File(userHomeDirectory + "/.jgit");
		if (!jgitFile.exists()) {
			try {
				String fileContent = "accesskey: " + accessKey + "\n" + "secretkey: " + secretKey;
				jgitFile.createNewFile();
				FileUtility.writeTextFile(jgitFile.getAbsolutePath(), fileContent);
				System.out.println("Creating jgit file at JGIT path: " + jgitFile.getAbsolutePath()
					+ "\nfor user name: " + System.getProperty("user.name"));
			}
			catch (IOException e) {
				e.printStackTrace();
				return buildResponse(500, "IOException during execution of file " + fileName + ".\n" + e.getMessage(),
					execCommand.toJSON());
			}
		}

		// 1. Check if directory for this user exists. If not create one similar to the bucket structure
		File userDirectory = new File(userDirPath);
		if (!userDirectory.exists() || !userDirectory.isDirectory()) {
			userDirectory.mkdirs();
		}
		System.out.println("User directory: " + userDirectory.getAbsolutePath());

		// 2. Check if the workflow is cloned already. If not clone the workflow otherwise pull the latest changes here
		File workflowDirectory = new File(userDirPath + "/" + repoName);
		if (workflowDirectory.exists()) {
			GITUtility.pull(repoName, userDirPath, repoRemotePath + "/" + repoName + ".git");
			System.out.println("Make local repositories upto date");
		}
		else {
			try {
				GITUtility.cloneRepository(workflowDirectory.getAbsolutePath(), repoRemotePath + "/" + repoName
					+ ".git");
				System.out.println("Clone remote repository at workflow directory: "
					+ workflowDirectory.getAbsolutePath());

			}
			catch (Exception e) {
				e.printStackTrace();
				return buildResponse(500,
					"GIT Exception during execution of file " + fileName + ".\n" + e.getMessage(), execCommand.toJSON());
			}
		}

		// 3. Find the specified file
		File fileToExecute = new File(workflowDirectory.getAbsolutePath() + "/" + fileName);
		if (!fileToExecute.exists()) {
			System.out.println("Unable to find file: " + fileToExecute.getAbsolutePath());
			return buildResponse(500, "File not found while trying to execute file " + fileName, execCommand.toJSON());
		}

		// 4. Execute the file
		System.out.println("We execute actual program here");
		String cmd = getSystemCommand(fileName);

		if (cmd == null || cmd.isEmpty()) {
			return buildResponse(500, "Execution of this file type is not yet supported." + fileName,
				execCommand.toJSON());
		}

		Process process = null;
		try {
			process = Runtime.getRuntime().exec(cmd, null, workflowDirectory);
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedReader errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			File file = new File(System.getProperty("user.home") +  "/cwb_last_execution_log.log");
			if (!file.exists())
				file.createNewFile();
			BufferedWriter outputStream = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			String out = null;
			while ((out = inputStream.readLine()) != null) {
				outputStream.write(out);
			}

			while ((out = errorStream.readLine()) != null) {
				outputStream.write(out);
			}

			process.waitFor();
			process.destroy();

			inputStream.close();
			errorStream.close();
			outputStream.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return buildResponse(500, "Exception during execution of file " + fileName + ".\n" + e.getMessage(),
				execCommand.toJSON());
		}

		// 4.1. After execution of programs, list all huge files
		// 4.2. If there are any huge files move them into a folder
		// 4.3. Add the folder in .gitignore file
		// 4.4. Generate download link for the folder
		// 4.5. Create a new file and store the download link for output folder
		
		
		try {
			// 5. Commit everything
			GITUtility.commitLocalChanges(repoName, userDirPath, "Commit after executing file " + fileName + "\n"
				+ comment, userName, userEmail);
			System.out.println("Commited local changes after execution");
			// 6. Push changes back
			GITUtility.push(repoName, userDirPath, repoRemotePath);
			System.out.println("Pushed local changes after execution to remote repository");
		}
		catch (Exception e) {
			e.printStackTrace();
			return buildResponse(500, "GIT Exception during execution of file " + fileName + ".\n" + e.getMessage(),
				execCommand.toJSON());
		}

		// 7. Return successful message
		return buildResponse(200, "Executed " + fileName + " successfully", execCommand.toJSON());
	}

	private String getSystemCommand(String fileName) {
		String command = null;
		String[] fileParts = fileName.split("\\.");
		String extension = fileParts[fileParts.length - 1];
		if (extension.equalsIgnoreCase("py")) {
			command = "python " + fileName;
		}
		else if (extension.equalsIgnoreCase("pro")) {
			command = "idl -e " + fileName.substring(0, fileName.length() - 4);
		}
		return command;
	}

	private Response buildResponse(int code, String message, JSONObject responseObject) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("message", message);
			if (responseObject != null)
				jsonObject.put("response", responseObject.toString());
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return Response.status(code).entity(jsonObject.toString()).build();
	}

}
