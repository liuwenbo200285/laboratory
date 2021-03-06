package com.wenbo.thread;

public class ThreadTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final String str = "asdfasdf";
        new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (str) {
					try {
						str.wait();
						System.out.println("已经获得锁！！！");
						Thread.sleep(1000*2);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("已经解锁1111");
			}
		}).start();
        new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (str) {
					for(int i = 0; i < 10; i++){
						System.out.println("已经执行！！！！！！");
						try {
							Thread.sleep(1000*2);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(i == 5){
							str.notifyAll();
						}
					}
				}
				
			}
		}).start();
	}

}
