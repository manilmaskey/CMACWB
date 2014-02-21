package edu.uah.itsc.cmac.ui;

import edu.uah.itsc.aws.S3;
import edu.uah.itsc.aws.User;

public class Utilities {
	private S3	s3;

	public Utilities() {
		s3 = new S3(User.awsAccessKey, User.awsSecretKey);
	}

}
