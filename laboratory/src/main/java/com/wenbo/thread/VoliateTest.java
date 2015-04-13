package com.wenbo.thread;

import java.util.LinkedList;


public class VoliateTest {
	
	private  int aa = 0;
	private boolean flag = false;

	public static void main(String[] args) {
		new VoliateTest().test();
	}
	
	public void test(){
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
//						System.out.println(Thread.currentThread().getName()+aa);
//						aa = 5;
						flag = true;
					}
				}
			}).start();
			new Thread(new Runnable() {
				@Override
				public void run() {
					while(flag){
						System.out.println(Thread.currentThread().getName()+aa);
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
