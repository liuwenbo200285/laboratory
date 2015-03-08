package com.wenbo.algorithms;
import java.util.Random;

public class Utils {

	/**
	 * 随机生成100以内指定元素个数的数字
	 * @param i
	 * @return
	 */
	public static int[] randomArray(int num){
		int [] array=new int[num];
		Random random = new Random();
		for(int i = 0 ; i < num; i++ ){
			array[i] = random.nextInt(100);
		}
		return array;
	}
}
