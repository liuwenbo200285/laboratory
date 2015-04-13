package com.wenbo.thread;

public class TestThread {
	private  static boolean exec = true;

    public static void main(String[] args) {
        IRun ir = new IRun();
        Thread it = new Thread(ir);
        it.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        	ex.printStackTrace();
        }
        ir.setStop();
    }

      static class IRun implements Runnable {
    	
        public void setStop() {
            exec = false;
            System.out.println("exec = " + exec);
        }
        @Override
        public void run() {
            int c = 0;
            while (exec) {
            	String s = new String("");
                c++;
            }
            System.out.println("退出了循环");
        }
    }
}
