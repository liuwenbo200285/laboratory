package com.wenbo.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Copyright (c) 2014, hunantv.com All Rights Reserved.
 * <p/>
 * User: jeffreywu  MailTo:jeffreywu@sohu-inc.com
 * Date: 15/5/25
 * Time: AM11:33
 * 从Kafka里面读取数据
 */
public class DemoKafkaQueueInput{
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoKafkaQueueInput.class);
    private static final String zkConnect = "INPUT.KAFKA.ZK";
    private static final String topic = "INPUT.KAFAKA.TOPIC";
    private static final String partition = "INPUT.KAFKA.PARTITION";
    private static final String timeout = "INPUT.KAFKA.TIMEOUT";
    private static final String borker = "INPUT.KAFKA.BROKER";
    private static final String bulkSize = "INPUT.KAFKA.BULKSIZE";
    private static final String borkerList = "INPUT.KAFKA.BORKERLIST";
    private static final String groupId = "INPUT.KAFKA.GROUPID";
    private String pZkConnect = null;
    private String pBokerList = null;
    private String pGroupId = null;
    private String pTopic = null;
    private int pPartition;
    private int pTimeout;
    private int pBorker;
    private int pBulkSize;
    private ConsumerConnector consumerConnector = null;
    
    public static void main(String[] args){
    	DemoKafkaQueueInput input = new DemoKafkaQueueInput();
    	input.init();
    	input.doStart();
    }

 
    public void doStart() {
        //启动Kafka联接
        Properties properties = new Properties();
        properties.put("zookeeper.connect", this.pZkConnect);
        properties.put("auto.commit.enable", "true");
        properties.put("request.timeout.ms","10000");
        properties.put("auto.commit.interval.ms","10000");
        properties.put("request.timeout.ms", 15000);
//        properties.put("metadata.broker.list", this.pBokerList);
        properties.put("group.id", this.pGroupId);
        properties.put("auto.offset.reset","smallest");
        ConsumerConfig consumerConfig = new ConsumerConfig(properties);
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);

        //联接Kafka对应的Topic
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(this.pTopic, this.pPartition);
        Map<String, List<KafkaStream<byte[], byte[]>>> topicMessageStreams = consumerConnector.createMessageStreams(map);
        List<KafkaStream<byte[], byte[]>> partitions = topicMessageStreams.get(this.pTopic);
        if (partitions == null || partitions.size() <= 0) {
            LOGGER.info("empty no partitions");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Executor executor = Executors.newFixedThreadPool(partitions.size());
        for (KafkaStream partit : partitions) {
            executor.execute(new ConsumerRunable(consumerConnector, partit, this.pBulkSize));
        }
    }

    public void init() {
        //将析参数
        this.pZkConnect = "127.0.0.1:2181";
        this.pTopic = "my-replicated-topic";
        this.pPartition = 3;
        this.pTimeout = 10000;
        this.pBorker = 6;
        this.pBulkSize = 10;
        this.pBokerList = "10.100.1.51:9092,10.100.1.52:9092,10.100.1.53:9092,10.100.1.54:9092,10.100.1.8:9092,10.100.1.9:9092";
        this.pGroupId = "test2";
    }

    /**
     * 消费端
     */
    static class ConsumerRunable implements Runnable {
        private ConsumerConnector consumerConnector;
        private KafkaStream kafkaStream;
        private int bulkSize;


        public ConsumerRunable(ConsumerConnector consumerConnector,KafkaStream kafkaStream, int bulkSize) {
            this.consumerConnector = consumerConnector;
            this.kafkaStream = kafkaStream;
            this.bulkSize = bulkSize;
        }

        @Override
        public void run() {
            ConsumerIterator<byte[], byte[]> iterator = kafkaStream.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                MessageAndMetadata<byte[], byte[]> next = iterator.next();
                String message = new String(next.message());
                System.out.println("=====>"+message);
                try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
    }
}
