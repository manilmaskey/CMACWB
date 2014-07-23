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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFolder;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.TagOpt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.AmazonClientException;
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
import com.amazonaws.services.s3.model.DeleteObjectsRequest.KeyVersion;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.MultiObjectDeleteException;
import com.amazonaws.services.s3.model.MultiObjectDeleteException.DeleteError;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import edu.uah.itsc.cmac.util.GITUtility;

public class S3 {
	private static Properties	properties			= null;
	private AmazonS3			amazonS3Service;
	public static String		delimiter			= "/";
	private static String		communityBucketName	= getKeyValueFromProperties("community_bucket_name");
	private String				awsAdminAccessKey;
	private String				awsAdminSecretKey;
	private String				awsAccessKey;
	private String				awsSecretKey;

	public S3(String aKey, String sKey) {
		awsAccessKey = aKey;
		awsSecretKey = sKey;
		com.amazonaws.auth.AWSCredentials credentials = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
		amazonS3Service = new AmazonS3Client(credentials);
	}

	public S3() {
		awsAdminAccessKey = getKeyValueFromProperties("aws_admin_access_key");
		awsAdminSecretKey = getKeyValueFromProperties("aws_admin_secret_key");
		com.amazonaws.auth.AWSCredentials credentials = new BasicAWSCredentials(awsAdminAccessKey, awsAdminSecretKey);
		amazonS3Service = new AmazonS3Client(credentials);
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

	public static String getCommunityBucketName() {
		return communityBucketName;
	}

	public void shareGITFolder(IFolder folder) {
		String sourceBucketName = folder.getProject().getName();
		// String workflowPath = folder.getProjectRelativePath().toString().replaceFirst("^/", "");
		// String workflowPath = folder.getProject().getName() + "/" + User.username + "/" + folder.getName() + ".git";

		// //////
		// /////
		// /////
		// copy object in S3 not working ??????

		// String workflowPath = folder.getProject().getName() + "/" + User.username + "/" + folder.getName() + ".git";
		String workflowPath = User.username + "/" + folder.getName() + ".git" + "/";
		String destBucketName = getCommunityBucketName();
		// String prefix = folder.getFullPath().toString().replaceFirst(sourceBucketName, "").replace("//", "");

		// Move repository from private bucket to community bucket and delete repository from private bucket

		copyFolderInS3(sourceBucketName, workflowPath, destBucketName, sourceBucketName);

		deleteFilesFromBucket(getAllFiles(sourceBucketName, workflowPath), sourceBucketName);

		// Modify remote reference in current local git directory

		Git git = GITUtility.getGit(folder.getName(), folder.getParent().getLocation().toString());
		if (git != null) {
			Repository repository = git.getRepository();
			// Get the config file, reset origin section with community bucket repository location
			StoredConfig config = repository.getConfig();
			config.unsetSection("remote", "origin");
			String repoCompleteRemotepath = "amazon-s3://.jgit@" + getCommunityBucketName()
				+ folder.getFullPath().toString() + ".git";
			config.setString("remote", "origin", "url", repoCompleteRemotepath);

			try {
				RemoteConfig remoteConfig = new RemoteConfig(config, "origin");
				remoteConfig.addFetchRefSpec(new RefSpec("+refs/heads/*:refs/remotes/origin/*"));
				remoteConfig.addPushRefSpec(new RefSpec("refs/heads/master:refs/heads/master"));
				remoteConfig.setTagOpt(TagOpt.FETCH_TAGS);
				remoteConfig.update(config);
				config.save();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (URISyntaxException e) {
				e.printStackTrace();
			}
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

	public static String getKeyValueFromProperties(String key) {
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