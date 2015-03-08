package com.wenbo.algorithms;

import java.util.Arrays;

/**
 * 插入排序
 * @author wenboliu
 *
 */
public class InsertSort {

	public static void main(String[] args) {
       sort(Utils.randomArray(5));
	}
	
	public static void sort(int[] array){
		if(array != null && array.length > 1){
			System.out.println("排序前:"+Arrays.toString(array));
			for(int i = 0;i < array.length; i++){
				int tmp = array[i];
				int n = i-1;
				for(;n >= 0 && tmp < array[n];n--){
					array[n+1] = array[n];
				}
				array[n+1] = tmp;
				System.out.println("排序后:"+Arrays.toString(array));
			}
		}
	}

}
