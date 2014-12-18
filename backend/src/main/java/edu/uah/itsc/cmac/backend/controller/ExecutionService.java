/**
 * 
 */
package edu.uah.itsc.cmac.backend.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

import edu.uah.itsc.cmac.backend.util.FileUtility;
import edu.uah.itsc.cmac.backend.util.GITUtility;
import edu.uah.itsc.cmac.backend.util.PropertyUtility;
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
		String large_bucket_name = execCommand.getLargeBucketName();
		boolean isSharedRepo = execCommand.isSharedRepo();

		PropertyUtility property = new PropertyUtility("cmacBackend.properties");

		String repoRemotePath = "amazon-s3://.jgit@";
		String userDirPath = null;
		String ignore_large_size_limit = null;
		String large_directory = null;

		userDirPath = property.getValue("backend_environment_path");
		ignore_large_size_limit = property.getValue("ignore_large_size_limit");
		large_directory = property.getValue("large_directory");

		if (userDirPath == null)
			userDirPath = "/home/ec2-user/cmac_backend_environment/s3/";

		if (ignore_large_size_limit == null)
			ignore_large_size_limit = "50";

		if (large_directory == null)
			large_directory = "large_files";

		if (large_bucket_name == null)
			large_bucket_name = "cwb_large_files_bucket";

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

		// 2. Check if the workflow is cloned already. If not clone the workflow otherwise delete old repository
		File workflowDirectory = new File(userDirPath + "/" + repoName);
		if (workflowDirectory.exists()) {
			delete(workflowDirectory);
			// GITUtility.pull(repoName, userDirPath, repoRemotePath + "/" + repoName + ".git");
			// System.out.println("Make local repositories upto date");
			System.out.println("Deleted old repository");
		}
		try {
			GITUtility.cloneRepository(workflowDirectory.getAbsolutePath(), repoRemotePath + "/" + repoName + ".git");
			System.out.println("Clone remote repository at workflow directory: " + workflowDirectory.getAbsolutePath());

		}
		catch (Exception e) {
			e.printStackTrace();
			return buildResponse(500, "GIT Exception during execution of file " + fileName + ".\n" + e.getMessage(),
				execCommand.toJSON());
		}

		// 3. Find the specified file
		File fileToExecute = new File(workflowDirectory.getAbsolutePath() + "/" + fileName);
		if (!fileToExecute.exists()) {
			System.out.println("Unable to find file: " + fileToExecute.getAbsolutePath());
			return buildResponse(500, "File not found while trying to execute file " + fileName, execCommand.toJSON());
		}

		// 3.1 Store current directory structure in ArrayList
		ArrayList<File> prevDirectoryStructure = walkDirectory(workflowDirectory);

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
			File file = new File(System.getProperty("user.home") + "/cwb_last_execution_log.log");
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
		int large_file_limit = Integer.parseInt(ignore_large_size_limit);

		// 4.1.1 Store current directory structure in ArrayList
		ArrayList<File> currDirectoryStructure = walkDirectory(workflowDirectory);
		currDirectoryStructure.removeAll(prevDirectoryStructure);

		ArrayList<File> ignoredFiles = new ArrayList<File>();
		for (File newFile : currDirectoryStructure) {
			if (newFile.length() >= large_file_limit)
				ignoredFiles.add(newFile);
		}

		JSONArray ignoredFilesArray = null;
		// 4.2. If there are any huge files move them into a folder
		if (ignoredFiles.size() > 0) {
			File largeDir = new File(workflowDirectory.getAbsolutePath() + "/" + large_directory);
			File gitIgnoreFile = new File(workflowDirectory.getAbsolutePath() + "/.gitignore");

			if (!largeDir.exists() || !largeDir.isDirectory()) {
				largeDir.mkdirs();
			}

			// 4.3. Add the folder in .gitignore file
			try {
				if (!gitIgnoreFile.exists()) {
					gitIgnoreFile.createNewFile();
				}

				FileInputStream fis = new FileInputStream(gitIgnoreFile);
				byte[] data = new byte[(int) gitIgnoreFile.length()];
				fis.read(data);
				fis.close();
				String contents = new String(data, "UTF-8");

				Pattern regex = Pattern.compile("^" + largeDir.getName() + "/$", Pattern.MULTILINE);
				Matcher regexMatcher = regex.matcher(contents);

				if (!regexMatcher.find()) {
					contents = contents + "\n" + largeDir.getName() + "/";
					BufferedWriter writer = new BufferedWriter(new FileWriter(gitIgnoreFile));
					writer.write(contents);
					writer.close();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}

			// 4.4 Move all the ignored files from large dir to S3
			ignoredFilesArray = new JSONArray();
			AmazonS3Client amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
			for (File file : ignoredFiles) {
				String relativePath = file.getPath().replace(workflowDirectory.getPath(), "").replaceAll("\\\\", "/");
				if (relativePath.startsWith("/"))
					relativePath = relativePath.replaceFirst("/", "");
				String s3Location = bucketName + "/" + userName + "/" + repoName + "/" + large_directory + "/"
					+ relativePath;
				s3Location = s3Location.replaceAll("//", "/");
				amazonS3Client.putObject(large_bucket_name, s3Location, file);
				JSONObject fileObject = new JSONObject();
				fileObject.put("name", file.getName());
				fileObject.put("path", relativePath);
				fileObject.put("size", file.length());
				fileObject.put("s3Location", s3Location);
				ignoredFilesArray.put(fileObject);
			}

			// 4.6 Put all the large files inside the directory
			for (File file : ignoredFiles) {
				String name = file.getPath().replace(workflowDirectory.getPath(), "");
				String newName = largeDir.getAbsolutePath() + "/" + name;
				File newFile = new File(newName);

				// The new file may be created anywhere in a directory, so create directory if necessary
				String newDirName = newFile.getAbsolutePath().replace(newFile.getName(), "");
				File newDir = new File(newDirName);
				if (!newDir.exists())
					newDir.mkdirs();
				if (newFile.exists())
					newFile.delete();
				file.renameTo(newFile);
			}

		}

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

		JSONObject responseObject = new JSONObject();
		responseObject.put("files", ignoredFilesArray);
		responseObject.put("requestInput", execCommand.toJSON());
		responseObject.put("largeDir", large_directory);
		responseObject.put("largeBucketName", large_bucket_name);

		// 7. Return successful message
		return buildResponse(200, "Successfully executed " + fileName, responseObject);
	}

	private void delete(File file) {
		if (file.isDirectory()) {
			for (File listFile: file.listFiles()) {
				delete(listFile);
			}
		}
		file.delete();
	}

	private ArrayList<File> walkDirectory(File file) {
		if (file == null)
			return null;

		if (file.getName().startsWith(".git"))
			return null;

		ArrayList<File> directoryStructure = new ArrayList<File>();

		if (file.isDirectory()) {
			for (File listFile : file.listFiles()) {
				if (listFile.getName().startsWith(".git"))
					continue;
				directoryStructure.addAll(walkDirectory(listFile));
			}
		}
		else
			directoryStructure.add(file);
		return directoryStructure;
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
				jsonObject.put("response", responseObject);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return Response.status(code).entity(jsonObject.toString()).build();
	}

}
