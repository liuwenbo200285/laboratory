package com.wenbo.kafka;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

public class ConsumerMsgTask implements Runnable {
    private KafkaStream m_stream;
    private int m_threadNumber;
 
    public ConsumerMsgTask(KafkaStream stream, int threadNumber) {
        m_threadNumber = threadNumber;
        m_stream = stream;
    }
 
    public void run() {
        ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
        while (it.hasNext()){
        	MessageAndMetadata<byte[], byte[]> item = it.next();
        	System.out.println("Thread " + m_threadNumber + ": "+ new String(item.message()));
        	System.out.println("Thread " + m_threadNumber + " offset: "+ item.offset());
        	System.out.println("Thread " + m_threadNumber + " partition: "+ item.partition());
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
