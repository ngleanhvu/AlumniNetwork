//package com.finalterm.alumninetwork.config;
//
//import com.rabbitmq.client.ConnectionFactory;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//
//@Configuration
//@PropertySource("classpath:config.properties")
//public class RabbitMQConfig {
//
//    @Autowired
//    private Environment env;
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory factory = new CachingConnectionFactory(env.getProperty("spring.rabbitmq.host", "localhost"));
//        factory.setUsername(env.getProperty("spring.rabbitmq.username", "guest")); // Thay bằng username thật nếu cần
//        factory.setPassword(env.getProperty("spring.rabbitmq.password", "guest")); // Thay bằng password thật nếu cần
//        return factory.getRabbitConnectionFactory();
//    }
//
//    @Bean
//    public Queue emailQueue() {
//        return new Queue(env.getProperty("spring.rabbitmq.host", "localhost"), true);
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate template = new RabbitTemplate((org.springframework.amqp.rabbit.connection.ConnectionFactory) connectionFactory);
//        return template;
//    }
//
//    @Bean
//    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, EmailConsumer listener) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory((org.springframework.amqp.rabbit.connection.ConnectionFactory) connectionFactory);
//        container.setQueues(emailQueue());
//        container.setMessageListener(listener);
//        return container;
//    }
//}
