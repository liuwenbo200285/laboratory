package com.wenbo.laboratory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupTest {
	
	private static final String FORUM_DOMAIN = "thread0806.php?fid=22&search=&page=1";
	
	private static final String DOMAIN = "http://cl.1024s.info/";
	
	private static  final int MAX_REPLAY = 90;
	
	private static  final int MIN_REPLAY = 20;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			new JsoupTest().test();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void test() throws IOException{
		for(int i = 101; i <= 201; i++){
			String url = DOMAIN+"thread0806.php?fid=22&search=&page="+i;
			System.out.println("===============第"+i+"页==================");
			getHtml(url);
//	        Scanner sc = new Scanner(System.in);
//	        System.out.println(sc.next());
		}
	}
	
	public void getHtml(String url){
		HttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet(url);
			httpGet.addHeader("Accept","text/html,application/xhtml+xml,application/xml;");
			httpGet.addHeader("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
			httpGet.addHeader("Accept-Encoding","gzip");
			httpGet.addHeader("Cache-Control","max-age=0");
			httpGet.addHeader("Content-Type","text/html");
			httpGet.addHeader("Connection","keep-alive");
			httpGet.addHeader("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.89 Safari/537.36");
            HttpClient httpClient = new DefaultHttpClient(); 
            response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				String info = null;
				if(response.getEntity().getContentEncoding().getValue().toLowerCase().equals("gzip")){
					InputStream inputStream = new GZIPInputStream(response.getEntity().getContent());
					info = inputtoString(inputStream,(int)response.getEntity().getContentLength());
				}else{
					info  = EntityUtils.toString(response.getEntity());
				}
				System.out.println(info);
				Document doc = Jsoup.parse(info);
				parse(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	
	public String inputtoString(InputStream instream,int max) throws IOException{
		if (instream == null) {
            return null;
        }
		if(max < 0){
			max = 1024*1024;
		}
		Charset charset = Charset.forName("GBK");
        Reader reader = new InputStreamReader(instream, charset);
        CharArrayBuffer buffer = new CharArrayBuffer(max);
        char[] tmp = new char[1024];
        int l;
        while((l = reader.read(tmp)) != -1) {
            buffer.append(tmp, 0, l);
        }
        return buffer.toString();
	}
	
	public void parse(Document doc){
		Elements elements = doc.select("tr[align$=center]");
		for(Element element:elements){
			Element titleElement = element.select("a[target=_blank]").get(1);
			String title =titleElement.text();
			String url = DOMAIN+titleElement.attr("href");
			String replayStr = element.select("td.f10").get(0).text();
			if(StringUtils.isNumeric(replayStr)){
				int num = Integer.parseInt(replayStr);
				if(num >= MIN_REPLAY && num <= MAX_REPLAY){
					String info = title+"===url:"+url+"===回复数："+num;
					writeToFile(info);
				}
			}
		}
	}
	
	public void writeToFile(String info){
		RandomAccessFile randomAccessFile = null;
		try {
			info = info+"\r\n";
			File file = new File("/Volumes/Share/System/Library/1024/info.txt");
			randomAccessFile = new RandomAccessFile(file,"rw");
			long length = randomAccessFile.length();
			randomAccessFile.seek(length);
			randomAccessFile.write(info.getBytes());
		} catch (Exception e) {
			
			e.printStackTrace();
		}finally{
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
