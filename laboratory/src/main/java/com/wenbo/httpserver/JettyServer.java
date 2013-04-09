package com.wenbo.httpserver;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

public class JettyServer {

	public static void main(String[] args){
		//连接池  
		try {
			Server server = new Server();
			SelectChannelConnector connector = new SelectChannelConnector();  
			connector.setPort(8081);  
	        connector.setMaxIdleTime(30000);  
	        connector.setRequestHeaderSize(8192);  
	        QueuedThreadPool threadPool =  new QueuedThreadPool(100);  
	        threadPool.setName("jetty-http");  
	        connector.setThreadPool(threadPool);  
		    server.setConnectors(new Connector[] { connector });
		    server.setHandler(new HessianMyHandler());
		    server.start();
		    server.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
