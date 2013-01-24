package com.wenbo.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.net.httpserver.*;

@SuppressWarnings("restriction")
public class Demo1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HttpServer httpServer;
		try {
			httpServer = HttpServer.create(new InetSocketAddress(8081), 5);
			httpServer.setExecutor(Executors.newCachedThreadPool());
			httpServer.createContext("/hello",new HttpHandler(){
				public void handle(HttpExchange httpExchange) throws IOException {
					Headers headers = httpExchange.getRequestHeaders();
					OutputStream responseStream = httpExchange.getResponseBody();
					List<String> lengthList = headers.get("Content-length");
					int len = Integer.parseInt(lengthList.get(0));
					Kryo kryo = new Kryo();
					InputStream inputStream = httpExchange.getRequestBody();
					ByteArrayOutputStream bArrayOutputStream = new ByteArrayOutputStream();
					byte b[] = new byte[len];
					while(inputStream.read(b) != -1){
						bArrayOutputStream.write(b);
					}
					bArrayOutputStream.flush();
					bArrayOutputStream.close();
					Input input = null;
					byte[] bb = null;
					try {
						bb = bArrayOutputStream.toByteArray();
						System.out.println(bb.length);
						input = new Input(Util.decrypt(bb));
					} catch (Exception e) {
						e.printStackTrace();
					}
					Map<String,Object> haMap1 = (Map<String, Object>) kryo.readClassAndObject(input);
					System.out.println(haMap1.size());
//					headers.set("Content-Type", "application/octet-stream");
					ByteArrayOutputStream oStream = new ByteArrayOutputStream();
					Output output = new Output(oStream);
					
					Map<String,Object> haMap = new HashMap<String, Object>();
					haMap.put("errorcode",1);
					kryo.writeClassAndObject(output,haMap);
					output.close();
					byte[] cc = oStream.toByteArray();
					httpExchange.sendResponseHeaders(200,cc.length);
					responseStream.write(cc);
					responseStream.close();
				}
				
			});
			httpServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
