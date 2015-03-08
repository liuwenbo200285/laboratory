package com.wenbo.algorithms;

import java.util.Arrays;

/**
 * 冒泡排序
 * @author wenboliu
 *
 */
public class BubbleSort {

	public static void main(String[] args) {
		sort(Utils.randomArray(30));
	}
	
	
	public static void sort(int [] array){
		System.out.println("排序前:"+Arrays.toString(array));
		if(array != null && array.length > 1){
			for(int i = 0; i < array.length; i++){
				boolean isChange = false;
				for(int n = array.length-1; n > i; n--){
					if(array[n] < array[n-1]){
						int tmp = array[n];
						array[n] = array[n-1];
						array[n-1] = tmp;
						isChange = true;
					}
				}
				if(!isChange){
					break;
				}
			}
		}
		System.out.println("排序后:"+Arrays.toString(array));
	}

}
