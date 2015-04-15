package com.wenbo.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;


public class Demo {

	Runnable r1 = () -> { System.out.println(this); };
	Runnable r2 = () -> { System.out.println(toString()); };
	  
	public static void main(String[] args) {
		List<String> names = new ArrayList<>();
		names.add("TaoBao");
		names.add("ZhiFuBao");
		names.add("Alibaba");
		List<String> lowercaseNames = names.stream().map(String::toLowerCase).collect(Collectors.toList());
		System.out.println(lowercaseNames.toString());
		Collections.sort(lowercaseNames, (o1, o2) -> o1.compareTo(o2));
		System.out.println(lowercaseNames.toString());
		//Lists是Guava中的一个工具类
		List<Integer> nums = Lists.newArrayList(1,null,3,4,null,6,3,4);
//		System.out.println(nums.stream().distinct().filter(num->num != null).collect(Collectors.toList()));
		System.out.println(nums.stream().filter(num->num != null)
				.distinct().mapToInt(num->num<<1).peek(System.out::println)
				.skip(2).limit(4).max().getAsInt());
		List<Integer> ints = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10);
		System.out.println("ints sum is:" + ints.stream().reduce(0, (sum, item) -> sum + item));
		
	}
	

	  public String toString() {  return "Hello, world"; }

}
