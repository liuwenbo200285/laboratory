package com.wenbo.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	  int aa = Runtime.getRuntime().availableProcessors();
      ExecutorService executorService =  new ThreadPoolExecutor(aa,5,
              60L, TimeUnit.SECONDS,
              new ArrayBlockingQueue<Runnable>(10));
      for(int i = 0; i < 20; i++){
    	  final int num = i;
    	  executorService.execute(new Runnable() {
			@Override
			public void run() {
				System.out.println(num);
				while(true){}
			}
		});
      }
	}

}
 