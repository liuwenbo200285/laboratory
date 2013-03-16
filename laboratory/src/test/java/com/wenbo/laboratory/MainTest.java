package com.wenbo.laboratory;

import java.text.ParseException;

public class MainTest {

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
	}
	
	public static boolean checkPosition(int position){
		int i = 1+(1<<1)+(1<<2)+(1<<3);
		if((31&(1<<2)) == (1<<2)){
			return true;
		}
		return false;
	}

}
