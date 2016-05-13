package com.service.impl;

import java.util.*;

import javax.jws.WebService;
import com.data.SearchResult;
import com.service.Search;

@WebService(endpointInterface = "com.service.Search", serviceName = "SearchService")
public class SearchImpl implements Search
{
     public ArrayList<SearchResult> doSearch(String keyword)
     {
    	// TODO: S3
    	ArrayList<SearchResult> searchlist = new ArrayList<SearchResult>();
 		for(int i = 0; i < 50; i++)
 		{
 			searchlist.add(new SearchResult("anteater.png", "Anteater", "$1000", "The anteaters are more closely related to the sloths than they are to any other group of mammals. Their next closest relations are armadillos."));
 		}
 		System.out.println("doSearch Service~~for " + keyword);
 		return searchlist;
     }

}
