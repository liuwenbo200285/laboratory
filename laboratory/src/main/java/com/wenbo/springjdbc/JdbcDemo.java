package com.wenbo.springjdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wenbo.generics.User;

@Service
public class JdbcDemo {
	
	@Autowired
	private Jdbc jdbc;

	public static void main(String[] args) {
		JdbcDemo jdbcDemo = new JdbcDemo();
		jdbcDemo.test();
	}
	
	public void test(){
		System.out.println(jdbc.getList("select * from user",User.class).size());
	}
	
	public void getUser(){
		String sql = "select name,nick_name from user";
		List<User> users =  jdbc.getList(sql,User.class);
		users.stream().forEach((user)->{
			System.out.println(user.getName()+":"+user.getNickName()+":"+user.getId());
		});
		DataSourceContextHolder.setDataSource("dataSource1");
		users =  jdbc.getList(sql,User.class);
		System.out.println(users.size());
		new Thread(()->{
			List<User> users1 =  jdbc.getList(sql,User.class);
			System.out.println(DataSourceContextHolder.getDataSource());
			System.out.println(Thread.currentThread().getName()+":"+users1.size());
		}).start();
	}

}
