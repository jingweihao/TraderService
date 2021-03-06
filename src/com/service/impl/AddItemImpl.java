package com.service.impl;

import java.io.IOException;

import javax.jws.WebService;

import com.S3Connection.S3Instance2;
import com.data.Sales;
import com.service.AddItem;

@WebService(endpointInterface = "com.service.AddItem", serviceName = "AddItemService")
public class AddItemImpl implements AddItem
{
	public String addItem(Sales sales) 
	{
		//S3	
		S3Instance2 s3=S3Instance2.getInstance();
		try {
			return s3.createItem(sales);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "fail to create item...";
	}
	
}
