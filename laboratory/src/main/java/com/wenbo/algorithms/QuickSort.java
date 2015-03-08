package com.wenbo.algorithms;

import java.util.Arrays;

/**
 * 快速排序
 * @author wenboliu
 *
 */
public class QuickSort {

	public static void main(String[] args) {
		sortInit(Utils.randomArray(6),0,5);
	}
	
	public static void sortInit(int [] array,int left,int right){
		if(left < right){
			int split = sort(array, left, right);
			sortInit(array, left,split-1);
			sortInit(array, split+1, right);
		}
	}
	
	public static int sort(int [] array,int left,int right){
		System.out.println("排序前:"+Arrays.toString(array));
		int tmp = array[left];
		while(left < right){
			while(left < right && array[right] > tmp){
				right--;
			}
			array[left] = array[right];
			
			while(left < right && array[left] <= tmp){
				left++;
			}
			array[right] = array[left];
		}
		array[left] = tmp;
		System.out.println("排序后:"+Arrays.toString(array));
		return left;
	}

}
