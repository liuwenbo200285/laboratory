package com.wenbo.http;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Util {
	
	/**
	 * 加密
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public static byte[] encrypt(byte[] b) throws Exception{
		// KeyGenerator提供对称密钥生成器的功能，支持各种算法
		 String sKey = "sadfasdfasdfasdfasdfasdfa";
		 DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(sKey.getBytes());
         SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
         SecretKey deskey = keyFactory.generateSecret(deSedeKeySpec);
		// Cipher负责完成加密或解密工作
		 Cipher c = Cipher.getInstance("DESede");
		// 根据密钥，对Cipher对象进行初始化,ENCRYPT_MODE表示加密模式
		c.init(Cipher.ENCRYPT_MODE, deskey);
		return c .doFinal(b);
	}
	
	/**
	 * 加密
	 * @param b
	 * @return
	 * @throws Exception 
	 */
	public static byte[] decrypt(byte[] b) throws Exception{
		String sKey = "sadfasdfasdfasdfasdfasdfa";
		DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(sKey.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
        SecretKey deskey = keyFactory.generateSecret(deSedeKeySpec);
		// Cipher负责完成加密或解密工作
		Cipher c = Cipher.getInstance("DESede");
		// 生成Cipher对象，指定其支持3DES算法
		c.init(Cipher.DECRYPT_MODE, deskey);
		System.out.println(b.length);
		return c .doFinal(b);
	}
	
	public static void main(String [] args) throws Exception{
		Kryo kryo = new Kryo();
		Map<String,Object> haMap1 = new HashMap<String, Object>();
		haMap1.put("aaa",111);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Output output = new Output(outputStream);
		kryo.writeObject(output,haMap1);
		output.close();
		byte[] aa = outputStream.toByteArray();
		System.out.println("加密前："+aa.length);
		byte[] bb = encrypt(aa);
		System.out.println("加密后："+bb.length);
		byte[] cc = decrypt(bb);
		System.out.println("解密后："+cc.length);
		Input input = new Input(cc);
		HashMap<String,Object> hashMap = kryo.readObject(input,HashMap.class);
		System.out.println(hashMap.size());
	}

}
