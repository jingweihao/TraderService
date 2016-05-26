package com.service.impl;

import javax.jws.WebService;
import com.service.DeleteItem;

@WebService(endpointInterface = "com.service.DeleteItem", serviceName = "DeleteItemService")
public class DeleteItemImpl implements DeleteItem
{
	public boolean deleteItem(String itemid) 
	{
        //TODO: S3
		return true;
	}

}
