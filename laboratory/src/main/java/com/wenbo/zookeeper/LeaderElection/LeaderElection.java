package com.wenbo.zookeeper.LeaderElection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import com.wenbo.zookeeper.TestMainClient;
import com.wenbo.zookeeper.TestMainServer;

/**
 * LeaderElection
 * <p/>
 * Author By: junshan
 * Created Date: 2010-9-8 10:05:41
 */
public class LeaderElection extends TestMainClient {
    public static final Logger logger = Logger.getLogger(LeaderElection.class);

    public LeaderElection(String connectString, String root) {
        super(connectString);
        this.root = root;
        if (zk != null) {
            try {
                Stat s = zk.exists(root, false);
                if (s == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                logger.error(e);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    void findLeader() throws InterruptedException, UnknownHostException, KeeperException {
        byte[] leader = null;
        byte[] localhost = InetAddress.getLocalHost().getAddress();
//        try {
//            leader = zk.getData(root + "/leader", true, null);
//        } catch (KeeperException e) {
//            if (e instanceof KeeperException.NoNodeException) {
//                logger.error(e);
//            } else {
//                throw e;
//            }
//        }
        String newFollow = null;
    	try {
    		newFollow = zk.create(root + "/follow", localhost, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            if (e instanceof KeeperException.NodeExistsException) {
                logger.error(e);
            } else {
                throw e;
            }
        }
//    	if(newFollow != null){
//    		following();
//    	}
//        else {
//            String newLeader = null;
//            try {
//                newLeader = zk.create(root + "/leader", localhost, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
//            } catch (KeeperException e) {
//                if (e instanceof KeeperException.NodeExistsException) {
//                    logger.error(e);
//                } else {
//                    throw e;
//                }
//            }
//            if (newLeader != null) {
//                leading();
//            } else {
//                mutex.wait();
//            }
//        }
    }

    @Override
    public void process(WatchedEvent event) {
    	System.out.println(event.getType());
    	try {
    		List<String> names = zk.getChildren(root,true);
    		for(String name:names){
    			System.out.println(name);
    		}
    		System.out.println(names.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
//        if (event.getPath().equals(root + "/leader") && event.getType() == Event.EventType.NodeCreated) {
//        	System.out.println("新建一个领导者！");
//        	leading();
//        }else if(event.getPath().equals(root + "/leader") && event.getType() == Event.EventType.NodeDeleted){
//        	System.out.println("一个领导者离开，重新选择新的领导者!");
//        	
//        }else if(event.getPath().equals(root + "/follow") && event.getType() == Event.EventType.NodeDeleted){
//        	System.out.println("一个组员离开!");
//        }
    }

    void leading() {
        System.out.println("成为领导者");
    }

    void following() {
        System.out.println("成为组成员");
    }

    public static void main(String[] args) {
//        TestMainServer.start();
        String connectString = "localhost:" + TestMainServer.CLIENT_PORT;

        LeaderElection le = new LeaderElection(connectString, "/GroupMembers");
        try {
            le.findLeader();
            Thread.sleep(1000*60*60);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
