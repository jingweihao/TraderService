package com.service.impl;

import javax.jws.WebService;

import com.service.Hello1;

@WebService(endpointInterface = "com.service.Hello1", serviceName = "Hello1Service")
public class Hello1Impl implements Hello1
{
	
	public String sayHello1(String person) 
	{
		System.out.println("Hello1 Service~~");
		return "Hello1 " + person;		
	}

	public String sayHello2(String person) 
	{
		System.out.println("Hello2 Service~~");
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "Hello2 " + person + "sleep  4000ms";		
	}
	
}
