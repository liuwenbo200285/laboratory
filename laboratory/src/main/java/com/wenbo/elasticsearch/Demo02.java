package com.wenbo.elasticsearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.InternalFilter;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;

import com.alibaba.fastjson.JSONObject;


public class Demo02 {
	
	private static final DateTimeFormatter TEST = DateTimeFormatter.ofPattern("yyyyMMddHHmmss"); 
	
	public static void main(String[] args) {
		testBulk();
	}
	
	
	public static void testBulk(){
		try(Client client = new TransportClient(ImmutableSettings
				.settingsBuilder().put("cluster.name", "elasticsearch").build())
				.addTransportAddress(new InetSocketTransportAddress(
						"10.100.3.29", 9300))){
			BulkProcessor bulkProcessor = BulkProcessor.builder(client,  
			        new BulkProcessor.Listener() {
			            @Override
			            public void beforeBulk(long executionId,
			                                   BulkRequest request){
			            } 
			            @Override
			            public void afterBulk(long executionId,
			                                  BulkRequest request,
			                                  BulkResponse response) {
			            	System.out.println("afterBulk:"+response.getItems());
			            } 
			            @Override
			            public void afterBulk(long executionId,
			                                  BulkRequest request,
			                                  Throwable failure) {
			            	failure.printStackTrace();
			            } 
			        })
			        .setBulkActions(20) 
			        .setBulkSize(new ByteSizeValue(10, ByteSizeUnit.MB)) 
			        .setFlushInterval(TimeValue.timeValueSeconds(30)) 
			        .setConcurrentRequests(1) 
			        .build();
			int i = 0;
			String message = null;
			try(BufferedReader bufferedReader = new 
					BufferedReader(new InputStreamReader(new FileInputStream(new File("/Users/liuwenbo/Desktop/message"))))){
				message = bufferedReader.readLine();
				while(true){
					String [] messages = StringUtils.split(message,"|");
					String info = messages[0];
					String [] infos = StringUtils.split(info," ");
					String jsonData = null;
					if(messages.length > 5){
						for(int n = 4;n < messages.length; n++){
							if(jsonData == null){
								jsonData =messages[n];
							}else{
								jsonData =jsonData+"|"+messages[n];
							}
						}
					}else{
						jsonData = messages[4];
					}
					JSONObject object = null;
					try {
						object = JSONObject.parseObject(jsonData);
					} catch (Exception e) {
						
					}
					object.put("apphost",infos[infos.length-1]);
					object.put("apptime",messages[1]);
					object.put("appname",messages[2]);
					object.put("loglevel",messages[3]);
					LocalDateTime localDateTime = LocalDateTime.parse(messages[1],TEST);
					Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();  
					Date date = Date.from(instant);
					object.put("@timestamp",date);
					String index = messages[2]+"-"+localDateTime.getYear()+"."+(localDateTime.getMonthValue()>9?localDateTime.getMonthValue():"0"+localDateTime.getMonthValue())
							+"."+(localDateTime.getDayOfMonth()>9?localDateTime.getDayOfMonth():"0"+localDateTime.getDayOfMonth());
					bulkProcessor.add(new IndexRequest(index,messages[2]).source(object.toJSONString()));
					i++;
					if(i%20==0){
						System.out.println("insert...");
						TimeUnit.SECONDS.sleep(10);
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void timeSearch(){
		try(Client awsClient = new TransportClient(ImmutableSettings
				.settingsBuilder().put("cluster.name", "es_aws").build())
				.addTransportAddress(new InetSocketTransportAddress(
						"54.223.196.255", 9300))){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long begin = dateFormat.parse("2015-09-28 00:00:00").getTime();
			long end = dateFormat.parse("2015-09-28 23:59:59").getTime();
			System.out.println(begin);
			System.out.println(end);
//			long begin = DateTime.now().minusHours(1).toDate().getTime();
			SearchResponse response = awsClient.prepareSearch("baiwan_nginx-access-2015.09.27","baiwan_nginx-access-2015.09.28")
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
					.setQuery(QueryBuilders.boolQuery().must(
							QueryBuilders.rangeQuery("@timestamp").from(begin).to(end)))
					.addAggregation(AggregationBuilders.terms("url").field("request_dir"))
					.execute().get();
			StringTerms stringTerms = (StringTerms)response.getAggregations().get("url");
			Iterator<Bucket> stringIterator = stringTerms.getBuckets().iterator();
			while(stringIterator.hasNext()){
				Bucket bucket = stringIterator.next();
				System.out.println(bucket.getKeyAsText()+":"+bucket.getDocCount());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void sort(){
		try(Client awsClient = new TransportClient(ImmutableSettings
				.settingsBuilder().put("cluster.name", "es_aws").build())
				.addTransportAddress(new InetSocketTransportAddress(
						"54.223.196.255", 9300))){
			SearchResponse response = awsClient.prepareSearch("ipservice_nginx-access-2015.09.28")
					.addAggregation(AggregationBuilders.terms("time").field("request_time"))
					.execute().get();
			Map<String,Aggregation> map = response.getAggregations().getAsMap();
			DoubleTerms doubleTerms = (DoubleTerms)map.get("time");
			Iterator<Bucket> iterator = doubleTerms.getBuckets().iterator();
			long allNum = 0;
			TreeMap<Double,Long> treeMap = new TreeMap<>();
			while(iterator.hasNext()){
				Bucket bucket = iterator.next();
				allNum +=bucket.getDocCount();
				treeMap.put(Double.valueOf(bucket.getKey()), bucket.getDocCount());
			}
			final long num = allNum;
			long n = 0;
			Iterator<Entry<Double,Long>> iterator2 = treeMap.entrySet().iterator();
			while(iterator2.hasNext()){
				Entry<Double,Long> entry = iterator2.next();
				n+=entry.getValue();
				if(entry.getKey() == 0){
					continue;
				}
				System.out.println("%"+(new BigDecimal(n).
						divide(new BigDecimal(num),5,RoundingMode.HALF_DOWN)).
						multiply(new BigDecimal(100)).doubleValue()+
						"的请求在"+entry.getKey()*1000+"毫秒以内!");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void filter(){
		try(Client awsClient = new TransportClient(ImmutableSettings
				.settingsBuilder().put("cluster.name", "es_aws").build())
				.addTransportAddress(new InetSocketTransportAddress(
						"54.223.196.255", 9300))){
			String[] urls = {"/api/question"};
			SearchResponse response = awsClient.prepareSearch("baiwan_nginx-access-2015.10.06")
//					.setPostFilter(FilterBuilders.boolFilter().mustNot(FilterBuilders.inFilter("request_dir", urls)))
//					.addAggregation(AggregationBuilders.terms("request").field("request_dir"))
					.addAggregation(AggregationBuilders.filter("url").
							filter(FilterBuilders.boolFilter().mustNot(FilterBuilders.termFilter("request_dir",urls)))
							.subAggregation(AggregationBuilders.terms("term_action").field("status")
									.subAggregation(AggregationBuilders.terms("request").field("request_dir"))))
					.execute().get();
			InternalFilter iterFilter = (InternalFilter)response.getAggregations().get("url");
			LongTerms longTerms = (LongTerms)iterFilter.getAggregations().get("term_action");
			Iterator<Bucket> longIterator = longTerms.getBuckets().iterator();
			while(longIterator.hasNext()){
				Bucket bucket = longIterator.next();
				System.out.println("code:"+bucket.getKey());
				StringTerms stringTerms = (StringTerms)bucket.getAggregations().get("request");
				Iterator<Bucket> stringIterator = stringTerms.getBuckets().iterator();
				while(stringIterator.hasNext()){
					bucket = stringIterator.next();
					System.out.println(bucket.getKeyAsText()+":"+bucket.getDocCount());
				}
			}
			System.out.println(response.getHits().getTotalHits());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void aggregation(){
		try(Client awsClient = new TransportClient(ImmutableSettings
				.settingsBuilder().put("cluster.name", "es_aws").build())
				.addTransportAddress(new InetSocketTransportAddress(
						"54.223.196.255", 9300))){
			SearchResponse response = awsClient.prepareSearch("ipservice_nginx-access-2015.09.28")
//					AggregationBuilders.terms("term_action").field("request_dir")
					.setQuery(QueryBuilders.boolQuery().mustNot(QueryBuilders.termQuery("status",200)))
					.addAggregation(AggregationBuilders.terms("term_action").field("status").
							subAggregation(AggregationBuilders.terms("request").field("request_dir")))
					.execute().get();
			Map<String,Aggregation> map = response.getAggregations().getAsMap();
			LongTerms longTerms = (LongTerms)map.get("term_action");
			Iterator<Bucket> iterator = longTerms.getBuckets().iterator();
			while(iterator.hasNext()){
				Bucket bucket = iterator.next();
				System.out.println("status:"+bucket.getKeyAsText());
				StringTerms stringTerms = (StringTerms)bucket.getAggregations().get("request");
				Iterator<Bucket> stringIterator = stringTerms.getBuckets().iterator();
				System.out.print("urls:");
				while(stringIterator.hasNext()){
					bucket = stringIterator.next();
					System.out.print(bucket.getKeyAsText()+",");
				}
				System.out.println("");
				System.out.println("");
			}
//			StringTerms stringTerms = (StringTerms)map.get("request");
//			Iterator<Bucket> stringIterator = stringTerms.getBuckets().iterator();
//			while(stringIterator.hasNext()){
//				Bucket bucket = stringIterator.next();
//				System.out.println(bucket.getKeyAsText()+":"+bucket.getDocCount());
//			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
