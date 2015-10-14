package com.wenbo.kafka;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ConsumerDemo {

	private static ExecutorService executor;
	private static Client client = new TransportClient(ImmutableSettings
			.settingsBuilder().put("cluster.name", "elasticsearch").build())
			.addTransportAddress(new InetSocketTransportAddress("10.100.3.29", 9300));

	public static void main(String[] arg) {
		executor = Executors.newFixedThreadPool(2);
		executor.submit(new ConsumerMsgTask(client,"10.100.3.32:2181,10.100.3.33:2181","passport-app","log"));
	}

}
