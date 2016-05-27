package com.service.impl;

import javax.jws.WebService;

import com.data.User;
import com.service.Verify;

@WebService(endpointInterface = "com.service.Verify", serviceName = "VerifyService")
public class VerifyImpl implements Verify
{

	public User verfiyUser(User user) 
	{
		//TODO:S3
		String verify_username = user.getUsername();
		String verify_password = user.getPassword();
		int i = 1;
		if(i == 1)
		{
			// correct;
			String get_tel = "999-999-9999";
			user.setConfirmpassword(verify_password);
			user.setTel(get_tel);
			return user;
		}
		// else
		return null;
	}

}
