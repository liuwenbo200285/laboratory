package com.wenbo.operations;

public class Operations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		    //判断奇数和偶数
		    int e = 5,f=8;
		    System.out.println((e&1)==0);
		    System.out.println((f&1)==0);
		    //两数交换
            int a = 10,b=11;
            a = a^b;
            b = a^b;
            a = a^b;
            System.out.println(a);
            System.out.println(b);
            
            //变换符号
            int c = 0;
            c = ~c+1;
            System.out.println(c);
            
            //求绝对值
            int g = -7;
            int gg = g >> 31;
            System.out.println(gg);
            System.out.println(g^gg);
            System.out.println((g^gg)-gg);
            
            //找出一个数组中一个的数字
            int aa[] = {1,2,1,4,5,6,7,8,4,5,6,7,8};
            int h  = 0;
            for(int i = 0; i < aa.length; i++){
            	h = h^aa[i];
            }
            System.out.println(h);
	}

}
