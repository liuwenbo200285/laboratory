package com.wenbo.springjdbc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringInit {
	
	static class ClassPathXmlApplicationContextHolder{
		private static ClassPathXmlApplicationContext springCtxHolder = new ClassPathXmlApplicationContext("applicationContext.xml");
	}

	private SpringInit(){
		
	}
	
	public static ApplicationContext getApplicationContext(){
		return ClassPathXmlApplicationContextHolder.springCtxHolder;
	}
	
	public static void main(String[] args){
		JdbcDemo jdbcDemo = getApplicationContext().getBean("jdbcDemo",JdbcDemo.class);
		jdbcDemo.getUser();
	}
}
