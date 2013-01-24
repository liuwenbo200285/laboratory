package com.wenbo.crypt;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class P12_07 {
	public static void main(String[] args) { // 要计算消息验证码的字符串
		String str = "郭克华_安全编程技术";
		String cc = "asdfasdfasdf";
		System.out.println("明文是:" + str);
		try {
			// 生成MAC对象
			SecretKeySpec SKS = new SecretKeySpec(cc.getBytes(), "HMACMD5");
			Mac mac = Mac.getInstance("HMACMD5");
			mac.init(SKS);
			// 传入要计算验证码的字符串
			mac.update(str.getBytes("UTF8"));
			// 计算验证码
			byte[] certifyCode = mac.doFinal();
			String str1 = new String(certifyCode);
			System.out.println("密文是:" + str1);
			// 用DES算法得到计算验证码的密钥
			bb(cc, str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void bb(String cc, String str) throws Exception {
		// 生成MAC对象
		SecretKeySpec SKS = new SecretKeySpec(cc.getBytes(), "HMACMD5");
		Mac mac = Mac.getInstance("HMACMD5");
		mac.init(SKS);
		// 传入要计算验证码的字符串
		mac.update(str.getBytes("UTF8"));
		// 计算验证码
		byte[] certifyCode = mac.doFinal();
		String str1 = new String(certifyCode);
		System.out.println("密文是:" + str1);
	}
}
