/**
 * 
 */
package edu.uah.itsc.aws;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateImageRequest;
import com.amazonaws.services.ec2.model.CreateImageResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DeregisterImageRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.ModifyImageAttributeRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.ec2.model.Tag;

/**
 * @author sshrestha
 * 
 */
public class EC2 {
	private AmazonEC2 amazonEC2;
	private String awsAdminAccessKey = "AKIAIKX2MDKF6M6GXD7Q";
	private String awsAdminSecretKey = "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";
	private final String AWS_USER_ID = "709010204591";

	public EC2() {
		AWSCredentials credentials = new BasicAWSCredentials(awsAdminAccessKey,
				awsAdminSecretKey);
		System.out.println("User................" + User.awsAccessKey + "\t"
				+ User.awsSecretKey);
		amazonEC2 = new AmazonEC2Client(credentials);
	}

	public List<Image> getAMIImages() throws AmazonServiceException,
			AmazonClientException {
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withOwners(AWS_USER_ID);
		List<Image> images = amazonEC2.describeImages(request).getImages();
		return images;
	}

	public void modifyImageAttribute(ModifyImageAttributeRequest request)
			throws AmazonServiceException, AmazonClientException {
		amazonEC2.modifyImageAttribute(request);
	}

	public CreateImageResult createImage(CreateImageRequest createRequest)
			throws AmazonServiceException, AmazonClientException {
		return amazonEC2.createImage(createRequest);

	}

	public void deregisterImage(DeregisterImageRequest request)
			throws AmazonServiceException, AmazonClientException {
		amazonEC2.deregisterImage(request);
	}

	public void createTags(CreateTagsRequest ctRequest)
			throws AmazonServiceException, AmazonClientException {
		amazonEC2.createTags(ctRequest);
	}

	public ArrayList<Instance> getInstances(String instanceState)
			throws AmazonServiceException, AmazonClientException {
		/* Amazon EC2 instance state
		 * Allowed Values: 
		 * pending, running, shutting-down, terminated, stopping, stopped 
		 */
		ArrayList<Instance> allInstances = new ArrayList<Instance>();
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		if (instanceState != null) {
			Filter filter = new Filter();
			filter.withName("instance-state-name");
			filter.withValues(instanceState);
			request.withFilters(filter);
		}

		DescribeInstancesResult result = amazonEC2.describeInstances(request);
		List<Reservation> reservations = result.getReservations();
		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();
			for (Instance instance : instances) {
				allInstances.add(instance);

			}
		}
		return allInstances;
	}

	public String getInstancePublicURL(String instanceNameTag)
			throws AmazonServiceException, AmazonClientException {
		Filter filter = new Filter();
		filter.withName("tag:Name");
		filter.withValues(instanceNameTag);
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		DescribeInstancesResult result = amazonEC2.describeInstances(request
				.withFilters(filter));

		List<Reservation> reservations = result.getReservations();

		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();

			for (Instance instance : instances) {
				String publicURL = instance.getPublicIpAddress();
				if (publicURL == null)
					return null;
				else
					return "http://" + publicURL + ":3000/posts/";
			}
		}
		return null;
	}

	public StartInstancesResult startInstances(List<String> instanceIds)
			throws AmazonServiceException, AmazonClientException {
		StartInstancesRequest startRequest = new StartInstancesRequest(
				instanceIds);
		return amazonEC2.startInstances(startRequest);
	}

	public StopInstancesResult stopInstances(List<String> instanceIds)
			throws AmazonServiceException, AmazonClientException {
		StopInstancesRequest stopRequest = new StopInstancesRequest(instanceIds);
		return amazonEC2.stopInstances(stopRequest);
	}

	public void runInstances(RunInstancesRequest runInstancesRequest,
			String instanceName) {
		RunInstancesResult result = amazonEC2.runInstances(runInstancesRequest);
		Reservation reservation = result.getReservation();
		List<Instance> instanceList = reservation.getInstances();
		Instance instance = instanceList.get(0);
		Tag tag = new Tag("Name", instanceName);
		List<String> resources = new ArrayList<String>();
		resources.add(instance.getInstanceId());
		List<Tag> tags = new ArrayList<Tag>();
		tags.add(tag);
		CreateTagsRequest tagRequest = new CreateTagsRequest(resources, tags);
		amazonEC2.createTags(tagRequest);
		
	}
}
