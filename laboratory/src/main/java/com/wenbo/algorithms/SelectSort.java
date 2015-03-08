package com.wenbo.algorithms;

import java.util.Arrays;

/**
 * 选择排序
 * @author wenboliu
 *
 */
public class SelectSort {

	public static void main(String[] args) {
		sort(Utils.randomArray(10));
	}
	
	public static void sort(int[] array){
		System.out.println("排序前:"+Arrays.toString(array));
		for(int i = 0; i < array.length; i++){
			int tmp = i;
			for(int n = i;n < array.length; n++){
				if(array[n] < array[tmp]){
					tmp = n;
				}
			}
			int tmpValue = array[i];
			array[i] = array[tmp];
			array[tmp] = tmpValue;
			
		}
		System.out.println("排序后:"+Arrays.toString(array));
	}

}
