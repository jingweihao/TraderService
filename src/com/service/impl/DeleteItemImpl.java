package com.service.impl;

import java.io.IOException;

import javax.jws.WebService;

import com.S3Connection.S3Instance;
import com.service.DeleteItem;

@WebService(endpointInterface = "com.service.DeleteItem", serviceName = "DeleteItemService")
public class DeleteItemImpl implements DeleteItem
{
	public boolean deleteItem(String category, String itemname, String itemid) 
	{
        //S3
		boolean ret=false;
		S3Instance s3=S3Instance.getInstance();
		try {
			ret = s3.delete(itemid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

}
