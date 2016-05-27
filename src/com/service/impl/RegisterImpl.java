package com.service.impl;

import javax.jws.WebService;

import com.data.User;
import com.service.Register;

@WebService(endpointInterface = "com.service.Register", serviceName = "RegisterService")
public class RegisterImpl implements Register
{

	public boolean registerUser(User user) 
	{
        int i = 1;
        if(i == 0)
        {
        	// if user name already exists!
        	return false;
        }
        
		return true;
	}

}
