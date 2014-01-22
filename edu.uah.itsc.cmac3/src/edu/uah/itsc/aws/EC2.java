/**
 * 
 */
package edu.uah.itsc.aws;

import java.util.ArrayList;
import java.util.List;

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
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

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

	public List<Image> getAMIImages() {
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withOwners(AWS_USER_ID);
		List<Image> images = amazonEC2.describeImages(request).getImages();
		return images;
	}

	public void modifyImageAttribute(ModifyImageAttributeRequest request) {
		amazonEC2.modifyImageAttribute(request);
	}

	public CreateImageResult createImage(CreateImageRequest createRequest) {
		return amazonEC2.createImage(createRequest);

	}

	public void deregisterImage(DeregisterImageRequest request) {
		amazonEC2.deregisterImage(request);
	}

	public void createTags(CreateTagsRequest ctRequest) {
		amazonEC2.createTags(ctRequest);
	}

	public ArrayList<Instance> getInstances() {
		ArrayList<Instance> allInstances = new ArrayList<Instance>();
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		Filter filter = new Filter();
		filter.withName("instance-state-name");
		filter.withValues("running");
		request.withFilters(filter);

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

	public String getInstancePublicURL(String instanceNameTag) {
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
	
	
	public RunInstancesResult runInstances(RunInstancesRequest runInstanceRequest){
		return amazonEC2.runInstances(runInstanceRequest);
	}
	
	public StopInstancesResult stopInstances(StopInstancesRequest stopInstanceRequest){
		return amazonEC2.stopInstances(stopInstanceRequest);
	}
}
