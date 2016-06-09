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
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.data.Sales;
import com.data.SearchResult;
import com.data.User;

public class S3Instance {
	
	private AmazonS3 s3;
	
	private S3Instance()
	{
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
//            credentials = new BasicAWSCredentials();
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
    
    public ArrayList<String> verifyPerson(User user) throws IOException{
    	String[] bucketname=new String[3];
    	ArrayList<String> ret=new ArrayList<String>();
        for (Bucket bucket : s3.listBuckets()) {
        	bucketname = bucket.getName().split("-");
        	if(bucketname.length == 3 && bucketname[0].equals("seller")){
        		S3Object object = s3.getObject(new GetObjectRequest(bucket.getName(), "confirmpassword"));
        		String temp=displayTextInputStream(object.getObjectContent()).get(0);
	        	if(bucketname[1].equals(user.getUsername().toLowerCase()) && temp.equals(user.getPassword())){
	        		ret.add(bucketname[2]);
	        		ret.add(temp);
	        		return ret;
	        	}
        	}
        }  
    	return ret;
    }
    
    public boolean createPerson(User user) throws IOException{
    	String[] bucketname=new String[3];
        for (Bucket bucket : s3.listBuckets()) {
        	bucketname = bucket.getName().split("-");
        	if(bucketname.length == 3 && bucketname[0].equals("seller")){
	        	if(bucketname[1].equals(user.getUsername().toLowerCase())){
	        		return false;
	        	}
        	}
        }  
        String personbucket = "seller-"+user.getUsername().toLowerCase()+"-"+user.getTel();
        s3.createBucket(personbucket);
        s3.putObject(new PutObjectRequest(personbucket, "password", createSampleFile(user.getPassword())));
        s3.putObject(new PutObjectRequest(personbucket, "confirmpassword", createSampleFile(user.getConfirmpassword())));
        return true;
    }
    
    public String createItem(Sales detail) throws IOException{
    	String itemId = UUID.randomUUID().toString().replace("-", "");
        String bucketname = detail.getCategory().toLowerCase()+"-"+detail.getName().toLowerCase()+"-"+itemId;
        String name = detail.getSellername().toLowerCase();
        try {
        	s3.createBucket(bucketname);
            System.out.println("Uploading a new object to S3 from a file\n");
            s3.putObject(new PutObjectRequest(bucketname, "id", createSampleFile(itemId)));
            s3.putObject(new PutObjectRequest(bucketname, "imgpath", createSampleFile(detail.getImgpath())));
            s3.putObject(new PutObjectRequest(bucketname, "price", createSampleFile(detail.getPrice())));
            s3.putObject(new PutObjectRequest(bucketname, "info", createSampleFile(detail.getInfo())));
            s3.putObject(new PutObjectRequest(bucketname, "sellername", createSampleFile(detail.getSellername())));
            s3.putObject(new PutObjectRequest(bucketname, "sellertel", createSampleFile(detail.getSellertel())));
        	String[] bucketName=new String[3];
            for (Bucket bucket : s3.listBuckets()) {
            	bucketName = bucket.getName().split("-");
            	if(bucketName[0].equals("seller")){
    	        	if(bucketName[1].equals(name)){
    	        		s3.putObject(new PutObjectRequest(bucket.getName(), itemId, createSampleFile("")));
    	        	}
            	}
            }
            return itemId;
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
        return "fail to create item...";
    }
    
    public boolean delete(String itemid) throws IOException{
    	boolean success1=false;
    	boolean success2=false;
    	String name="";
    	String[] bucketname=new String[3];
        for (Bucket bucket : s3.listBuckets()) {
        	bucketname = bucket.getName().split("-");
        	if(bucketname.length == 3 && !bucketname[0].equals("seller") && success1==false){
	        	if(bucketname[2].equals(itemid)){
	        		S3Object object = s3.getObject(new GetObjectRequest(bucket.getName(), "sellername"));
	        		name = displayTextInputStream(object.getObjectContent()).get(0);
	        		s3.deleteObject(bucket.getName(), "id");
	        		s3.deleteObject(bucket.getName(), "imgpath");
	        		s3.deleteObject(bucket.getName(), "info");
	        		s3.deleteObject(bucket.getName(), "price");
	        		s3.deleteObject(bucket.getName(), "sellername");
	        		s3.deleteObject(bucket.getName(), "sellertel");
	        		s3.deleteBucket(bucket.getName());
	        		success1=true;
	        	}
        	}else{
        		if(bucketname.length == 3 && success2==false){
	        		if(bucketname[1].equals(name)){
	        			s3.deleteObject(new DeleteObjectRequest(bucket.getName(),itemid));
	        			success2=true;
	        		}
        		}
        	}
        }  	
    	return success1 && success2;
    }
    
    public ArrayList<SearchResult> search(String str) throws IOException{
    	ArrayList<SearchResult> ret=new ArrayList<SearchResult>();
    	String[] bucketname=new String[3];
    	ArrayList<String> bucketName=new ArrayList<String>();
        for (Bucket bucket : s3.listBuckets()) {
        	bucketname = bucket.getName().split("-");
        	if(bucketname.length == 3 && !bucketname[0].equals("seller")){
	        	int diff=Math.abs(str.length()-bucketname[0].length());
	        	if(minld(str,bucketname[0])-diff<Math.min(str.length(), bucketname[0].length())/2){
	        		bucketName.add(bucket.getName());
	        	}else{
		        	diff=Math.abs(str.length()-bucketname[1].length());
		        	if(minld(str,bucketname[1])-diff<Math.min(str.length(), bucketname[1].length())/2){
		        		bucketName.add(bucket.getName());
		        	}
	        	}
        	}
        }  
    	for(int i=0;i<bucketName.size();i++){
    		SearchResult temp=new SearchResult();
    		S3Object object = s3.getObject(new GetObjectRequest(bucketName.get(i), "id"));
    		temp.setImgpath(displayTextInputStream(object.getObjectContent()).get(0));
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "imgpath"));
    		temp.setImgpath(displayTextInputStream(object.getObjectContent()).get(0));
    		temp.setName(bucketName.get(i).split("-")[1]);
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "price"));
    		temp.setPrice(displayTextInputStream(object.getObjectContent()).get(0));
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "info"));
    		temp.setInfo(displayTextInputStream(object.getObjectContent()).get(0));
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "sellername"));
    		temp.setSellername(displayTextInputStream(object.getObjectContent()).get(0));
    		object = s3.getObject(new GetObjectRequest(bucketName.get(i), "sellertel"));
    		temp.setSellertel(displayTextInputStream(object.getObjectContent()).get(0));
    		ret.add(temp);
    	}
    	return ret;
    }
    
    public ArrayList<Sales> searchPerson(String username) throws IOException{
    	ArrayList<Sales> ret = new ArrayList<Sales>();    	
    	String[] bucketname=new String[3];
    	ArrayList<String> rec = new ArrayList<String>();
        for (Bucket bucket : s3.listBuckets()) {
        	bucketname = bucket.getName().split("-");
        	if(bucketname.length == 3 && bucketname[0].equals("seller")){
	        	if(bucketname[1].equals(username.toLowerCase())){
	                ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
	                        .withBucketName(bucket.getName())
	                        .withPrefix(""));
	                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	                    rec.add(objectSummary.getKey());           
	                }
	                break;
	        	}
        	}
        }
        String[] bucketname2=new String[3];
        for(Bucket bucket2 : s3.listBuckets()){
        	bucketname2 = bucket2.getName().split("-");
        	for(int i=0;i<rec.size();i++){
            	if(bucketname2.length == 3 && bucketname2[2].equals(rec.get(i))){
            		Sales temp=new Sales();
            		S3Object object = s3.getObject(new GetObjectRequest(bucket2.getName(), "id"));
            		temp.setId(bucketname2[2]);
            		object = s3.getObject(new GetObjectRequest(bucket2.getName(), "imgpath"));
            		temp.setImgpath(displayTextInputStream(object.getObjectContent()).get(0));
            		temp.setName(bucketname2[1]);
            		object = s3.getObject(new GetObjectRequest(bucket2.getName(), "price"));
            		temp.setPrice(displayTextInputStream(object.getObjectContent()).get(0));
            		object = s3.getObject(new GetObjectRequest(bucket2.getName(), "info"));
            		temp.setInfo(displayTextInputStream(object.getObjectContent()).get(0));
            		temp.setCategory(bucketname2[0]);
            		temp.setSellername(username);
            		object = s3.getObject(new GetObjectRequest(bucket2.getName(), "sellertel"));
            		temp.setSellertel(displayTextInputStream(object.getObjectContent()).get(0));
            		ret.add(temp);
            	}
        	}
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

