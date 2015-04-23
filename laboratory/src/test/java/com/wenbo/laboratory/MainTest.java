package com.wenbo.laboratory;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MainTest {
	
	private static DruidPooledConnection connection= null;
	
	private static RandomAccessFile randomAccessFile = null;

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		findUnionId();
//		backup();
		try {
			if(randomAccessFile != null){
				randomAccessFile.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void findUnionId(){
		String sql = "select unionid from accounts_wechat group by unionid having count(unionid) > 1 limit 1200";
		ResultSet resultSet = null;
		try {
			resultSet = getConnection().createStatement().executeQuery(sql);
			while(resultSet.next()){
				String unionid = resultSet.getString("unionid");
				sql = "select id,email from accounts_wechat where unionid='"+unionid+"'";
				ResultSet emailSet = connection.createStatement().executeQuery(sql);
				List<Integer> idList = new ArrayList<Integer>();
				Map<Integer,String> eMap = new HashMap<Integer, String>();
				Map<Integer,String> vMap = new HashMap<Integer, String>();
				while(emailSet.next()){
					String email = emailSet.getString("email");
					int id = emailSet.getInt("id");
					if(!isVip(email)){
						eMap.put(id,email);
					}
					vMap.put(id,email);
					idList.add(id);
				}
				int updateId = 0;
				if(idList.size() == eMap.size()){//没有vip，只保留id最小的
					Collections.sort(idList);
					for(int i = 1;i<idList.size(); i++){
						updateId = idList.get(i);
						updateUnionId(updateId,unionid,eMap.get(updateId));
					}
				}else if(eMap.isEmpty() && idList.size() > 1){//2个邮箱一样而且都是vip 删除其中一个
					Collections.sort(idList);
					for(int i = 1;i<idList.size(); i++){
						updateId = idList.get(i);
						updateUnionId(updateId,unionid,vMap.get(updateId));
					}
				}else{//有vip，修改map中所有id
					eMap.entrySet().stream().forEach((entry)->{
						updateUnionId(entry.getKey(),unionid,entry.getValue());
					});
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				if(resultSet != null){
					resultSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean isVip(String email){
		HttpResponse response = null;
		 try {
			 email = StringUtils.remove(email,"|").trim();
				HttpGet httpGet = new HttpGet("http://192.168.9.23:8081/batch/query-account-vip-history?email_id="+email);
	            HttpClient httpClient = new DefaultHttpClient(); 
	            response = httpClient.execute(httpGet);
				if (response.getStatusLine().getStatusCode() == 200) {
					String info = EntityUtils.toString(response.getEntity());
					System.out.println(email+":"+info);
					JSONObject object = JSON.parseObject(info);
					return object.getJSONObject("data").getBoolean("history_charge");
				}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	    return false;
	}
	
	public static void updateUnionId(int id,String unionid,String email){
		String sql = "update accounts_wechat set unionid='"+unionid+"_"+id+"' where id="+id;
		try {
			int result = getConnection().createStatement().executeUpdate(sql);
			if(result == 0){
				System.out.println("update error!sql:"+sql);
			}else{
				System.out.println("id:"+id+" update success!sql:"+sql);
				writeToFile(id+":"+unionid+":"+email);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	
	public static void delUnionId(int id){
		String sql = "delete from accounts_wechat where id="+id;
		try {
			int result = getConnection().createStatement().executeUpdate(sql);
			if(result == 0){
				System.out.println("del error!sql:"+sql);
			}else{
				System.out.println("id:"+id+" del success!sql:"+sql);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	
	public static void writeToFile(String info){
		try {
			if(randomAccessFile == null){
				File file = new File("/Users/wenboliu/data/tmp/update.txt");
				randomAccessFile = new RandomAccessFile(file,"rw");
				long length = randomAccessFile.length();
				randomAccessFile.seek(length);
			}
			info = info+"\r\n";
			randomAccessFile.write(info.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	
	
	public static DruidPooledConnection getConnection(){
		if(connection == null){
			try {
				DruidDataSource dataSource = new DruidDataSource();
				dataSource.setUrl("jdbc:mysql://192.168.1.55:3306/passport");
				dataSource.setUsername("neo");
				dataSource.setPassword("fuckbaozi");
				connection = dataSource.getConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
			    
			}
		}
		return connection;
	}
	
	
	/**
	 * 
	 * @param email
	 */
	public static void backup(){
		BufferedReader reader = null;
		try {
			reader = new 
					 BufferedReader(new InputStreamReader(
							 new FileInputStream(new File("/Users/wenboliu/data/tmp/update.txt"))));
			String email = null;
			while((email = reader.readLine()) != null){
				String [] infos = StringUtils.split(email,":");
				if(infos != null && infos.length == 3){
					String id = infos[0];
					String sql = "update accounts_wechat set unionid='"+infos[1]+"' where id="+id;
					int result = getConnection().createStatement().executeUpdate(sql);
					if(result == 0){
						System.out.println("update error!");
						break;
					}
					System.out.println("backup sql:"+sql);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
		    
		}
	}

}
