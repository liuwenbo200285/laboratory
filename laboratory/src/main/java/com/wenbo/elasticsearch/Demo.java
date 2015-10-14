package com.wenbo.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.google.common.base.Strings;
import com.hunantv.fw.redis.Redis;

public class Demo {
	
    private static final ExecutorService exceuteService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static Client client = new TransportClient(ImmutableSettings.settingsBuilder()
	        .put("cluster.name", "es_aws").build())
    .addTransportAddress(new InetSocketTransportAddress("54.223.213.124",9300));
    
    private static final DateTimeFormatter INDEX_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    
	private static final DateTimeFormatter YYY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


	public static void main(String[] args) throws IOException {
//		writeMaxId(123);
//		importVipData();
//		System.out.println(MD5.MD5Encode("WECHAT_9GfzcmMCbNjs@hifly.tv"));
//		getLogData();
//		getLogData();
//		getLogDataTicket();
//		System.out.println(URLDecoder.decode("%7B%22province%22%3A%20%22%5Cu6c5f%5Cu82cf%22%2C%20%22openid%22%3A%20%2287C2E46CDC58B56BC72BAC9B618E2497%22%2C%20%22expireIn%22%3A%20%227776000%22%2C%20%22accessToken%22%3A%20%220DCFE48601FD58FC594B82B594442F8A%22%2C%20%22city%22%3A%20%22%5Cu6dee%5Cu5b89%22%2C%20%22country%22%3A%20%22%5Cu4e2d%5Cu56fd%22%2C%20%22sex%22%3A%202%2C%20%22uip%22%3A%20%2249.77.134.95%22%2C%20%22seqid%22%3A%20%22f46ac9d310c54f4f94a4caa3c1d7ef9f%22%2C%20%22type%22%3A%20%22qq%22%2C%20%22action%22%3A%20%22ThirdLogin%22%2C%20%22refreshToken%22%3A%20%22A590DDFC9E248365D3842C1176AAFF17%22%2C%20%22nickname%22%3A%20%22%5Cu6653%5Cu6653%5Cu7684%5Cu5361%5Cu897f%22%2C%20%22avatar%22%3A%20%22%255B%2522http%253A//qzapp.qlogo.cn/qzapp/210731/87C2E46CDC58B56BC72BAC9B618E2497/30%2522%252C%2520%2522http%253A//qzapp.qlogo.cn/qzapp/210731/87C2E46CDC58B56BC72BAC9B618E2497/50%2522%252C%2520%2522http%253A//qzapp.qlogo.cn/qzapp/210731/87C2E46CDC58B56BC72BAC9B618E2497/100%2522%252C%2520%2522http%253A//q.qlogo.cn/qqapp/210731/87C2E46CDC58B56BC72BAC9B618E2497/40%2522%252C%2520%2522http%253A//q.qlogo.cn/qqapp/210731/87C2E46CDC58B56BC72BAC9B618E2497/100%2522%255D%22%7D","utf-8"));
//		getData();
//		testUserEs();
//		getDataForTest();
//		cleanData();
		searchTest();
	}
	
	
	public static void searchTest(){
		try(BufferedReader bufferedReader = new 
				BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/liuwenbo/Desktop/message"))))){
			String message = bufferedReader.readLine();
			String[] messages = StringUtils.split(message,"|");
			System.out.println(messages.length);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void deleteData(){
//		long beginDate = Long.parseLong(LocalDateTime.now().format(YYY_FORMATTER));
		LocalDate localDate = LocalDate.now();
		localDate = localDate.minusDays(1);
		String date = localDate.format(INDEX_FORMATTER);
		String index = "passport-2015.09.14";
		DeleteByQueryResponse response = client.prepareDeleteByQuery(index)
		.setQuery(QueryBuilders.boolQuery().must(QueryBuilders.termQuery("_type","passport_session")).
				must(QueryBuilders.termQuery("action","getSession")))
		        .execute()
		        .actionGet();
		System.out.println(response.status().getStatus());
	}
	
	
	public static void getDataForTest(){
		
	}
	
	public static void cleanData(){
		SearchResponse response = client.prepareSearch("passport-interface-2015.08.31")
		        .setTypes("logtype")
		        .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
		        .setFrom(0)
		        .setSize(1000)
		        .get();
		SearchHit[] hits = response.getHits().getHits();
		for(int i = 0; i < hits.length; i++){
			DeleteRequest deleteRequest = new DeleteRequest("passport-interface-2015.08.31",hits[i].getType(),hits[i].getId());
			client.delete(deleteRequest).actionGet();
		}
		
	}
	
	public static void importData(String jsonData){
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		bulkRequest.add(client.prepareIndex("passport-interface-2015.08.31", "applogs")  
                .setSource(jsonData));
		BulkResponse resp = bulkRequest.execute().actionGet(); 
		System.out.println(resp.getItems()[0].getFailureMessage());
	}
	
	public static void getData(){
		try {
			String url = "http://10.100.1.7:9200/apps-passport-interface-2015.08.31/applogs/_search";
			HttpPost httpPost = new HttpPost(url);
			String str = null;
			try(BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/liuwenbo/data/elasticsearch-head/searchjson/search2"))))){
				str = bf.readLine();
			}catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject searchObject = JSON.parseObject(str);
			boolean flag = true;
			int start = 0;
			while(flag){
				searchObject.put("from",start);
				String searchStr = searchObject.toJSONString();
				StringEntity stringentity = new StringEntity(searchStr,"UTF-8");
				httpPost.addHeader("content-type", "application/json");
				httpPost.setEntity(stringentity);
//				try(CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
//						Redis redis = new Redis()){
//					if(response.getStatusLine().getStatusCode() == 200){
//						String resStr = EntityUtils.toString(response.getEntity());
//						JSONObject object = JSON.parseObject(resStr);
//						JSONArray array  = object.getJSONObject("hits").getJSONArray("hits");
//						int num = array.size();
//						if(num == 0 || start==20){
//							return;
//						}
//						for(int i = 0; i < num; i++){
//							String responseStr  = array.getJSONObject(i).getJSONObject("_source").toJSONString();
//							exceuteService.submit(()->{
//								importData(responseStr);
//							});
//						}
//					}
//				}catch (Exception e) {
//					e.printStackTrace();
//				}
				start = start+1000;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void getLogDataTicket(){
		try {
			String url = "http://10.100.1.7:9200/apps-passport-interface-2015.07.29/applogs/_search";
			HttpPost httpPost = new HttpPost(url);
			String str = null;
			try(BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/liuwenbo/data/elasticsearch-head/searchjson/search2"))))){
				str = bf.readLine();
			}catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject searchObject = JSON.parseObject(str);
			boolean flag = true;
			int start = 0;
			while(flag){
				searchObject.put("from",start);
				String searchStr = searchObject.toJSONString();
				System.out.println(searchStr);
				StringEntity stringentity = new StringEntity(searchStr,"UTF-8");
				httpPost.addHeader("content-type", "application/json");
				httpPost.setEntity(stringentity);
				try(CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
						Redis redis = new Redis()){
					if(response.getStatusLine().getStatusCode() == 200){
						String resStr = EntityUtils.toString(response.getEntity());
						JSONObject object = JSON.parseObject(resStr);
						JSONArray array  = object.getJSONObject("hits").getJSONArray("hits");
						int num = array.size();
						if(num == 0){
							return;
						}
						for(int i = 0; i < num; i++){
							JSONObject logObject  = array.getJSONObject(i).getJSONObject("_source");
							String ticket = logObject.getString("ticket");
							if(Strings.isNullOrEmpty(ticket)){
								continue;
							}
							String apiStr = logObject.getString("Api_GetSession");
							apiStr = StringUtils.removeEnd(apiStr,"_");
							JSONObject getSessionObject = JSON.parseObject(apiStr);
							if(getSessionObject != null && getSessionObject.containsKey("params")){
								String strTicket = getSessionObject.getJSONObject("params").getString("sid");
								if(ticket == null){
									continue;
								}
								if(!ticket.equals(strTicket)){
									System.out.println(logObject.toJSONString());
								}
							}
						}
					}
				}
				start = start+100;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void getLogData(){
		try {
			String url = "http://10.100.1.7:9200/apps-passport-interface-2015.07.29/applogs/_search";
			HttpPost httpPost = new HttpPost(url);
			String str = null;
			try(BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/liuwenbo/data/elasticsearch-head/searchjson/search2"))))){
				str = bf.readLine();
			}catch (Exception e) {
				e.printStackTrace();
			}
			JSONObject searchObject = JSON.parseObject(str);
			int noSession = 0;
			int havaSession = 0;
			boolean flag = true;
			int start = 0;
			while(flag){
				searchObject.put("from",start);
				String searchStr = searchObject.toJSONString();
				System.out.println(searchStr);
				StringEntity stringentity = new StringEntity(searchStr,"UTF-8");
				httpPost.addHeader("content-type", "application/json");
				httpPost.setEntity(stringentity);
				try(CloseableHttpResponse response = HttpClients.createDefault().execute(httpPost);
						Redis redis = new Redis()){
					if(response.getStatusLine().getStatusCode() == 200){
						String resStr = EntityUtils.toString(response.getEntity());
						JSONObject object = JSON.parseObject(resStr);
						JSONArray array  = object.getJSONObject("hits").getJSONArray("hits");
						int num = array.size();
						if(num == 0 || start > 520000){
							System.out.println("noSession:"+noSession);
							System.out.println("havaSession:"+havaSession);
							return;
						}
						for(int i = 0; i < num; i++){
							JSONObject logObject  = array.getJSONObject(i).getJSONObject("_source");
							String ticket = logObject.getString("ticket");
							String info = redis.jedis.get(ticket);
							if(Strings.isNullOrEmpty(info)){
								noSession ++;
							}else{
								try{
									JSONObject object2 = JSON.parseObject(info);
									if(object2.containsKey("extdata")){
										continue;
									}
								}catch(Exception e){
//									FileUtil.writeToFile(info,"/Users/liuwenbo/data/tmp/jsonerror.txt");
									e.printStackTrace();
									continue;
								}
								if(!logObject.containsKey("Api_GetSession")){
									continue;
								}
//								FileUtil.writeToFile(logObject.toJSONString(),"/Users/liuwenbo/data/tmp/20150729.txt");
								try {
									String apiStr = logObject.getString("Api_GetSession");
									apiStr = StringUtils.removeEnd(apiStr,"_");
									JSONObject getSessionObject = JSON.parseObject(apiStr);
									if(JSON.parseObject(getSessionObject.getString("result")).
											getJSONObject("msg").containsKey("data")){
										continue;
									}
									ticket = getSessionObject.getJSONObject("params").getString("sid");
									if(ticket == null){
										System.out.println(apiStr);
										continue;
									}
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println(logObject.getString("Api_GetSession"));
								}
								info = redis.jedis.get(ticket);
								if(Strings.isNullOrEmpty(info)){
									continue;
								}
								try {
									if(!JSON.parseObject(info).containsKey("uuid")){
										continue;
									}
								} catch (Exception e) {
									e.printStackTrace();
									System.out.println(info);
									continue;
								}
//								FileUtil.writeToFile(logObject.toJSONString(),"/Users/liuwenbo/data/tmp/ticket_seqid.txt");
								havaSession++;
							}
						}
					}else{
						System.out.println(EntityUtils.toString(response.getEntity()));
					}
				}
				start = start+500;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void importVipData(){
		try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/wenboliu/Desktop/account-vip20150527.csv"))))){
			String info = null;
			while((info=bufferedReader.readLine()) != null){
//				exceuteService.execute(new ImportVipDataRunnable(info));
			}
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	
	public static void writeMaxId(int maxId){
		PrintWriter printWriter = null;
		try {
			String path ="/Users/wenboliu/data/tmp/";
			printWriter = new PrintWriter(new File(path+"maxid.txt"),"UTF-8");
			printWriter.write(maxId+"");
			printWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			IOUtils.close(printWriter);
		}
	}
	
	
	public static void testEs(){
		@SuppressWarnings("resource")
		Client client = new TransportClient(ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "elk7").build())
	            .addTransportAddress(new InetSocketTransportAddress("10.100.1.7",9300));
		SearchResponse response = client.prepareSearch("apps-passport-interface-2015.07.20")
		        .setTypes("applogs")
		        .setQuery(QueryBuilders.termQuery("invoke","pc"))
		        .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
		        .setFrom(0)
		        .setSize(10)
		        .get();
		System.out.println(response.getHits().getTotalHits());
		client.close();
	}
	
	public static void testUserEs(){
		@SuppressWarnings("resource")
		Client client = new TransportClient(ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "elasticsearch").build())
	            .addTransportAddress(new InetSocketTransportAddress("192.168.9.55",9300));
		SearchResponse response = client.prepareSearch("passport")
		        .setTypes("user")
		        .setQuery(QueryBuilders.prefixQuery("nickname","小"))
		        .setFrom(0)
		        .setSize(10)
		        .get();
		System.out.println(response.getHits().getTotalHits());
		SearchHit searchHit = response.getHits().getHits()[0];
		System.out.println(searchHit.getSourceAsString());
		client.close();
	}
	
	
	

}
