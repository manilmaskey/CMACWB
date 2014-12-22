package edu.uah.itsc.aws;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.model.Instance;

public class CustomAWSInstance {
	private Instance	instance;
	private Regions		region;

	public CustomAWSInstance(Instance instance, Regions region) {
		super();
		this.instance = instance;
		this.region = region;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Regions getRegion() {
		return region;
	}

	public void setRegion(Regions region) {
		this.region = region;
	}
}
