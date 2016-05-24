package com.service.impl;

import java.util.*;
import javax.jws.WebService;

import com.data.Sales;
import com.service.PersonalSales;

@WebService(endpointInterface = "com.service.PersonalSales", serviceName = "PersonalSalesService")
public class PersonalSalesImpl implements PersonalSales
{
	
	public ArrayList<Sales> getSales(String username) 
	{
		// TODO: S3
		ArrayList<Sales> list = new ArrayList<Sales>();
		for(int i = 0; i < 50; i++)
		{
			list.add(new Sales(String.valueOf(i), "anteater.png", "Anteater" + i, "$20000", "The anteaters are more closely related to the sloths than they are to any other group of mammals. Their next closest relations are armadillos."));
		}
		
		System.out.println(username + " getSales Service~~");
		return list;

	}

}
