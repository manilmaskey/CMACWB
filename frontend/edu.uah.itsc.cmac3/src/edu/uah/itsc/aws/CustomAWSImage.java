package edu.uah.itsc.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Image;

public class CustomAWSImage {
	private Image	image;
	private Regions	region;

	public CustomAWSImage(Image image, Regions region) {
		super();
		this.image = image;
		this.region = region;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Regions getRegion() {
		return region;
	}

	public void setRegion(Regions region) {
		this.region = region;
	}
}
