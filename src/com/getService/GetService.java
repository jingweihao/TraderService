package com.getService;

import java.util.*;

import javax.jws.WebService;

import com.data.Sales;
import com.data.SearchResult;

@WebService
public interface GetService 
{
	// back up the TraderBalancer
	public ArrayList<SearchResult> SearchService(String keyword);
	
	public ArrayList<Sales> SalesService(String username);
}