/*
  * TimeDemo.java 
  * 创建于  2013-2-22
  * 
  * 版权所有@深圳市精彩无限数码科技有限公司
  */
package com.wenbo.time;

import java.util.Calendar;

/**
 * @author Administrator
 *
 */
public class TimeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Calendar loginoutCalendar = Calendar.getInstance();
		System.out.println(loginoutCalendar.get(Calendar.DAY_OF_YEAR));
	}

}
