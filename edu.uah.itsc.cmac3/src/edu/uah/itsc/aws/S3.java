/*
 * This Class handles the Amazon S3 functionalities
 */
package edu.uah.itsc.aws;

/*
 * This document is a part of the source code and related artifacts for CMAC Project funded by NASA Copyright © 2013,
 * University of Alabama in Huntsville You may not use this file except in compliance with University of Alabama in
 * Huntsville License. Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the license. Date: Jul 26, 2013
 * Filename: S3.java Author:
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.GetGroupPolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetGroupPolicyResult;
import com.amazonaws.services.identitymanagement.model.PutGroupPolicyRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;

import edu.uah.itsc.cmac.util.GITUtility;

public class S3 {
	private static Properties	properties	= null;
	private AmazonS3			amazonS3Service;
	public static String		delimiter	= "/";
	private String				communityBucketName;
	private String				awsAdminAccessKey;
	private String				awsAdminSecretKey;
	private String				awsAccessKey;
	private String				awsSecretKey;

	public S3(String aKey, String sKey) {
		awsAccessKey = aKey;
		awsSecretKey = sKey;
		communityBucketName = getKeyValueFromProperties("community_bucket_name");
		com.amazonaws.auth.AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		amazonS3Service = new AmazonS3Client(credentials);
	}

	public S3() {
		awsAdminAccessKey = getKeyValueFromProperties("aws_admin_access_key");
		awsAdminSecretKey = getKeyValueFromProperties("aws_admin_secret_key");
		com.amazonaws.auth.AWSCredentials credentials = new BasicAWSCredentials(awsAdminAccessKey, awsAdminSecretKey);
		amazonS3Service = new AmazonS3Client(credentials);
		communityBucketName = getKeyValueFromProperties("community_bucket_name");
	}

	public void addGroupPolicy(String groupName, String policyName, String policyToAdd) {
		// Create ami with proper credentials
		AmazonIdentityManagementClient ami = new AmazonIdentityManagementClient(new BasicAWSCredentials(
			awsAdminAccessKey, awsAdminSecretKey));
		GetGroupPolicyRequest ggpRequest = new GetGroupPolicyRequest(groupName, policyName);
		GetGroupPolicyResult ggpResult = ami.getGroupPolicy(ggpRequest);
		String policy = ggpResult.getPolicyDocument();
		try {
			policy = new URI(policy).getPath().toString();
			JSONObject policyObject = new JSONObject(policy);
			JSONArray policyStatementsArray = policyObject.getJSONArray("Statement");
			JSONArray policyToAddArray = new JSONArray("[" + policyToAdd + "]");
			for (int i = 0; i < policyToAddArray.length(); i++) {
				policyStatementsArray.put(policyToAddArray.get(i));
			}
			policyObject.put("Statement", policyStatementsArray);
			policy = policyObject.toString(4);
			// if (1 == 1 ) return;
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();

		}
		// Add new policy as required
		PutGroupPolicyRequest pgpRequest = new PutGroupPolicyRequest(groupName, policyName, policy);
		ami.putGroupPolicy(pgpRequest);

	}

	public void addBucketGroupPolicy(String groupName, String policyName, String bucketName) {
		// Create ami with proper credentials
		AmazonIdentityManagementClient ami = new AmazonIdentityManagementClient(new BasicAWSCredentials(
			awsAdminAccessKey, awsAdminSecretKey));
		GetGroupPolicyRequest ggpRequest = new GetGroupPolicyRequest(groupName, policyName);
		GetGroupPolicyResult ggpResult = ami.getGroupPolicy(ggpRequest);
		String policy = ggpResult.getPolicyDocument();
		try {
			policy = new URI(policy).getPath().toString();
			JSONObject policyObject = new JSONObject(policy);
			JSONArray policyStatementsArray = policyObject.getJSONArray("Statement");
			// We are going to add new bucket in the Resource array list in the json format
			for (int i = 0; i < policyStatementsArray.length(); i++) {
				JSONObject statementObject = (JSONObject) policyStatementsArray.get(i);
				JSONArray actionArray = (JSONArray) statementObject.getJSONArray("Action");
				if (actionArray.length() == 1 && actionArray.getString(0).equalsIgnoreCase("s3:List*")) {
					JSONArray resourceArray = (JSONArray) statementObject.getJSONArray("Resource");
					resourceArray.put(resourceArray.length(), "arn:aws:s3:::" + bucketName);
				}
				else if (actionArray.length() == 3) {
					HashSet<String> set = new HashSet<String>(3);
					set.add(actionArray.getString(0));
					set.add(actionArray.getString(1));
					set.add(actionArray.getString(2));
					if (set.contains("s3:Get*") && set.contains("s3:Put*") && set.contains("s3:List*")) {
						JSONArray resourceArray = (JSONArray) statementObject.getJSONArray("Resource");
						resourceArray.put(resourceArray.length(), "arn:aws:s3:::" + bucketName + "/${aws:username}/*");
					}
				}
			}

			policyObject.put("Statement", policyStatementsArray);
			policy = policyObject.toString(4);
			// if (1 == 1 ) return;
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();

		}
		// Add new policy as required
		PutGroupPolicyRequest pgpRequest = new PutGroupPolicyRequest(groupName, policyName, policy);
		ami.putGroupPolicy(pgpRequest);

	}

	public void addWorkflowSharePolicy(String groupName, String policyName, String workflowPath) {
		// Create ami with proper credentials
		AmazonIdentityManagementClient ami = new AmazonIdentityManagementClient(new BasicAWSCredentials(
			awsAdminAccessKey, awsAdminSecretKey));
		GetGroupPolicyRequest ggpRequest = new GetGroupPolicyRequest(groupName, policyName);
		GetGroupPolicyResult ggpResult = ami.getGroupPolicy(ggpRequest);
		String policy = ggpResult.getPolicyDocument();
		try {
			policy = new URI(policy).getPath().toString();
			JSONObject policyObject = new JSONObject(policy);
			JSONArray policyStatementsArray = policyObject.getJSONArray("Statement");
			// We are going to add new bucket in the Resource array list in the json format
			for (int i = 0; i < policyStatementsArray.length(); i++) {
				JSONObject statementObject = (JSONObject) policyStatementsArray.get(i);
				JSONArray actionArray = (JSONArray) statementObject.getJSONArray("Action");
				if (actionArray.length() == 3) {
					HashSet<String> set = new HashSet<String>(3);
					set.add(actionArray.getString(0));
					set.add(actionArray.getString(1));
					set.add(actionArray.getString(2));
					if (set.contains("s3:Get*") && set.contains("s3:Put*") && set.contains("s3:List*")) {
						JSONArray resourceArray = (JSONArray) statementObject.getJSONArray("Resource");
						resourceArray.put(resourceArray.length(), "arn:aws:s3:::" + workflowPath + "/*");
					}
				}
			}

			policyObject.put("Statement", policyStatementsArray);
			policy = policyObject.toString(4);
			// if (1 == 1 ) return;
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();

		}
		// Add new policy as required
		PutGroupPolicyRequest pgpRequest = new PutGroupPolicyRequest(groupName, policyName, policy);
		ami.putGroupPolicy(pgpRequest);

	}

	public String getAccessKey() {
		return awsAccessKey;
	}

	public String getSecretKey() {
		return awsSecretKey;
	}

	public AmazonS3 getService() {
		return amazonS3Service;
	}

	public String getDelimiter() {
		return delimiter;
	}

	// public String getBucketName(){
	// return bucketName;
	// }

	public String getCommunityBucketName() {
		return communityBucketName;
	}

	// public String getRootFolder(){
	// return rootFolder;
	// }
	// change
	// public void downloadFile(String bucketName,String s3fileName,String localfileName){
	// try{
	// S3Object object = s3Service.getObject(bucketName, s3fileName);
	// InputStream reader = new BufferedInputStream(
	// object.getDataInputStream());
	// File file = new File(localfileName);
	// OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
	//
	// int read = -1;
	//
	// while ( ( read = reader.read() ) != -1 ) {
	// writer.write(read);
	// }
	//
	// writer.flush();
	// writer.close();
	// reader.close();
	// }
	// catch (ServiceException se){
	// System.out.println("ServiceException: "+se.toString());
	// }
	// catch (IOException ioe){
	// System.out.println("IOException: "+ioe.toString());
	// }
	// }

	public void downloadFile(String bucketName, String s3fileName, String localfileName) {
		try {
			System.out.println("S3 downloadFile " + bucketName + "  " + s3fileName);
			com.amazonaws.services.s3.model.S3Object object = amazonS3Service.getObject(bucketName, s3fileName);
			InputStream reader = new BufferedInputStream(object.getObjectContent());
			File file = new File(localfileName);

			OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

			int read = -1;

			while ((read = reader.read()) != -1) {
				writer.write(read);
			}

			writer.flush();
			writer.close();
			reader.close();
		}

		catch (IOException ioe) {
			System.out.println("IOException: in method S3.downloadFile " + ioe.toString());
		}
	}

	public void downloadFolder(String bucketName, String folderName) {
		ObjectListing filteredObjects = amazonS3Service.listObjects(bucketName, folderName);

		System.out.println("downloadFolder ---------> " + bucketName + " " + folderName);

		for (S3ObjectSummary objectSummary : filteredObjects.getObjectSummaries()) {
			String currentResource = objectSummary.getKey();
			System.out.println("folderName=" + folderName);
			System.out.println("downloadFolder currentResource=" + currentResource);
			if ((currentResource.indexOf("_$folder$") > 0)
				&& !folderName.equals(currentResource.substring(0, currentResource.indexOf("_$folder$")))) {
				String prefix = currentResource.substring(0, currentResource.indexOf("_$folder$")) + File.separator;
				System.out.println("Downloading folder " + prefix);
				downloadFolder(bucketName, prefix);
			}
			else if ((currentResource.indexOf("_$folder$") > 0)
				&& folderName.equals(currentResource.substring(0, currentResource.indexOf("_$folder$")))) {

			}
			else {
				IFile f;
				String fullFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString()
					+ java.io.File.separator + bucketName + java.io.File.separator + currentResource;
				System.out.println("Downloading file " + currentResource);
				downloadFile(bucketName, currentResource, fullFilePath);

			}

		}
	}

	public boolean userFolderExists(String name, String bName) {
		boolean found = false;
		ObjectListing filteredObjects = amazonS3Service.listObjects(bName, name);

		for (S3ObjectSummary objectSummary : filteredObjects.getObjectSummaries()) {
			String currentResource = objectSummary.getKey();
			System.out.println("userFolderExists folderName=" + name);
			if (currentResource.equals(name) || name.equals(currentResource + "_$folder")) {
				found = true;
				break;
			}
		}
		return found;

	}

	public void uploadUserFolder(String name, String bName) {

		amazonS3Service.putObject(bName, name + "_$folder$", new ByteArrayInputStream(new byte[0]), null);

	}

	public void uploadFolderName(IFolder folder) {
		String path = folder.getFullPath().toOSString();
		System.out.println("uploadFolderName path=" + path);

		int startPosition = 1;
		int endPosition = path.indexOf(File.separator, startPosition);
		String bucketName = path.substring(startPosition, endPosition);
		System.out.println("uploadFolderName bucketName=" + bucketName);
		String projectAndBucket = bucketName + File.separator;

		startPosition = path.indexOf(projectAndBucket) + projectAndBucket.length();
		endPosition = path.length();// path.indexOf(File.separator, startPosition);
		String folderKey = path.substring(startPosition, endPosition) + "_$folder$";
		System.out.println("folderKey=" + folderKey);
		folderKey = folderKey.replaceAll("\\\\", "/");
		try {

			amazonS3Service.putObject(bucketName, folderKey, new ByteArrayInputStream(new byte[0]), null);
		}
		catch (AmazonServiceException ae) {
			// TODO: handle exception
			System.out.println(User.awsAccessKey + "  " + User.awsSecretKey);
			System.out.println("Error while uploading foldername " + ae.toString());
		}
	}

	public void uploadFolder(IFolder folder) {
		try {
			uploadFolderName(folder);

			System.out.println("Folder name uploaded!");

			IResource[] resources = folder.members();

			for (int i = 0; i < resources.length; i++) {
				if (resources[i] instanceof IFile) {
					IFile file = (IFile) resources[i];
					URI uri = file.getLocationURI();

					// what if file is a link, resolve it.
					if (file.isLinked()) {
						uri = file.getRawLocationURI();
					}

					// Gets native File using EFS
					File f = EFS.getStore(uri).toLocalFile(0, new NullProgressMonitor());
					uploadFile(f);
					System.out.println("File uploaded! " + file.getName());
				}
				else if (resources[i] instanceof IFolder) {
					System.out.println("else folder=" + ((IFolder) resources[i]).getName());
					uploadFolder((IFolder) resources[i]);
				}
			}
			System.out.println("Folders uploaded!");
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}
	}

	public void uploadFile(File file) {
		System.out.println("Selected file getAbsolutePath:" + file.getAbsolutePath());
		String project = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator;
		int startPosition = file.getAbsolutePath().indexOf(project) + project.length();
		int endPosition = file.getAbsolutePath().indexOf(File.separator, startPosition);
		System.out.println("startPosition:endPosition" + startPosition + ":" + endPosition);
		String bucketName = file.getAbsolutePath().substring(startPosition, endPosition);
		project = project + bucketName + File.separator;
		startPosition = file.getAbsolutePath().indexOf(project) + project.length();
		endPosition = file.getAbsolutePath().length();
		String key = file.getAbsolutePath().substring(startPosition, endPosition);

		System.out.println("project: " + project);
		System.out.println("bucketName: " + bucketName);
		System.out.println("Key: " + key);
		key = key.replaceAll("\\\\", "/"); // if the OS is windows the folder slash needs to be /
		amazonS3Service.putObject(bucketName, key, file);
	}

	public void shareFolderName(IFolder folder) {
		String path = folder.getFullPath().toOSString();
		System.out.println("shareFolderName path=" + path);

		int startPosition = 1;
		int endPosition = path.indexOf(File.separator, startPosition);
		String bucketName = path.substring(startPosition, endPosition);

		String projectAndBucket = bucketName + File.separator;

		startPosition = path.indexOf(projectAndBucket) + projectAndBucket.length();
		endPosition = path.length();// path.indexOf(File.separator, startPosition);
		String folderKey = path.substring(startPosition, endPosition) + "_$folder$";
		folderKey = folderKey.replaceAll("\\\\", "/");
		System.out.println("shareFolderName folderKey=" + folderKey);

		CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, folderKey, communityBucketName, folder
			.getProject().getName() + "/" + folderKey);
		IProject communityProject = ResourcesPlugin.getWorkspace().getRoot().getProject(communityBucketName);
		IFolder userFolder = communityProject.getFolder(bucketName).getFolder(User.username);
		String userFolderString = userFolder.toString() + "_$folder$";
		// copyObjRequest.setCannedAccessControlList(CannedAccessControlList.PublicRead);
		try {
			amazonS3Service.copyObject(copyObjRequest);
			uploadFolderName(userFolder);
			System.out.println("----------------------shareFolderName folderKey=" + folderKey);
		}
		catch (AmazonClientException ace) {
			System.out.println("shareFolderName: AmazonClientException " + ace.toString());
		}
		// amazonS3Service.putObject(bucketName, folderKey, new ByteArrayInputStream(new byte[0]), null);
	}

	public String getS3ResourceName(String localFileName) {
		String bn = localFileName.substring(localFileName.indexOf(java.io.File.separator, 1) + 1,
			localFileName.length());
		bn = bn.replace('\\', '/');
		return bn;
	}

	public String getBucketName(String localFileName) {

		String bn = localFileName.substring(1, localFileName.indexOf(java.io.File.separator, 1));
		return bn;
	}

	public String getLocalResourceName(String localFileName) {
		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + localFileName;
	}

	public void shareFile(File file) {
		System.out.println("Selected file getAbsolutePath:" + file.getAbsolutePath());
		String project = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator;
		int startPosition = file.getAbsolutePath().indexOf(project) + project.length();
		int endPosition = file.getAbsolutePath().indexOf(File.separator, startPosition);
		String bucketName = file.getAbsolutePath().substring(startPosition, endPosition);
		System.out.println("Source Bucket Name:" + bucketName);
		project = project + bucketName + File.separator;
		startPosition = file.getAbsolutePath().indexOf(project) + project.length();
		endPosition = file.getAbsolutePath().length();
		String key = file.getAbsolutePath().substring(startPosition, endPosition);

		key = key.replace('\\', '/');
		System.out.println("Key:" + key);
		CopyObjectRequest copyObjRequest = new CopyObjectRequest(bucketName, key, communityBucketName, bucketName + "/"
			+ key);

		// copyObjRequest.setCannedAccessControlList(CannedAccessControlList.PublicRead);

		try {
			amazonS3Service.copyObject(copyObjRequest);
		}
		catch (AmazonClientException ace) {
			System.out.println("shareFile: AmazonClientException " + ace.toString());
		}
		// amazonS3Service.setObjectAcl(communityBucketName, key, CannedAccessControlList.PublicRead);
	}

	public void shareFolder(IFolder folder) {

		System.out.println("Share Folder-----------------------> " + folder.getName());
		shareFolderName(folder);
		try {
			IResource[] resources = folder.members();

			for (int i = 0; i < resources.length; i++) {
				if (resources[i] instanceof IFile) {
					IFile file = (IFile) resources[i];
					URI uri = file.getLocationURI();

					// what if file is a link, resolve it.
					if (file.isLinked()) {
						uri = file.getRawLocationURI();
					}

					// Gets native File using EFS
					File f = EFS.getStore(uri).toLocalFile(0, new NullProgressMonitor());
					shareFile(f);
				}
				else if (resources[i] instanceof IFolder) {
					System.out.println("else folder=" + ((IFolder) resources[i]).getName());
					shareFolder((IFolder) resources[i]);
				}

			}
		}
		catch (CoreException ce) {
			ce.printStackTrace();
		}
	}

	public void shareGITFolder(IFolder folder) {
		String path = folder.getLocation().toString();

		// TODO: Copy bucket contents of this workflow repo from private bucket to community bucket
		String sourceBucketName = folder.getProject().getName();
		String workflowPath = folder.getProjectRelativePath().toString().replaceFirst("^/", "");
		String destBucketName = getCommunityBucketName();
		String prefix = folder.getFullPath().toString().replaceFirst(sourceBucketName, "").replace("//", "");

		copyFolderInS3(sourceBucketName, workflowPath, destBucketName, sourceBucketName);

		// TODO: Modify remote reference in current local git directory
		// Delete files from bucket and clone back. Need to replace with better solution, i.e try to refer to repository
		// in community bucket directly
		deleteFilesFromBucket(getAllFiles(sourceBucketName, prefix), sourceBucketName);

		try {
			folder.delete(true, null);
			String repoCompleteRemotepath = "amazon-s3://.jgit@" + getCommunityBucketName()
				+ folder.getFullPath().toString() + ".git";
			GITUtility.cloneRepository(folder.getLocation().toString(), repoCompleteRemotepath);
			folder.refreshLocal(IFolder.DEPTH_INFINITE, null);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void copyFolderInS3(String sourceBucketName, String sourceFolder, String destBucketName, String destFolder) {
		ListObjectsRequest lor = new ListObjectsRequest();
		lor.setBucketName(sourceBucketName);
		lor.setDelimiter(getDelimiter());
		lor.setPrefix(sourceFolder);

		ObjectListing filteredObjects = null;
		try {
			filteredObjects = getService().listObjects(lor);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception listing filtered objects");
			return;
		}

		List<String> commonPrefixes = filteredObjects.getCommonPrefixes();
		for (String currentResource : commonPrefixes) {
			System.out.println(currentResource);
			copyFolderInS3(sourceBucketName, currentResource, destBucketName, destFolder);
		}
		for (S3ObjectSummary objectSummary : filteredObjects.getObjectSummaries()) {
			String currentResource = objectSummary.getKey();
			String destResource = destFolder + "/" + currentResource;
			CopyObjectRequest copyRequest = new CopyObjectRequest(sourceBucketName, currentResource, destBucketName,
				destResource);
			try {
				amazonS3Service.copyObject(copyRequest);
			}
			catch (AmazonClientException ace) {
				System.out.println("shareFile: AmazonClientException " + ace.toString());
			}

		}

	}

	/**
	 * @return the amazonS3Service
	 */
	public AmazonS3 getAmazonS3Service() {
		return amazonS3Service;
	}

	public ArrayList<String> getAllBuckets() {
		List<Bucket> allBuckets = amazonS3Service.listBuckets();
		ArrayList<String> allBucketsString = new ArrayList<String>();
		for (Bucket bucket : allBuckets) {
			allBucketsString.add(bucket.getName());
		}
		return allBucketsString;
	}

	public boolean doesBucketExist(String bucketToCheck) {
		ArrayList<String> allBuckets = getAllBuckets();
		for (String bucket : allBuckets) {
			if (bucketToCheck.equalsIgnoreCase(bucket))
				return true;
		}
		return false;
	}

	public void deleteBucket(String bucketName) {
		DeleteBucketRequest deleteRequest = new DeleteBucketRequest(bucketName);
		amazonS3Service.deleteBucket(deleteRequest);
	}

	private static String getKeyValueFromProperties(String key) {
		if (properties != null && properties.containsKey(key)) {
			return properties.getProperty(key);
		}
		if (properties == null) {
			properties = new Properties();
			try {
				properties.load(S3.class.getClassLoader().getResourceAsStream("cmac.properties"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties.getProperty(key);
	}

	public ArrayList<String> getAllFiles(String bucket, String prefix) {
		ObjectListing listing = amazonS3Service.listObjects(bucket, prefix);
		ArrayList<String> allFilesString = new ArrayList<String>();
		extractKeys(listing, allFilesString);
		while (listing.isTruncated()) {
			listing = amazonS3Service.listNextBatchOfObjects(listing);
			extractKeys(listing, allFilesString);
		}
		return allFilesString;
	}

	private void extractKeys(ObjectListing listing, ArrayList<String> allFilesString) {
		List<S3ObjectSummary> list = listing.getObjectSummaries();
		for (S3ObjectSummary s3ObjectSummary : list) {
			allFilesString.add(s3ObjectSummary.getKey());
		}
	}

	/**
	 * This method deletes files from a bucket. Array list of string of files to be deleted and bucket should be
	 * provided
	 * 
	 * @param selectedFiles
	 * @param bucketName
	 */
	public void deleteFilesFromBucket(ArrayList<String> selectedFiles, String bucketName) {
		if (selectedFiles.isEmpty())
			return;
		DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName);
		ArrayList<KeyVersion> keys = new ArrayList<KeyVersion>();
		for (String selectedFile : selectedFiles) {
			KeyVersion keyVersion = new KeyVersion(selectedFile);
			keys.add(keyVersion);
		}
		deleteRequest.setKeys(keys);
		try {
			DeleteObjectsResult deleteResult = amazonS3Service.deleteObjects(deleteRequest);
			System.out.format("Successfully deleted %s items\n", deleteResult.getDeletedObjects().size());
		}
		catch (MultiObjectDeleteException e) {
			e.printStackTrace();
			for (DeleteError deleteError : e.getErrors()) {
				System.out.format("Object Key: %s\t%s\t%s\n", deleteError.getKey(), deleteError.getCode(),
					deleteError.getMessage());
			}
		}
	}

}