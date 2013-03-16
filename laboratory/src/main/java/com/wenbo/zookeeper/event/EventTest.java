package com.wenbo.zookeeper.event;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class EventTest {
	
	private static final int CLIENT_PORT = 2181;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// ����һ���������������
		 ZooKeeper zk = new ZooKeeper("localhost:" + CLIENT_PORT, 
		        1000*2, new Watcher() { 
		            // ������б��������¼�
		            public void process(WatchedEvent event) { 
		                System.out.println("�Ѿ�������" + event.getType() + "�¼���"); 
		            } 
		        }); 
		 // ����һ��Ŀ¼�ڵ�
		 zk.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,
		   CreateMode.PERSISTENT); 
		 // ����һ����Ŀ¼�ڵ�
		 zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),
		   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		 System.out.println(new String(zk.getData("/testRootPath",false,null))); 
		 // ȡ����Ŀ¼�ڵ��б�
		 System.out.println(zk.getChildren("/testRootPath",true)); 
		 // �޸���Ŀ¼�ڵ�����
		 zk.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
		 System.out.println("Ŀ¼�ڵ�״̬��["+zk.exists("/testRootPath",true)+"]"); 
		 // ��������һ����Ŀ¼�ڵ�
		 zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), 
		   Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
		 System.out.println(new String(zk.getData("/testRootPath/testChildPathTwo",true,null))); 
		 // ɾ����Ŀ¼�ڵ�
		 zk.delete("/testRootPath/testChildPathTwo",-1); 
		 zk.delete("/testRootPath/testChildPathOne",-1); 
		 // ɾ����Ŀ¼�ڵ�
		 zk.delete("/testRootPath",-1); 
		 Thread.sleep(1000*60*60);
		 // �ر�����
		 zk.close();

	}

}