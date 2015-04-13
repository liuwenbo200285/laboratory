package com.wenbo.thread;

public class Demo implements Runnable{

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new Thread((new Demo(0))).start();
		new Thread((new Demo(1))).start();
		new Thread((new Demo(2))).start();
//		Thread.sleep(1000);
//		Demo.flag = true;
	} 
	
	private  int i;
	
	private static String flag = "aaa";
	
	public Demo(int i){
		this.i = i;
	}
	
	public void println() throws InterruptedException{
		System.out.println("=============================="+i);
		synchronized (flag) {
			while(true){
				System.out.println("++++++++++++++++++++++++++"+i);
				Thread.sleep(1000);
			}
		}
	}
	
	public void printlnOne() throws InterruptedException{
		while(true){
			System.out.println("get object......"+i);
			Thread.sleep(1000);
		}
	}

	@Override
	public void run() {
		try {
			if(i==1){
				this.printlnOne();
			}else{
				this.println();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
