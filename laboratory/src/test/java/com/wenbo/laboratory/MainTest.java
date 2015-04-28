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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class MainTest {
	
	private static DruidPooledConnection connection= null;
	
	private static RandomAccessFile randomAccessFile = null;
	
	private static RandomAccessFile randomAccessErrorFile = null;
	
    private static final ExecutorService exceuteService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    
    private static final AtomicInteger AU_ATOMIC_INTEGER = new AtomicInteger();

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
//		findUnionId();
		int num = findUserId(59364233);
		System.out.println("ALl num:"+num);
		try {
			while(num == AU_ATOMIC_INTEGER.get()){
				if(randomAccessFile != null){
					randomAccessFile.close();
				}
				if(randomAccessErrorFile != null){
					randomAccessErrorFile.close();
				}
				exceuteService.shutdownNow();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static int findUserId(int beginId){
		String sql = "select f.uuid,u.nickname,f.email,f.platform,f.reg_time,u.avatar,u.isband from friend f  right join user_profile u on f.email=u.email where f.uid > "+beginId+" ";
		ResultSet resultSet = null;
		int num = 0;
		try {
			resultSet = getConnection().createStatement().executeQuery(sql);
			while(resultSet.next()){
				String uuid = resultSet.getString("uuid");
				if(uuid == null){
					continue;
				}
				String email = resultSet.getString("email");
				String nickname = resultSet.getString("nickname");
				String platform = resultSet.getString("platform");
				String reg_time = resultSet.getString("reg_time");
				String avatar = resultSet.getString("avatar");
				String isband = resultSet.getString("isband");
				UserInfo userInfo = new UserInfo();
				userInfo.setAvatar(avatar);
				userInfo.setEmail(email);
				userInfo.setIsband(isband);
				userInfo.setNickname(nickname);
//				userInfo.setOpenid(openid);
				userInfo.setPlatform(platform);
				userInfo.setReg_time(reg_time);
				userInfo.setUuid(uuid);
				sendData(userInfo);
				num++;
			}
			System.out.println("send Data end!!!!!");
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
		return num;
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
	
	public static void sendData(UserInfo userInfo){
		exceuteService.submit(()->{
			HttpResponse response = null;
			HttpClient httpClient = new DefaultHttpClient();
			 try {
				 	String openid = null;
					if(StringUtils.startsWith(userInfo.getEmail(),"WECHAT_")){
						String findOpenIdSql = "select openid from accounts_wechat where uuid='"+userInfo.getUuid()+"'";
						ResultSet webchatResultSet = getConnection().createStatement().executeQuery(findOpenIdSql);
						while(webchatResultSet.next()){
							openid = webchatResultSet.getString("openid");
							break;
						}
					}else if(StringUtils.startsWith(userInfo.getEmail(),"XIAOMI_")){
						String findOpenIdSql = "select openid from accounts_xiaomi where uuid='"+userInfo.getUuid()+"'";
						ResultSet xiaomResultSet = getConnection().createStatement().executeQuery(findOpenIdSql);
						while(xiaomResultSet.next()){
							openid = xiaomResultSet.getString("openid");
							break;
						}
					}else{
						String findOpenIdSql = "select rcontent from accounts_oauth where remail='"+userInfo.getEmail()+"'";
						ResultSet oauthResultSet = getConnection().createStatement().executeQuery(findOpenIdSql);
						while(oauthResultSet.next()){
							openid = oauthResultSet.getString("rcontent");
							break;
						}
					}
				    List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
				    parameters.add(new BasicNameValuePair("uuid",userInfo.getUuid()));
				    parameters.add(new BasicNameValuePair("nickname",userInfo.getNickname()));
				    parameters.add(new BasicNameValuePair("email",userInfo.getEmail()));
				    parameters.add(new BasicNameValuePair("reg_time",userInfo.getReg_time()));
				    parameters.add(new BasicNameValuePair("isband",userInfo.getIsband()));
				    parameters.add(new BasicNameValuePair("avatar",userInfo.getAvatar()));
				    parameters.add(new BasicNameValuePair("platform",userInfo.getPlatform()));
				    parameters.add(new BasicNameValuePair("openid",openid));
				    parameters.add(new BasicNameValuePair("invoker","www"));
					UrlEncodedFormEntity uef = new UrlEncodedFormEntity(parameters,"UTF-8");
					HttpPost httpPost = new HttpPost("http://192.168.9.54/addEsUser");
					httpPost.setEntity(uef);
		            response = httpClient.execute(httpPost);
					if (response.getStatusLine().getStatusCode() == 200) {
						String json = EntityUtils.toString(response.getEntity());
						JSONObject object = JSONObject.parseObject(json);
						if(object.getIntValue("status") == 0){
							writeToFile(userInfo.getUuid()+":"+userInfo.getEmail()+":"+userInfo.getNickname(),"send.txt");
						}else{
							writeToErrorFile(json+"========="+userInfo.getUuid()+":"+userInfo.getEmail()+":"+userInfo.getNickname(),"sendError.txt");
						}
					}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				httpClient.getConnectionManager().closeExpiredConnections();
				System.out.println("send finish!!!!!!");
				System.out.println(AU_ATOMIC_INTEGER.incrementAndGet());
			}
		});
	}
	
	public static void updateUnionId(int id,String unionid,String email){
		String sql = "update accounts_wechat set unionid='"+unionid+"_"+id+"' where id="+id;
		try {
			int result = getConnection().createStatement().executeUpdate(sql);
			if(result == 0){
				System.out.println("update error!sql:"+sql);
			}else{
				System.out.println("id:"+id+" update success!sql:"+sql);
				writeToFile(id+":"+unionid+":"+email,"vip.txt");
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
	
	public static synchronized void writeToFile(String info,String fileName){
		try {
			if(randomAccessFile == null){
				File file = new File("/Users/wenboliu/data/tmp/"+fileName);
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
	
	public static synchronized void writeToErrorFile(String info,String fileName){
		try {
			if(randomAccessErrorFile == null){
				File file = new File("/Users/wenboliu/data/tmp/"+fileName);
				randomAccessErrorFile = new RandomAccessFile(file,"rw");
				long length = randomAccessErrorFile.length();
				randomAccessErrorFile.seek(length);
			}
			info = info+"\r\n";
			randomAccessErrorFile.write(info.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	
	
	public static DruidPooledConnection getConnection(){
		if(connection == null){
			try {
				DruidDataSource dataSource = new DruidDataSource();
				dataSource.setUrl("jdbc:mysql://192.168.1.110:3306/passport");
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
