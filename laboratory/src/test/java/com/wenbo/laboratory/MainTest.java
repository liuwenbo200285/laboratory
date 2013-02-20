package com.wenbo.laboratory;

import hirondelle.date4j.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainTest {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		int hour = 13;
		GregorianCalendar calender = new GregorianCalendar(Locale.CHINA);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = calender.get(Calendar.YEAR)+"-"+
				(calender.get(Calendar.MONTH)+1)+"-"+(calender.get(Calendar.DAY_OF_MONTH))+" "+hour+":00:00";
		Date beginDate = simpleDateFormat.parse(str);
		Date now = new Date();
		long i = (beginDate.getTime()-now.getTime())/(1000*60);
	}
	
	public static boolean checkPosition(int position){
		int i = 1+(1<<1)+(1<<2)+(1<<3);
		if((31&(1<<2)) == (1<<2)){
			return true;
		}
		return false;
	}

}
