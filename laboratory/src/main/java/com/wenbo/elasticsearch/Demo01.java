package com.wenbo.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.impl.client.HttpClientBuilder;

public class Demo01 {
	
	private static final ExecutorService KICKED_USER_EXECUTORSERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private static final DateTimeFormatter YYMMDD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public static void main(String[] args) throws Exception {
		String url = "http://192.168.9.29:8001/in/EditUserInfo?invoker=itvsdk&sign=1185acb9d5a0667fdccf6e7d1598c11d66541fb5&data=%7B%22uuid%22%3A%222059718cd5a811dc217a0fcb2432da49%22%2C%22ticket%22%3A%223714M3JJSNKBEZPB73D6%22%2C%22nickname%22%3A%22%22%2C%22avatar%22%3A%22%22%2C%22isband%22%3A%223%22%2C%22banned%22%3A%22%22%2C%22uip%22%3A%22127.0.0.1%22%2C%22seqid%22%3A%221309458556dae6f05c96238c61fcc16b%22%2C%22is_sign%22%3A1%7D";
		System.out.println(URLDecoder.decode(url,"utf-8"));
		sendEmail();
	}
	
	public static void sendEmail(){
		try{
			String time = LocalDateTime.now().format(YYMMDD_FORMATTER);
			String key = "ott_count_"+time;
			String ottcount = "";
			key = "mpp_count_"+time;
			String mppcount = "";
			int mpp = Integer.parseInt(mppcount)-41760;
			String info = "";
			String[] infos = StringUtils.split(info,",");
			int allkeys = Integer.parseInt(infos[0].split("=")[1]);
			int expires = Integer.parseInt(infos[1].split("=")[1]);
			String content = "ottcount:"+ottcount+"\r\n mppcount:"+mpp+"\r\n noexpires:"+(allkeys-expires);
			Map<String,String> paraMap = new HashMap<String, String>();
			paraMap.put("SES_appid","aaa");
			paraMap.put("SES_fromName","passport");
			paraMap.put("SES_pwd","698D51A19D8A121CE581499D7B701668");
			paraMap.put("SES_fromAddress","admin@hunantv.com");
			paraMap.put("SES_sendType","rightnow");
			paraMap.put("SES_content",content);
			paraMap.put("SES_address","liuwenbo@e.hunantv.com");
			paraMap.put("SES_title",time+"   redis数据");
//			HttpPost httpPost = HttpUtil.getHttpPost("http://192.168.1.168:80/ses/sendEmail", paraMap);
//			try(CloseableHttpResponse response = HttpClientBuilder.create().build().execute(httpPost);){
//				System.out.println(response.getStatusLine().getStatusCode());
//			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void cleanData() throws IOException{
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(new File("/Users/liuwenbo/Documents/errorkey.txt"))));
		String str = null;
		List<String> list = new ArrayList<String>();
		while((str=bufferedReader.readLine()) != null){
			str = StringUtils.remove(str,"errorkey:=============>");
			list.add(str);
		}
		bufferedReader.close();
		Jedis jedis = new Jedis("10.100.3.81",8110,60*1000);
		list.parallelStream().forEach(key->{
			try {
				jedis.del(key);
				System.out.println(key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
