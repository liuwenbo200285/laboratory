package com.wenbo.sort;

import java.util.Arrays;

public class SortTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int aa[] = {10,25,13,56,20,1,39,60}; 
		maoPao(aa);
	}
	
	/**
	 * 冒泡排序
	 * @param aa
	 */
	public static void maoPao(int [] aa){
		int num = aa.length;
		int tmp = 0;
		boolean flag = false;
		System.out.println(Arrays.toString(aa));
		for(int i = 0; i < num; i++){
			flag = false;
			for(int j = num-1;j >0;j--){
				if(aa[j] < aa[j-1]){
					tmp = aa[j];
					aa[j] = aa[j-1];
					aa[j-1] = tmp;
					flag = true;
				}
			}
			if(!flag){
				break;
			}
			System.out.println(Arrays.toString(aa));
		}
		System.out.println(Arrays.toString(aa));
	}

}
