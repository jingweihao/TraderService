package com.S3Connection;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3Instance2 {
	
	private AmazonS3 s3;
	
	private S3Instance2()
	{
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (/Users/xiaofeng/.aws/credentials), and is in valid format.",
                    e);
        }
        s3 = new AmazonS3Client(credentials);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        s3.setRegion(usWest2);
	}
	
    private static final S3Instance2 s3instance = new S3Instance2();
	
	public static S3Instance2 getInstance()
	{
		System.out.println("Get Instance~~~~~~~~");
		return s3instance;
	}
	
}
