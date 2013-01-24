package com.wenbo.random;

import java.util.Random;

public class RandomTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Random random = new Random();
		for(int i = 0; i < 100; i++){
			System.out.println(random.nextInt(2));
		}

	}

}
