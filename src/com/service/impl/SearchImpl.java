package com.service.impl;

import java.io.IOException;
import java.util.*;

import javax.jws.WebService;

import com.S3Connection.S3Instance2;
import com.data.SearchResult;
import com.service.Search;

@WebService(endpointInterface = "com.service.Search", serviceName = "SearchService")
public class SearchImpl implements Search
{
     public ArrayList<SearchResult> doSearch(String keyword)
     {
    	//S3
    	System.out.println("doSearch Service~~for " + keyword);    	 
    	S3Instance2 s3=S3Instance2.getInstance();
    	
    	ArrayList<SearchResult> searchlist = new ArrayList<SearchResult>();
// 		for(int i = 0; i < 50; i++)
// 		{
// 			searchlist.add(new SearchResult(String.valueOf(i),"anteater.png", "Anteater" + i, "$1000", "The anteaters are more closely related to the sloths than they are to any other group of mammals. Their next closest relations are armadillos."));
// 		}
 		try {
 			searchlist=s3.search(keyword);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		
 		return searchlist;
     }

}
