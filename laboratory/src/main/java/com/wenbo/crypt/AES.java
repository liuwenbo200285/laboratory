/*
  * AES.java 
  * 创建于  2013-4-10
  * 
  * 版权所有@深圳市精彩无限数码科技有限公司
  */
package com.wenbo.crypt;

/**
 * @author Administrator
 *
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

public class AES {
	
	private static final String keyword = "123456789";
	
	/**
	 * 密钥算法
	*/
	private static final String KEY_ALGORITHM = "AES";
		
	private static Key key;
	
	static{
		try {
			if(key == null){
				   KeyGenerator  generator = KeyGenerator.getInstance(KEY_ALGORITHM);  
		           SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");  
		           secureRandom.setSeed(keyword.getBytes("UTF-8"));  
		           generator.init(128 ,secureRandom);  
		           key = generator.generateKey(); 
			   }
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(" 初始化密钥出现异常 ");
		}
	}
	
	public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }
	
	/**
	 * 生成AES对称秘钥
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException 
	 */
	public static void generateKey() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		try {
			   if(key == null){
				   synchronized(AES.class){
					   if(key == null){
						   KeyGenerator  generator = KeyGenerator.getInstance(KEY_ALGORITHM);  
				           SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");  
				           secureRandom.setSeed(keyword.getBytes("UTF-8"));  
				           generator.init(128 ,secureRandom);  
				           key = generator.generateKey(); 
					   }
				   }
			   }
		} catch (Exception e) {
			throw new RuntimeException(" 初始化密钥出现异常 ");
		}
	}
	
	
	/**
	 * 加密
	 * @param in
	 * @param out
	 * @throws InvalidKeyException
	 * @throws ShortBufferException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IOException
	 */
	public static void encrypt(InputStream in, OutputStream out) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		crypt(in, out, Cipher.ENCRYPT_MODE);
	}
	
	/**
	 * 解密
	 * @param in
	 * @param out
	 * @throws InvalidKeyException
	 * @throws ShortBufferException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IOException
	 */
	public static void decrypt(InputStream in, OutputStream out) throws InvalidKeyException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, IOException {
		crypt(in, out, Cipher.DECRYPT_MODE);
	}

	/**
	 * 实际的加密解密过程
	 * @param in
	 * @param out
	 * @param mode
	 * @throws IOException
	 * @throws ShortBufferException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 */
	public static void crypt(InputStream in, OutputStream out, int mode) throws IOException, ShortBufferException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(mode,AES.key);
		
		int blockSize = cipher.getBlockSize();
		int outputSize = cipher.getOutputSize(blockSize);
		byte[] inBytes = new byte[blockSize];
		byte[] outBytes = new byte[outputSize];
		
		int inLength = 0;
		boolean more = true;
		while (more) {
			inLength = in.read(inBytes);
			if (inLength == blockSize) {
				int outLength = cipher.update(inBytes, 0, blockSize, outBytes);
				out.write(outBytes, 0, outLength);
			} else {
				more = false;
			}
		}
		if (inLength > 0)
			outBytes = cipher.doFinal(inBytes);
		else
			outBytes = cipher.doFinal();
		out.write(outBytes);
		out.flush();
	}
	
	public static void main(String[] args){
		System.out.println(System.getProperty("file.encoding"));
	}
	
}