package com.yimeicloud.study.message_rabbitmq;

import java.util.concurrent.TimeUnit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App implements CommandLineRunner {
	final static String queueName = "spring-boot";
	
	@Autowired
	AnnotationConfigApplicationContext context;
	
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	// queue
	@Bean
	Queue queue() {
		return new Queue(queueName, false);
	}
	
	// exchange
	@Bean
	TopicExchange exchange() {
		return new TopicExchange("spring-boot-exchange");
	}
	
	// binding
	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(queueName);
	}
	
	@Bean
	Receiver receiver() {
		return new Receiver();
	}
	
	@Bean
	MessageListenerAdapter listenerAdapter(Receiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
	
	/*@Bean
	ConnectionFactory factory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost("localhost");
		factory.setUsername("zcm");
		factory.setPassword("zcm");
		return factory;
	}*/
	
	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory factory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(factory);
		container.setQueueNames(queueName);
		container.setMessageListener(listenerAdapter);
		
		return container;
	}
	
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    }

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Waiting five seconds...");
        Thread.sleep(5000);
        System.out.println("Sending message...");
        rabbitTemplate.convertAndSend(queueName, "Hello from RabbitMQ!");
        receiver().getLatch().await(10000, TimeUnit.MILLISECONDS);
        context.close();
	}
}
