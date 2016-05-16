package com.yimeicloud.study.message_rabbitmq;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Server implements CommandLineRunner {
	private final static String EXCHANGE_NAME = "spring-boot-exchange";

	@Autowired
	AnnotationConfigApplicationContext context;

	@Autowired
	RabbitTemplate template;
	
	@Bean
	TopicExchange exchange() {
		return new TopicExchange(EXCHANGE_NAME);
	}

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Sending message...");
		template.convertAndSend(EXCHANGE_NAME, "spring.boot.key", "this is spring.boot.key!");
		template.convertAndSend(EXCHANGE_NAME, "mybatis.test", "this is not spring.boot.key!");
		context.close();
	}
}
