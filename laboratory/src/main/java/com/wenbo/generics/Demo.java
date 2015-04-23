package com.wenbo.generics;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Demo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<User> persons = new ArrayList<User>();
		persons.add(new User());
		persons.add(new User());
		persons.add(new User());
		persons.add(new User());
		save(persons);
	}
	
	public static <T> void save(T t){
		Field [] fields = t.getClass().getDeclaredFields();
		if(fields != null
				&& fields.length > 0){
			for(Field field:fields){
				System.out.println(field.getName());
				String fileName = field.getName();
				String methodName = "set"+StringUtils.substring(fileName,0,1).toUpperCase()+StringUtils.substring(fileName,1,fileName.length());
				System.out.println(methodName);
				try {
					Method method = t.getClass().getMethod(methodName,field.getType());
					if(StringUtils.contains(field.getType().toString(),"Integer")){
						method.invoke(t,Integer.valueOf(11));
					}else if(StringUtils.contains(field.getType().toString(),"String")){
						method.invoke(t,"wenbo");
					}
					String getMethodName = "get"+StringUtils.substring(fileName,0,1).toUpperCase()+StringUtils.substring(fileName,1,fileName.length());
					System.out.println(getMethodName);
					Method getMethod = t.getClass().getMethod(getMethodName, new Class[]{});
					System.out.println(getMethod.invoke(t,new Object[]{}));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static <T> void save(List<T> ts){
		for(T t:ts){
			save(t);
		}
	}

}
