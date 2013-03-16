package com.wenbo.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;

public class Demo {

 // 会话超时时间，设置为与系统默认时间一致

 private static final int SESSION_TIMEOUT = 30000;

 // 创建 ZooKeeper 实例

 ZooKeeper zk;

 // 初始化 ZooKeeper 实例

 private void createZKInstance() throws IOException{
  zk = new ZooKeeper("192.168.1.102:2181", Demo.SESSION_TIMEOUT,new Watcher(){

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("eventType="+event.getType());
	}
	  
  });
 }

 private void ZKOperations() throws IOException, InterruptedException, KeeperException{

  Thread.sleep(1000*30);
  System.out.println("\n 创建 ZooKeeper 节点 (znode ： zoo2, 数据： myData2 ，权限： OPEN_ACL_UNSAFE ，节点类型： Persistent");

  zk.create("/zoo2", "myData2".getBytes(), Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

  System.out.println("\n 查看是否创建成功： ");

  System.out.println(new String(zk.getData("/zoo2",true, null)));

  System.out.println("\n 修改节点数据 ");

  zk.setData("/zoo2", "shenlan211314".getBytes(), -1);

  System.out.println("\n 查看是否修改成功： ");

  System.out.println(new String(zk.getData("/zoo2", true, null)));

  System.out.println("\n 删除节点 ");

  zk.delete("/zoo2", -1);

  System.out.println("\n 查看节点是否被删除： ");

  System.out.println(" 节点状态： [" + zk.exists("/zoo2", true) + "]");

 }

 private void ZKClose() throws InterruptedException{
  zk.close();
 }

 public static void main(String[] args) throws IOException,InterruptedException, KeeperException {
  Demo dm = new Demo();
  dm.createZKInstance();
  dm.ZKOperations();
  dm.ZKClose();
 }

}



