/*
  * ClassLoaderTest.java 
  * 创建于  2013-2-28
  * 
  * 版权所有@深圳市精彩无限数码科技有限公司
  */
package com.wenbo.jvmtest;

/**
 * @author Administrator
 *
 */
public class ClassLoaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ClassLoader classLoader = ClassLoaderTest.class.getClassLoader();
		while(classLoader.getParent() != null){
			System.out.println(classLoader.getClass());
			classLoader = classLoader.getParent();
		}
		System.out.println(classLoader.getClass());
//		System.out.println(ClassLoaderTest.class.getClassLoader());
		
		
	}

}
