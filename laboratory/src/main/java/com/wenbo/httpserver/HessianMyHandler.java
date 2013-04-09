package com.wenbo.httpserver;

import java.io.ByteArrayInputStream;
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
import org.eclipse.jetty.server.handler.DefaultHandler;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class HessianMyHandler extends DefaultHandler{

	@Override
	public void handle(String target, Request baseRequest,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if(target == null || !target.equals("/hello")){
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
		byte[] bb = null;
		try {
			bb = bArrayOutputStream.toByteArray();
			bb = Util.decrypt(bb);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bb);
		HessianInput hessianInput = new HessianInput(byteArrayInputStream);
		Map<String,Object> haMap1 = (Map<String, Object>)hessianInput.readObject();
		System.out.println(haMap1.size());
		Map<String,Object> haMap = new HashMap<String, Object>();
		List<User> users = new ArrayList<User>(10000);
		for(int i = 0; i < 3500; i++){
			users.add(new User(i,"user"+i));
		}
		haMap.put("user",users);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		HessianOutput hessianOutput = new HessianOutput(outputStream);
		hessianOutput.writeObject(haMap);
		hessianOutput.flush();
		try {
			bb = Util.encrypt(outputStream.toByteArray());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.addIntHeader("Status Code",200);
		response.setContentLength(bb.length);
		responseStream.write(bb);
		responseStream.close();
	}

	
}
