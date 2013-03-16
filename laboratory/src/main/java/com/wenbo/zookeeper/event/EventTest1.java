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
		// 创建一个与服务器的连接
		 ZooKeeper zk = new ZooKeeper("localhost:" + CLIENT_PORT, 
		        1000*60, new Watcher() { 
		            // 监控所有被触发的事件
		            public void process(WatchedEvent event) { 
		                System.out.println("已经触发了" + event.getType() + "事件！"); 
		            } 
		        }); 
		 Stat stat = zk.exists("/testRootPath",true);
		 if(stat != null){
			 zk.delete("/testRootPath",-1);
		 }else{
			 zk.create("/testRootPath","asdf".getBytes(),Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		 }
		 Thread.sleep(1000*60*60);
		 // 关闭连接
		 zk.close();

	}

}
