package com.wenbo.date;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

public class DateDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		localDate();
		localTime();
	}
	
	
	public static void localDate(){
		LocalDate localDate = LocalDate.now();
		System.out.println(localDate.toString());
		System.out.println(localDate.getDayOfMonth());
		System.out.println(localDate.getDayOfYear());
		System.out.println(localDate.getMonthValue());
		System.out.println(localDate.getYear());
		System.out.println(localDate.getDayOfWeek().getValue());
		
		// 根据年月日取日期，12月就是12：
		LocalDate crischristmas = LocalDate.of(2014, 12, 25); // -> 2014-12-25
		// 根据字符串取：
		LocalDate endOfFeb = LocalDate.parse("2014-02-28"); // 严格按照ISO yyyy-MM-dd验证，02写成2都不行，当然也有一个重载方法允许自己定义格式
		LocalDate.parse("2014-02-28"); // 无效日期无法通过：DateTimeParseException: Invalid date
		
		LocalDate today = LocalDate.now();
		// 取本月第1天：
		LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2014-12-01
		// 取本月第2天：
		LocalDate secondDayOfThisMonth = today.withDayOfMonth(2); // 2014-12-02
		// 取本月最后一天，再也不用计算是28，29，30还是31：
		LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth()); // 2014-12-31
		// 取下一天：
		LocalDate firstDayOf2015 = lastDayOfThisMonth.plusDays(1); // 变成了2015-01-01
		// 取2015年1月第一个周一，这个计算用Calendar要死掉很多脑细胞：
		LocalDate firstMondayOf2015 = LocalDate.parse("2015-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)); // 2015-01-05
	}
	
	public static void localTime(){
		LocalTime now = LocalTime.now(); // 11:09:09.240
		System.out.println(now);
		now = LocalTime.now().withNano(0); // 11:09:09
		System.out.println(now);
		LocalTime zero = LocalTime.of(0, 0, 0); // 00:00:00
		System.out.println(zero);
		LocalTime mid = LocalTime.parse("12:00:00"); // 12:00:00
		System.out.println(mid);
	}

}
