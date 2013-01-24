package com.wenbo.http;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("trst",1);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Kryo kryo = new Kryo();
		kryo.register(HashMap.class);
		Output output = new Output(outputStream);
		kryo.writeClassAndObject(output,map);
		output.flush();
		output.close();
		Input input = new Input(outputStream.toByteArray());
		map = (Map<String, Object>) kryo.readClassAndObject(input);
		System.out.println(map.get("trst"));
	}

}
