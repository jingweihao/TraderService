package com.service.impl;

import javax.jws.WebService;

import com.service.Hello;

@WebService(endpointInterface = "com.service.Hello", serviceName = "HelloService")
public class HelloImpl implements Hello
{
	public String sayHello(String person)
	{
		System.out.println("Hello Service~~");
		return "Hello " + person;		
	}
	
}
