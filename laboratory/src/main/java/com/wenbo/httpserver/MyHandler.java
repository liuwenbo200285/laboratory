package com.wenbo.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;

public class MyHandler extends AbstractHandler {
	
	private static Kryo getKryo(){
		Kryo kryo = new Kryo();
		kryo.setReferences(false); 
		kryo.setRegistrationRequired(false); 
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
		kryo.setDefaultSerializer(TaggedFieldSerializer.class);
		return kryo;
	}

	@Override
	public void handle(String arg0, Request arg1, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		if(arg0 == null || !arg0.equals("/hello")){
			return;
		}
		OutputStream responseStream = response.getOutputStream();
		int len = Integer.parseInt(request.getHeader("Content-length"));
		InputStream inputStream = request.getInputStream();
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
		Kryo kryo = getKryo();
		Map<String,Object> haMap1 = (Map<String, Object>) kryo.readClassAndObject(input);
		System.out.println(haMap1.size());
		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
		Output output = new Output(oStream);
		Map<String,Object> haMap = new HashMap<String, Object>();
		List<User> users = new ArrayList<User>(8000);
		for(int i = 0; i < 80000; i++){
			users.add(new User(i,"user"+i));
		}
		haMap.put("user",users);
		kryo.writeClassAndObject(output,haMap);
		output.close();
		byte[] cc = oStream.toByteArray();
//		response.sendResponseHeaders(200,cc.length);
		response.addIntHeader("Status Code",200);
		response.setContentLength(cc.length);
		responseStream.write(cc);
		responseStream.close();
	}

}
