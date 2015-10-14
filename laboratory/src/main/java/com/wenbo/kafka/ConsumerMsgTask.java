package com.wenbo.kafka;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ajax.JSON;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;

import com.alibaba.fastjson.JSONObject;

public class ConsumerMsgTask implements Runnable {
    private KafkaStream<byte[], byte[]> m_stream;
    private int m_threadNumber;
    private Client client;
    private ConsumerConnector consumer;
    private Boolean flag = false;
    private String topic;
    private String zookeeper;
    private String groupId;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss"); 
 
    public ConsumerMsgTask(Client client,String zookeeper,String topic,String groupId) {
        this.client = client;
        this.zookeeper = zookeeper;
        this.topic = topic;
        this.groupId = groupId;
    }
 
    public void run() {
    	BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId,
                                   BulkRequest request){
            	flag = false;
            } 
            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  BulkResponse response) {
            	System.out.println("afterBulk:"+response.getItems());
            	consumer.commitOffsets();
            	flag = true;
            } 
            @Override
            public void afterBulk(long executionId,
                                  BulkRequest request,
                                  Throwable failure) {
            	failure.printStackTrace();
            	consumer.shutdown();
            	initKafkaStream();
            } 
        };
    	BulkProcessor bulkProcessor = BulkProcessor.builder(client,listener)
    	.setBulkActions(20) 
        .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB)) 
        .setFlushInterval(TimeValue.timeValueSeconds(30)) 
        .setConcurrentRequests(1) 
        .build();
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        try {
        	while (it.hasNext() && flag.booleanValue()){
            	MessageAndMetadata<byte[], byte[]> item = it.next();
            	System.out.println("threadNumber:"+m_threadNumber+",offset " + item.offset() + ",partition: "+item.partition());
            	try {
            		String message = new String(item.message(),"UTF-8");
                	String [] messages = StringUtils.split(message,"|");
    				String info = messages[0];
    				String [] infos = StringUtils.split(info," ");
    				String jsonData = null;
    				if(messages.length > 5){
    					for(int n = 4;n < messages.length; n++){
    						jsonData +=messages[n];
    					}
    				}else{
    					jsonData = messages[4];
    				}
    				JSONObject object = null;
                	try{
                		object = JSONObject.parseObject(jsonData);
                	}catch(Exception e){
                		e.printStackTrace();
                		continue;
                	}
                	object.put("apphost",infos[infos.length-1]);
    				object.put("apptime",messages[1]);
    				object.put("appname",messages[2]);
    				object.put("loglevel",messages[3]);
    				LocalDateTime localDateTime = LocalDateTime.parse(messages[1],DATE_TIME_FORMATTER);
    				Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();  
    				Date date = Date.from(instant);
    				object.put("@timestamp",date);
    				String index = messages[2]+"-"+localDateTime.getYear()+"."+(localDateTime.getMonthValue()>9?localDateTime.getMonthValue():"0"+localDateTime.getMonthValue())
    						+"."+(localDateTime.getDayOfMonth()>9?localDateTime.getDayOfMonth():"0"+localDateTime.getDayOfMonth());
                	bulkProcessor.add(new IndexRequest(index,messages[2]).source(object.toJSONString()));
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void initKafkaStream(){
    	consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(zookeeper,groupId));
    	Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer
				.createMessageStreams(topicCountMap);
		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
		m_stream = streams.get(0);
    }
    
    private  ConsumerConfig createConsumerConfig(String a_zookeeper,
			String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("auto.offset.reset", "smallest");
		props.put("zookeeper.session.timeout.ms", "400");
		props.put("zookeeper.sync.time.ms", "200");
		props.put("auto.commit.interval.ms", "1000");
		props.put("auto.commit.enable", "false");
		props.put("num.consumer.fetchers", "2");
		return new ConsumerConfig(props);
	}
}
