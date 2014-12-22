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
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
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

import edu.uah.itsc.cmac.Utilities;

/**
 * @author sshrestha
 * 
 */
public class EC2 {
	private AmazonEC2	amazonEC2;
	private String		awsAdminAccessKey;
	private String		awsAdminSecretKey;
	private String		awsUserID;

	public EC2() {
		awsAdminAccessKey = Utilities.getKeyValueFromPreferences("s3", "aws_admin_access_key");
		awsAdminSecretKey = Utilities.getKeyValueFromPreferences("s3", "aws_admin_secret_key");
		awsUserID = Utilities.getKeyValueFromPreferences("s3", "aws_user_id");
		AWSCredentials credentials = new BasicAWSCredentials(awsAdminAccessKey, awsAdminSecretKey);
		System.out.println("User................" + User.awsAccessKey + "\t" + User.awsSecretKey);
		amazonEC2 = new AmazonEC2Client(credentials);
	}

	public List<CustomAWSImage> getAMIImages() throws AmazonServiceException, AmazonClientException {
		DescribeImagesRequest request = new DescribeImagesRequest();
		request.withOwners(awsUserID);
		List<CustomAWSImage> images = new ArrayList<CustomAWSImage>();

		for (Regions r : Regions.values()) {
			// We won't have access to all regions, for eg. GovCloud, so catch exception that AWS throws and continue
			try {
				amazonEC2.setRegion(Region.getRegion(r));
				List<Image> amiImages = amazonEC2.describeImages(request).getImages();
				for (Image amiImage : amiImages) {
					CustomAWSImage image = new CustomAWSImage(amiImage, r);
					images.add(image);
				}
			}
			catch (Exception e) {
				continue;
			}
		}

		return images;
	}

	public void modifyImageAttribute(ModifyImageAttributeRequest request) throws AmazonServiceException,
		AmazonClientException {
		amazonEC2.modifyImageAttribute(request);
	}

	public CreateImageResult createImage(CreateImageRequest createRequest, Regions region)
		throws AmazonServiceException, AmazonClientException {
		amazonEC2.setRegion(Region.getRegion(region));
		CreateImageResult createImageResult = amazonEC2.createImage(createRequest);
		amazonEC2.setRegion(Region.getRegion(Regions.US_EAST_1));
		return createImageResult;
	}

	public void deregisterImage(DeregisterImageRequest request, Regions region) throws AmazonServiceException,
		AmazonClientException {
		amazonEC2.setRegion(Region.getRegion(region));
		amazonEC2.deregisterImage(request);
		amazonEC2.setRegion(Region.getRegion(Regions.US_EAST_1));
	}

	public void createTags(CreateTagsRequest ctRequest, Regions region) throws AmazonServiceException,
		AmazonClientException {
		amazonEC2.setRegion(Region.getRegion(region));
		amazonEC2.createTags(ctRequest);
		amazonEC2.setRegion(Region.getRegion(Regions.US_EAST_1));
	}

	public ArrayList<CustomAWSInstance> getInstances(String instanceState) throws AmazonServiceException,
		AmazonClientException {
		/*
		 * Amazon EC2 instance state Allowed Values: pending, running, shutting-down, terminated, stopping, stopped
		 */
		ArrayList<CustomAWSInstance> allInstances = new ArrayList<CustomAWSInstance>();
		DescribeInstancesRequest request = new DescribeInstancesRequest();
		if (instanceState != null) {
			Filter filter = new Filter();
			filter.withName("instance-state-name");
			filter.withValues(instanceState);
			request.withFilters(filter);
		}

		for (Regions r : Regions.values()) {
			// We won't have access to all regions, for eg. GovCloud, so catch exception that AWS throws and continue
			try {
				amazonEC2.setRegion(Region.getRegion(r));
				for (Reservation reservation : amazonEC2.describeInstances(request).getReservations()) {
					for (Instance instance : reservation.getInstances()) {
						allInstances.add(new CustomAWSInstance(instance, r));
					}
				}
			}
			catch (Exception e) {
				continue;
			}
		}
		return allInstances;
	}

	public String getInstancePublicURL(String instanceNameTag) throws AmazonServiceException, AmazonClientException {
		Filter filter = new Filter();
		filter.withName("tag:Name");
		filter.withValues(instanceNameTag);
		DescribeInstancesRequest request = new DescribeInstancesRequest();

		List<Reservation> reservations = new ArrayList<Reservation>();

		for (Regions r : Regions.values()) {
			// We won't have access to all regions, for eg. GovCloud, so catch exception that AWS throws and continue
			try {
				amazonEC2.setRegion(Region.getRegion(r));
				reservations.addAll(amazonEC2.describeInstances(request.withFilters(filter)).getReservations());
			}
			catch (Exception e) {
				continue;
			}

		}

		amazonEC2.setRegion(Region.getRegion(Regions.US_EAST_1));

		for (Reservation reservation : reservations) {
			List<Instance> instances = reservation.getInstances();

			for (Instance instance : instances) {
				String publicURL = instance.getPublicIpAddress();
				if (publicURL == null)
					return null;
				else
					return publicURL;
			}
		}
		return null;
	}

	public StartInstancesResult startInstances(List<String> instanceIds, Regions regions)
		throws AmazonServiceException, AmazonClientException {
		amazonEC2.setRegion(Region.getRegion(regions));
		StartInstancesRequest startRequest = new StartInstancesRequest(instanceIds);
		StartInstancesResult result = amazonEC2.startInstances(startRequest);
		amazonEC2.setRegion(Region.getRegion(Regions.US_EAST_1));
		return result;
	}

	public StopInstancesResult stopInstances(List<String> instanceIds, Regions regions) throws AmazonServiceException,
		AmazonClientException {
		amazonEC2.setRegion(Region.getRegion(regions));
		StopInstancesRequest stopRequest = new StopInstancesRequest(instanceIds);
		StopInstancesResult result = amazonEC2.stopInstances(stopRequest);
		amazonEC2.setRegion(Region.getRegion(Regions.US_EAST_1));
		return result;
	}

	public void runInstances(RunInstancesRequest runInstancesRequest, String instanceName, Regions region) {
		amazonEC2.setRegion(Region.getRegion(region));
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

	public Regions getInstanceRegion(String instanceId) {
		DescribeInstancesRequest dir = new DescribeInstancesRequest().withInstanceIds(instanceId);
		Regions instanceRegion = null;
		for (Regions region : Regions.values()) {
			try {
				amazonEC2.setRegion(Region.getRegion(region));
				DescribeInstancesResult result = amazonEC2.describeInstances(dir);
				if (!result.getReservations().isEmpty()) {
					instanceRegion = region;
				}
			}
			catch (Exception e) {
				continue;
			}
		}
		amazonEC2.setRegion(Region.getRegion(Regions.US_EAST_1));
		return instanceRegion;
	}
}
