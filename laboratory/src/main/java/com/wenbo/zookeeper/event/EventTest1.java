package com.wenbo.zookeeper.event;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class EventTest1 {
	
	private static final int CLIENT_PORT = 2181;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// ����һ���������������
		 ZooKeeper zk = new ZooKeeper("localhost:" + CLIENT_PORT, 
		        1000*60, new Watcher() { 
		            // ������б��������¼�
		            public void process(WatchedEvent event) { 
		                System.out.println("�Ѿ�������" + event.getType() + "�¼���"); 
		            } 
		        }); 
		 Stat stat = zk.exists("/testRootPath",true);
		 if(stat != null){
			 zk.delete("/testRootPath",-1);
		 }else{
			 zk.create("/testRootPath","asdf".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		 }
		 Thread.sleep(1000*60*60);
		 // �ر�����
		 zk.close();

	}

}
