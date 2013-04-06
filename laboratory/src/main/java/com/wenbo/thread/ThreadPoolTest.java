/**
 * 
 */
package com.wenbo.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wenbo
 *
 */
public class ThreadPoolTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		newFixedThreadPool();
	}
	
	public static void newFixedThreadPool(){
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		for(int i = 0; i < 20; i++){
			final int  num = i;
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println(num);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			System.out.println("i:"+i);
		}
	}

}
