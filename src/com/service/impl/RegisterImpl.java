package com.service.impl;

import java.io.IOException;

import javax.jws.WebService;

import com.S3Connection.S3Instance2;
import com.data.User;
import com.service.Register;

@WebService(endpointInterface = "com.service.Register", serviceName = "RegisterService")
public class RegisterImpl implements Register
{

	public boolean registerUser(User user) 
	{
		//S3
        S3Instance2 s3=S3Instance2.getInstance();
        try {
			return s3.createPerson(user);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
	}

}
