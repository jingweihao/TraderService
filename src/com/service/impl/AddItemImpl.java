package com.service.impl;

import javax.jws.WebService;

import com.data.Sales;
import com.service.AddItem;

@WebService(endpointInterface = "com.service.AddItem", serviceName = "AddItemService")
public class AddItemImpl implements AddItem
{
	public String addItem(Sales sales) 
	{
		// TODO: S3		
		return "1000";
	}
	
}
