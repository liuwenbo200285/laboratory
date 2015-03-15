package com.wenbo.spring.ioc;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SpringIocTest {

	@Bean
    MessageService mockMessageService() {
        return new MessageService() {
        	
            public String getMessage() {
              return "Hello World!";
            }
        };
    }
	
	public static void main(String[] args) {
		@SuppressWarnings("resource")
		ApplicationContext context = 
		          new AnnotationConfigApplicationContext(SpringIocTest.class);
		      MessagePrinter printer = context.getBean(MessagePrinter.class);
		      printer.print();
	      
	}

}
