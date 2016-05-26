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

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.data.SearchResult;

public class S3Instance {
	
	private AmazonS3 s3;
	
	private S3Instance()
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
	
    private static final S3Instance s3instance = new S3Instance();
	
	public static S3Instance getInstance()
	{
		System.out.println("Get Instance~~~~~~~~");
		return s3instance;
	}
	
    private static File createSampleFile(String detail) throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(detail+"\n");
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
    
    public void createItem(String item, String[] detail) throws IOException{
        String bucketname=item;
        String[] key=new String[5];
        key[0]="seller";
        key[1]="buyer";
        key[2]="price";
        key[3]="img";
        key[4]="info";
        try {
        	s3.createBucket(bucketname);
            System.out.println("Uploading a new object to S3 from a file\n");
            for(int i=0;i<5;i++){
            	s3.putObject(new PutObjectRequest(bucketname, key[i], createSampleFile(detail[i])));
            }
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
    
    public void delete(String bucketname){
//    	String key="";
//    	System.out.println("Deleting an object\n");
//    	s3.deleteObject(bucketname, key);

    	System.out.println("Deleting bucket " + bucketname + "\n");
    	s3.deleteBucket(bucketname);
    }
    
    public ArrayList<SearchResult> search(String str) throws IOException{
    	ArrayList<SearchResult> ret=new ArrayList<SearchResult>();
    	String[] bucketname=new String[3];
    	ArrayList<String> bucketName=new ArrayList<String>();
        for (Bucket bucket : s3.listBuckets()) {
        	bucketname=bucket.getName().split("-");
        	int diff=Math.abs(str.length()-bucketname[0].length());
        	if(minld(str,bucketname[0])-diff<=Math.min(str.length(), bucketname[0].length())/2){
        		bucketName.add(bucket.getName());
        	}
        	diff=Math.abs(str.length()-bucketname[1].length());
        	if(minld(str,bucketname[1])-diff<=Math.min(str.length(), bucketname[1].length())/2){
        		bucketName.add(bucket.getName());
        	}
        }  
    	for(int i=0;i<bucketName.size();i++){
    		SearchResult temp=new SearchResult();
    		S3Object object = s3.getObject(new GetObjectRequest(bucketName.get(i), "id"));
    		temp.setImgpath(displayTextInputStream(object.getObjectContent()).get(0));
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "img"));
    		temp.setImgpath(displayTextInputStream(object.getObjectContent()).get(0));
    		temp.setName(bucketName.get(i).split("-")[1]);
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "price"));
    		temp.setPrice(displayTextInputStream(object.getObjectContent()).get(0));
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "info"));
    		temp.setInfo(displayTextInputStream(object.getObjectContent()).get(0));
    		ret.add(temp);
    	}
    	return ret;
    }
    
    public static int minld(String input, String target){
		int m=input.length();
		int n=target.length();
		int cost=0;
		char inp;
		char tar;
		int[][] d=new int[m+1][n+1];
		for(int i=0;i<m+1;i++){
			d[i][0]=i;
		}
		for(int i=0;i<n+1;i++){
			d[0][i]=i;
		}
		for(int i=1;i<m+1;i++){
			inp=input.charAt(i-1);
			for(int j=1;j<n+1;j++){
				tar=target.charAt(j-1);
				if(inp==tar){
					cost=0;
				}else{
					cost=1;
				}
				d[i][j]=Math.min(Math.min(d[i-1][j]+1, d[i][j-1]+1), d[i-1][j-1]+cost);
			}
		}
		return d[m][n];
	}
}

