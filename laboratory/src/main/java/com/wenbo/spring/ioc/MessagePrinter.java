package com.wenbo.spring.ioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagePrinter {

	@Autowired
	private MessageService messageService;
	
	
	public void print(){
		System.out.println(messageService.getMessage());
	}
}
