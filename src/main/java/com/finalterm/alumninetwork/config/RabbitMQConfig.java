package com.finalterm.alumninetwork.config;

import com.finalterm.alumninetwork.service.EmailService;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
//@ComponentScan(basePackages ={
//        "com.finalterm.alumninetwork",
//        "com.finalterm.alumninetwork.controller",
//        "com.finalterm.alumninetwork.service",
//        "com.finalterm.alumninetwork.repository",
//        "com.finalterm.alumninetwork.exception"
//})
//@EnableRabbit
public class RabbitMQConfig {

    @Autowired
    private Environment environment;

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.host")));
        connectionFactory.setPort(Integer.parseInt(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.port"))));
        connectionFactory.setUsername(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.username")));
        connectionFactory.setPassword(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.password")));
        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        return new Queue(Objects.requireNonNull(environment.getProperty("rabbitmq.queue.name")), true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(environment.getProperty("rabbitmq.exchange.name"));
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(environment.getProperty("rabbitmq.rounting.key.name"));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

}
