package com.S3Connection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.data.Sales;

public class S3Instance2 {
	
	private AmazonS3 s3;
	
	private S3Instance2()
	{
		AWSCredentials credentials = null;
        try {
//            credentials = new ProfileCredentialsProvider("default").getCredentials();
//        	credentials = new BasicAWSCredentials("AKIAJMDC4NCM6KLE24BQ","GyX4zHGbLdaGOoC4UsRG/bTgLop08TZEo16o67zt");
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
	
    private static File createSampleFile(ArrayList<String> detail) throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for(int i=0; i<detail.size(); i++){
        	writer.write(detail.get(i)+"\n");
        }
        writer.close();
        return file;
    }

    private static ArrayList<String> displayTextInputStream(InputStream input) throws IOException {
    	ArrayList<String> ret=new ArrayList<String>(); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;
            ret.add(line);
        }
        return ret;
    }
	
	public String createItem(Sales detail) throws IOException{
		String itemId = UUID.randomUUID().toString().replace("-", "");
		String objname = detail.getCategory()+"/"+detail.getName()+"/"+itemId;
		ArrayList<String> content = new ArrayList<String>();
		content.add(itemId);
		content.add(detail.getPrice());
		content.add(detail.getInfo());
		content.add(detail.getImgpath());
		content.add(detail.getSellername().toLowerCase());
		content.add(detail.getSellertel());
		s3.putObject(new PutObjectRequest("myitem", objname, createSampleFile(content)));
		for (Bucket bucket : s3.listBuckets()) {
			String judge = bucket.getName();
	        if(judge.equals("myperson")){
	        	String personname = detail.getSellername().toLowerCase()+"/"+detail.getSellertel();
	        	S3Object object = s3.getObject(new GetObjectRequest(judge, personname));
				ArrayList<String> temp = displayTextInputStream(object.getObjectContent());
				temp.add(objname);
	        	s3.putObject(new PutObjectRequest(judge, itemId, createSampleFile(temp)));
        	}
        }
		return itemId;
	}
	
}
