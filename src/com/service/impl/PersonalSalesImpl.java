package com.service.impl;

import java.io.IOException;
import java.util.*;
import javax.jws.WebService;

import com.S3Connection.S3Instance2;
import com.data.Sales;
import com.service.PersonalSales;

@WebService(endpointInterface = "com.service.PersonalSales", serviceName = "PersonalSalesService")
public class PersonalSalesImpl implements PersonalSales
{
	
	public ArrayList<Sales> getSales(String username) 
	{
		// TODO: S3
		S3Instance2 s3 = S3Instance2.getInstance();
		ArrayList<Sales> list = new ArrayList<Sales>();
		try {
			list = s3.searchPerson(username);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(list.size());
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i));
		}
		System.out.println(username + " getSales Service~~");
		return list;

	}

}
