package com.wenbo.gc;



public class GCStringTest {

	public static void main(String[] args) {
		String s1 = "ja";
		String s2 = "va";
		String s3 = "java";
		String s4 = "ja"+s2;
		System.out.println(s3==s4.intern());
		System.out.println(s3==s4);
	}

}
