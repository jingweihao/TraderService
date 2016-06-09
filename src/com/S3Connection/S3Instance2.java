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

public class S3Instance2 {
	
	private AmazonS3 s3;
	private static String itembucket = "itemcollection1";
	private static String personbucket = "personcollection1";
	
	private S3Instance2()
	{
		AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
//        	credentials = new BasicAWSCredentials("");
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
	
    private static final S3Instance2 s3instance2 = new S3Instance2();
	
	public static S3Instance2 getInstance()
	{
		System.out.println("Get Instance222222~~~~~~~~");
		return s3instance2;
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
    
    public ArrayList<String> verifyPerson(User user) throws IOException{
    	String personname = user.getUsername().toLowerCase();
    	ArrayList<String> temp = new ArrayList<String>();
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(personbucket)
                .withPrefix(personname));
        if(objectListing.getObjectSummaries().size()==0){
        	return temp;
        }
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
        	S3Object object = s3.getObject(new GetObjectRequest(personbucket, objectSummary.getKey()));
        	System.out.println(objectSummary.getKey());
        	ArrayList<String> t = displayTextInputStream(object.getObjectContent());
    		if(user.getPassword().equals(t.get(1))){
    			temp.add(objectSummary.getKey().split("-")[1]);
    			temp.add(user.getPassword());
    		}
    		object.close();
        }
    	return temp;
    }
    
    public boolean createPerson(User user) throws IOException{
    	String personname = user.getUsername().toLowerCase()+"-"+user.getTel();
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(personbucket)
                .withPrefix(user.getUsername().toLowerCase()));
        if(objectListing.getObjectSummaries().size()!=0){
        	return false;
        }
    	ArrayList<String> temp = new ArrayList<String>();
    	temp.add(user.getPassword());
    	temp.add(user.getConfirmpassword());	
    	s3.putObject(new PutObjectRequest(personbucket, personname, createSampleFile(temp)));
    	return true;
    }
	
	public String createItem(Sales detail) throws IOException{
		String itemId = UUID.randomUUID().toString().replace("-", "");
		String objname = detail.getCategory().toLowerCase()+"-"+detail.getName()+"-"+itemId;
		ArrayList<String> content = new ArrayList<String>();
		content.add(itemId);
		content.add(detail.getPrice());
		content.add(detail.getInfo());
		content.add(detail.getImgpath());
		content.add(detail.getSellername().toLowerCase());
		content.add(detail.getSellertel());
		s3.putObject(new PutObjectRequest(itembucket, objname, createSampleFile(content)));
    	String personname = detail.getSellername().toLowerCase()+"-"+detail.getSellertel();
    	System.out.println(personname);
    	S3Object object = s3.getObject(new GetObjectRequest(personbucket, personname));
		ArrayList<String> temp = displayTextInputStream(object.getObjectContent());
		temp.add(objname);
    	s3.putObject(new PutObjectRequest(personbucket, personname, createSampleFile(temp)));
		return itemId;
	}
	
	public boolean delete(String category, String itemname, String itemid, String personname) throws IOException{
    	String name1 = category.toLowerCase()+"-"+itemname+"-"+itemid;
    	s3.deleteObject(itembucket, name1);
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(personbucket)
                .withPrefix(personname));
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
        	String name2 = objectSummary.getKey();
        	S3Object object = s3.getObject(new GetObjectRequest(personbucket, name2));
    		ArrayList<String> temp = displayTextInputStream(object.getObjectContent());
    		System.out.println(temp.get(2));
    		for(int i=0; i<temp.size(); i++){
    			if(temp.get(i).equals(name1)){
    				temp.remove(i);
    			}
    		}
    		s3.putObject(new PutObjectRequest(personbucket, name2, createSampleFile(temp)));
        }
    	return true;
	 }
	    
    public ArrayList<SearchResult> search(String str) throws IOException{
    	System.out.println("llllllllllllllllllllllokkkkkkkk");
    	Boolean found = false;
    	ArrayList<SearchResult> ret=new ArrayList<SearchResult>();
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(itembucket)
                .withPrefix(str));
        if(objectListing.getObjectSummaries().size()!=0){
        	found = true;
        }
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
        	String key = objectSummary.getKey();
        	System.out.println("fffffffffffff"+key);
        	S3Object object = s3.getObject(new GetObjectRequest(itembucket, key));
    		ArrayList<String> temp = displayTextInputStream(object.getObjectContent());
    		SearchResult col = new SearchResult();
    		col.setId(temp.get(0));
    		col.setPrice(temp.get(1));
    		col.setInfo(temp.get(2));
    		col.setImgpath(temp.get(3));
    		col.setSellername(temp.get(4));
    		col.setSellertel(temp.get(5));
    		col.setName(key.split("-")[1]);
    		ret.add(col);
    		object.close();
        }
        if(!found){
        	objectListing = s3.listObjects(new ListObjectsRequest()
	                .withBucketName(itembucket)
	                .withPrefix(""));
        	ArrayList<String> match = new ArrayList<String>();
        	for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
 	        	String name = objectSummary.getKey();
 	        	String[] bucketname = name.split("-");	        	
	        	if(bucketname.length == 3){
	        		int diff=Math.abs(str.length()-bucketname[0].length());
 		        	if(minld(str,bucketname[0])-diff<Math.min(str.length(), bucketname[0].length())/2){
 		        		match.add(name);
 		        	}else{
 			        	diff=Math.abs(str.length()-bucketname[1].length());
 			        	if(minld(str,bucketname[1])-diff<Math.min(str.length(), bucketname[1].length())/2){
 			        		match.add(name);
 			        	}
 		        	}
	        	}
        	}
        	for(int i=0; i<match.size(); i++){
        		S3Object object2 = s3.getObject(new GetObjectRequest(itembucket, match.get(i)));
	    		ArrayList<String> temp2 = displayTextInputStream(object2.getObjectContent());
	    		SearchResult col2 = new SearchResult();
	    		col2.setId(temp2.get(0));
	    		col2.setPrice(temp2.get(1));
	    		col2.setInfo(temp2.get(2));
	    		col2.setImgpath(temp2.get(3));
	    		col2.setSellername(temp2.get(4));
	    		col2.setSellertel(temp2.get(5));
	    		col2.setName(match.get(i).split("-")[1]);
	    		ret.add(col2);
        	}
        }  	
    	return ret;
    }
    
    public ArrayList<Sales> searchPerson(String username) throws IOException{
    	ArrayList<Sales> ret = new ArrayList<Sales>();
    	ArrayList<String> temp = new ArrayList<String>();
    	ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(personbucket)
                .withPrefix(username));
        if(objectListing.getObjectSummaries().size()==0){
        	return ret;
        }
        for(S3ObjectSummary objectSummary : objectListing.getObjectSummaries()){
        	String name = objectSummary.getKey();
        	S3Object object = s3.getObject(new GetObjectRequest(personbucket, name));
    		temp = displayTextInputStream(object.getObjectContent());
        }
        for(int i=2; i<temp.size(); i++){
        	ObjectListing objectListing2 = s3.listObjects(new ListObjectsRequest()
                    .withBucketName(itembucket)
                    .withPrefix(temp.get(i)));
        	for(S3ObjectSummary objectSummary2 : objectListing2.getObjectSummaries()){
	        	String name2 = objectSummary2.getKey();
	        	S3Object object2 = s3.getObject(new GetObjectRequest(itembucket, name2));
	    		ArrayList<String> temp2 = displayTextInputStream(object2.getObjectContent());
	    		Sales putin = new Sales();
	    		putin.setId(temp2.get(0));
	    		putin.setPrice(temp2.get(1));
	    		putin.setInfo(temp2.get(2));
	    		putin.setImgpath(temp2.get(3));
	    		putin.setSellername(temp2.get(4));
	    		putin.setSellertel(temp2.get(5));
	    		putin.setCategory(name2.split("-")[0]);
	    		putin.setName(name2.split("-")[1]);
	    		ret.add(putin);
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
