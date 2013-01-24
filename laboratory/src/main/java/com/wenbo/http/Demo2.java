package com.wenbo.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Demo2 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	   URL url = new URL("http://localhost:8081/hello");
	   HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
	   //设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在 
	   // http正文内，因此需要设为true, 默认情况下是false; 
	   urlConnection.setDoOutput(true); 
	   // 设置是否从httpUrlConnection读入，默认情况下是true; 
	   urlConnection.setDoInput(true); 
	   // Post 请求不能使用缓存 
	   urlConnection.setUseCaches(false); 
	   // 设定传送的内容类型是可序列化的java对象 
	   // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException) 
	   urlConnection.setRequestProperty("Content-type", "application/octet-stream"); 
	   // 设置连接主机超时
	   urlConnection.setConnectTimeout(30000);  
	   //从主机读取数据超时
	   urlConnection.setReadTimeout(30000); 
	   // 设定请求的方法为"POST"，默认是GET 
//	   urlConnection.setRequestMethod("POST"); 
	   // 连接，从上述第2条中url.openConnection()至此的配置必须要在connect之前完成， ]
	   Kryo kryo = new Kryo();
	   OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream(),64*1024);
	   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	   Output output = new Output(byteArrayOutputStream);
	   Map<String,Object> haMap1 = new HashMap<String, Object>();
	   haMap1.put("aaa",111);
	   kryo.writeClassAndObject(output,haMap1);
	   output.close();
	   byteArrayOutputStream.close();
	   byte[] bb = byteArrayOutputStream.toByteArray();
	   outputStream.write(Util.encrypt(bb));
	   outputStream.close();
	   int size = 0;
	   if(urlConnection.getResponseCode() == 200){
		   size = urlConnection.getContentLength();
	   }
	   InputStream inputStream = urlConnection.getInputStream();
	   byte[] cc = new byte[size];
	   ByteArrayOutputStream responseStream = new ByteArrayOutputStream(size);
	   while (inputStream.read(cc) != -1) {
		   responseStream.write(cc);
	   }
	   byte[] dd = responseStream.toByteArray();
	   System.out.println(dd.length);
	   Input input = new Input(dd);
	   Map<String,Object> haMap = (Map<String, Object>) kryo.readClassAndObject(input);
	   System.out.println(haMap.toString());
	}
	
	
	

}
