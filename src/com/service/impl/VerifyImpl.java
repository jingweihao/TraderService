package com.service.impl;

import javax.jws.WebService;

import com.S3Connection.S3Instance2;
import com.data.User;
import com.service.Verify;

import java.io.IOException;
import java.util.*;

@WebService(endpointInterface = "com.service.Verify", serviceName = "VerifyService")
public class VerifyImpl implements Verify
{

	public User verfiyUser(User user) 
	{
		//S3
		S3Instance2 s3 = S3Instance2.getInstance();
		ArrayList<String> result=new ArrayList<String>();
		try {
			result=s3.verifyPerson(user);
			if(result.size()!=0)
			{
				// correct;
				user.setConfirmpassword(result.get(1));
				user.setTel(result.get(0));
				return user;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// else
		return null;
	}

}
