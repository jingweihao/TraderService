package com.balancer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

import org.apache.cxf.clustering.AbstractStaticFailoverStrategy;

/**
 * load balance strategy based on a randomized walk through the
 * static cluster represented by multiple endpoints associated
 * with the same service instance.
 *
 */
public class LCLoadBalanceStrategy extends AbstractStaticFailoverStrategy {
    
    private Random random;
    private String type;
    
    /**
     * Constructor.
     */
    public LCLoadBalanceStrategy() {
        random = new Random();
    }
    
    public void setType(String type)
    {
    	this.type = type;
    }
    

    /**
     * Get next alternate endpoint.
     * 
     * @param alternates non-empty List of alternate endpoints 
     * @return
     */
    protected <T> T getNextAlternate(List<T> alternates) 
    {
    	this.type = alternates.get(0).toString().substring(36,alternates.get(0).toString().length());
//    	System.out.println("alternate address:======");
//    	for(int i = 0; i < alternates.size(); i++)
//    	{
//    		System.out.println(alternates.get(i));
//    	}
    	
    	
    	// alternates current good address or initial address
    	// check map
    	
    	getlistsingleton allAddress = getlistsingleton.getInstance();
    	
    	System.out.println("current record connections:=======");
    	allAddress.printconn();
    	
    	PriorityQueue<Servers> queue = allAddress.getQueue();
    	Servers nextserver = queue.remove();
    	String address = nextserver.getAddress();
    	int connections = nextserver.getConnections();
    	System.out.println("min connections: "+ connections);
    	System.out.println("min address: "+ address);
    	
    	List<Servers> notexist = new ArrayList<Servers>();
    	
    	while(!alternates.contains(address+"/"+type))
    	{
    		System.out.println("not exist: " + address);
    		notexist.add(nextserver);
    		nextserver = queue.remove();
    		address = nextserver.getAddress();	
    		
    		connections = nextserver.getConnections();
        	System.out.println("new min connections: "+ connections);
        	
    		System.out.println("new min address:" + address);
    	}
    	
    	System.out.println("selected end: "+ address);
    	for(int i = 0; i < notexist.size(); i++)
    	{
    		queue.add(notexist.get(i));
    	}
    	
    	allAddress.setQueue(queue);
    	allAddress.addconnection(address);
    	System.out.println("after add:===========");
    	allAddress.printqueue();
    	
//    	System.out.println("selected server address:===========");
//    	System.out.println(address);
//    	System.out.println();
    	    	
//    	System.out.println("now record connections");
//    	allAddress.printconn();
//    	System.out.println();
    	
    	String newaddress = address + "/"+ type;
    	System.out.println("selected entire address with type: ");
    	System.out.println(newaddress);
    	System.out.println();
    	
//    	alternates.remove(newaddress);
    	return (T)newaddress;
//        return alternates.get(random.nextInt(alternates.size()));
    }
    
    
    
    

    
}
