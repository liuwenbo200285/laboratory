package com.wenbo.elasticsearch;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeRequest;
import org.elasticsearch.action.admin.indices.optimize.OptimizeResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsRequest;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ESSearchDemo {
	
	private static Client client = new TransportClient(ImmutableSettings.settingsBuilder()
	        .put("cluster.name", "es_yg").build())
    .addTransportAddress(new InetSocketTransportAddress("10.100.4.127",9300));
	
	private static final DateTimeFormatter YYY_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	
	private static final DateTimeFormatter INDEX_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
	

	public static void main(String[] args){
//		searchByData1();
//		deleteESDATA("es_b28","192.168.1.56","passport-2015.09.20");
//		ottErrorDeal();
//		ottErrorDeal();
//		Date date = new Date();AU_u55q0pWMmNqiIq94h
//		date.setTime(1442667690000l);
//		LocalDateTime localDateTime = LocalDateTime.
//				ofInstant(date.toInstant(),ZoneId.of(ZoneId.SHORT_IDS.get("PST")));
//		SearchResponse searchResponse = client.prepareSearch("otterror-2015.09.19")
//				.setTypes("otterror")
//				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
//				.setQuery(QueryBuilders.idsQuery("AU_u55q0pWMmNqiIq94h"))
//				.setFrom(0)
//				.setSize(3000).get();
//		System.out.println(searchResponse.getHits().totalHits());
//		queryTest();
//		deleteESDATA("es_yg","10.100.4.127","passport-2015.09.14");
	}
	

	
	public static void ottErrorDeal(){
		try{
			IndicesAdminClient indicesAdminClient = client.admin().indices();
			IndicesStatsRequest statsRequest = new IndicesStatsRequest(); 
			statsRequest = statsRequest.all();
			IndicesStatsResponse response = indicesAdminClient.stats(statsRequest).get();
			response.getIndices().forEach((key,value)->{
				if(StringUtils.startsWith(key,"otterror")){
					String[] names = key.split("-"); 
					String date = names[1];
					String[] dates = StringUtils.split(date,".");
					if(!"2015".equals(dates[0])){
//						dealOTTDate(key,client);
						DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(key);
						try {
							indicesAdminClient.delete(deleteIndexRequest).get();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dealOTTDate(String index,Client client){
		try{
			SearchResponse searchResponse = client.prepareSearch(index)
			.setTypes("otterror")
			.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
			.setFrom(0)
			.setSize(3000).get();
			Iterator<SearchHit> iterator = searchResponse.getHits().iterator();
			while(iterator.hasNext()){
				SearchHit searchHit = iterator.next();
				long time = Long.parseLong(searchHit.getSource().get("time").toString())*1000;
				Date date = new Date();
				date.setTime(time);
				LocalDateTime localDateTime = LocalDateTime.
						ofInstant(date.toInstant(),ZoneId.of(ZoneId.SHORT_IDS.get("PST")));
				String insertIndex = "otterror-"+localDateTime.getYear()+"."+localDateTime.getMonthValue()+
						"."+localDateTime.getDayOfMonth();
				String timeStr = localDateTime.format(YYY_FORMATTER);
				JSONObject object = JSON.parseObject(searchHit.getSourceAsString());
				SimpleDateFormat ISO8601DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSS'Z'");
				object.put("errortime",timeStr);
				object.put("@timestamp", ISO8601DATEFORMAT.format(date));
				importData(insertIndex,client,object.toJSONString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void deleteOTTData(String clusterName,String ip,String index){
		try (Client client = new TransportClient(ImmutableSettings
				.settingsBuilder().put("cluster.name",clusterName).build())
				.addTransportAddress(new InetSocketTransportAddress(
						ip, 9300))) {
			DeleteByQueryResponse deleteByQueryResponse = client
					.prepareDeleteByQuery(index)
					.setQuery(
							QueryBuilders
									.boolQuery()
									.must(QueryBuilders.termQuery("_type",
											"otterror")).mustNot(QueryBuilders.termQuery("aver","4.4.4"))).execute().actionGet();
			System.out.println("delete interface data status:"
					+ deleteByQueryResponse.status().getStatus());
		} catch (Exception e) {
			System.out.println(ip+":error");
			e.printStackTrace();
		}
	}
	
	public static void importData(String index,Client client,String jsonData){
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		bulkRequest.add(client.prepareIndex(index, "otterror")  
                .setSource(jsonData));
		BulkResponse resp = bulkRequest.execute().actionGet(); 
		System.out.println(resp.getItems()[0].getId());
		System.out.println(resp.getItems()[0].getIndex());
	}
	
	public static void deleteESDATA(String cluster,String ip,String index) {
		try (Client client = new TransportClient(ImmutableSettings
				.settingsBuilder().put("cluster.name",cluster).build())
				.addTransportAddress(new InetSocketTransportAddress(
						ip, 9300))) {
			DeleteByQueryResponse deleteByQueryResponse = client
					.prepareDeleteByQuery(index)
					.setQuery(
							QueryBuilders
									.boolQuery()
									.must(QueryBuilders.termQuery("_type",
											"passport_interface"))
									.must(QueryBuilders.termQuery("action",
											"GetUser"))).execute().actionGet();
			System.out.println("delete interface data status:"
					+ deleteByQueryResponse.status().getStatus());
			
			deleteByQueryResponse = client
					.prepareDeleteByQuery(index)
					.setQuery(
							QueryBuilders
									.boolQuery()
									.must(QueryBuilders.termQuery("_type",
											"passport_session"))
									.must(QueryBuilders.termQuery("action",
											"getSession"))).execute().actionGet();
			System.out.println("delete interface data status:"
					+ deleteByQueryResponse.status().getStatus());
			
			IndicesAdminClient indicesAdminClient = client.admin().indices();
			OptimizeRequest request = new OptimizeRequest();
			request.indices(index).onlyExpungeDeletes(true);
			indicesAdminClient.optimize(request,new ActionListener<OptimizeResponse>(){
				@Override
				public void onResponse(OptimizeResponse response) {
					System.out.println(response.getSuccessfulShards());
				}

				@Override
				public void onFailure(Throwable e) {
					e.printStackTrace();
				}
				
			});
			Thread.currentThread().join();
		} catch (Exception e) {
			System.out.println(ip+":error");
			e.printStackTrace();
		}
	}
	
	public static void searchByData1(){
		try {
			String index = "passport-"+LocalDateTime.now().format(INDEX_FORMATTER);
			SearchResponse response = client.prepareSearch(index)
			        .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
			        .setTypes("passport_session")
			        .setQuery(QueryBuilders.boolQuery()
			        		.must(QueryBuilders.rangeQuery("use_time").gte(10)))
			        .setFrom(0)
			        .setSize(2)
			        .get();
			SearchHit[] hits = response.getHits().getHits();
			System.out.println(hits.length);
			for(SearchHit hit:hits){
				System.out.println(hit.getSource().get("action")+":"+hit.getSource().get("use_time")+":"+hit.getSource().get("seqid")+":"+hit.getSource().get("apptime"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void searchByData(){
		long beginDate = Long.parseLong(LocalDateTime.now().format(YYY_FORMATTER));
		SearchResponse response = client.prepareSearch("passport_interface-2015.09.07")
		        .setTypes("passport_interface")
		        .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
		        .setQuery(QueryBuilders.termQuery("action.raw","GetUser"))
		        .setPostFilter(FilterBuilders.rangeFilter("apptime").from(beginDate-1200).to(beginDate))
		        .setFrom(0)
		        .setSize(2)
		        .get();
		SearchHit[] hits = response.getHits().getHits();
		System.out.println(hits.length);
		for(SearchHit hit:hits){
			System.out.println(hit.getSource().get("action")+":"+hit.getSource().get("used_time")+":"+hit.getSource().get("seqid")+":"+hit.getSource().get("apptime"));
		}
	}
	
	public JSONObject flushData() {
		long beginDate = Long.parseLong(LocalDateTime.now().format(
				YYY_FORMATTER));
		String index = "passport-"
				+ LocalDateTime.now().format(INDEX_FORMATTER);
		SearchResponse response = client
				.prepareSearch(index)
				.setTypes("passport_interface")
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery("action.raw", "GetUser"))
				.setPostFilter(
						FilterBuilders.rangeFilter("apptime")
								.from(beginDate - 30).to(beginDate)).setFrom(0)
				.setSize(4).get();
		SearchHit[] hits = response.getHits().getHits();
		JSONArray timeArray = new JSONArray();
		JSONArray dataArray = new JSONArray();
		for (SearchHit hit : hits) {
			timeArray.add(hit.getSource().get("apptime").toString()
					.substring(8, 14));
			JSONObject object = new JSONObject();
			object.put("y", hit.getSource().get("used_time"));
			object.put("seqid", hit.getSource().get("seqid"));
			dataArray.add(object);
		}
		JSONObject resultJsonObject = new JSONObject();
		resultJsonObject.put("xdata", timeArray);
		resultJsonObject.put("ydata", dataArray);
		return resultJsonObject;
	}

	public JSONObject getDataByDateAndSeqId(String date, String seqId) {
		JSONObject object = new JSONObject();
		SearchResponse response = client
				.prepareSearch("passport_interface-2015.09.07")
				.setTypes("passport_interface")
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery("seqid", seqId)).setFrom(0)
				.setSize(1000).get();
		SearchHit[] hits = response.getHits().getHits();
		if (hits.length > 0) {
			object.put("passport_interface", hits[0].getSource());
		} else {
			return object;
		}
		// -- account
		response = client.prepareSearch("passport_account-2015.09.07")
				.setTypes("passport_account")
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery("seqid", seqId)).setFrom(0)
				.setSize(1000).get();
		hits = response.getHits().getHits();
		if (hits.length > 0) {
			object.put("passport_account", hits[0].getSource());
		}

		// -- auth
		response = client.prepareSearch("passport_oauth-2015.09.07")
				.setTypes("passport_oauth")
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery("seqid", seqId)).setFrom(0)
				.setSize(1000).get();
		hits = response.getHits().getHits();
		if (hits.length > 0) {
			object.put("passport_oauth", hits[0].getSource());
		}

		// -- session
		response = client.prepareSearch("passport_session-2015.09.07")
				.setTypes("passport_session")
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(QueryBuilders.termQuery("seqid", seqId)).setFrom(0)
				.setSize(1000).get();
		hits = response.getHits().getHits();
		if (hits.length > 0) {
			object.put("passport_session", hits[0].getSource());
		}
		return object;
	}
}
