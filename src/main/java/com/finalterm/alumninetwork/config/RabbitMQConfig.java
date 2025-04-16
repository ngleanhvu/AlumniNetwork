package com.finalterm.alumninetwork.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Objects;

@Configuration
@ComponentScan(basePackages ={
        "com.finalterm.alumninetwork",
        "com.finalterm.alumninetwork.controller",
        "com.finalterm.alumninetwork.service",
        "com.finalterm.alumninetwork.repository",
        "com.finalterm.alumninetwork.exception"
})
@EnableRabbit
@PropertySource("classpath:config.properties")
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
        connectionFactory.setVirtualHost(Objects.requireNonNull(environment.getProperty("spring.rabbitmq.virtual-host")));
        return connectionFactory;
    }

    @Bean
    public Queue queue() {
        return new Queue(Objects.requireNonNull(environment.getProperty("rabbitmq.queue.name")), true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("emailExchange", true, false);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange directExchange) {
        return BindingBuilder.bind(queue).to(directExchange).with(environment.getProperty("rabbitmq.routing.key.name"));
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean(name = "rabbitListenerContainerFactory")
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(1); // Đảm bảo có ít nhất 1 consumer
        factory.setMaxConcurrentConsumers(5); // Tối đa 5 consumer nếu cần
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public ApplicationRunner runner(AmqpAdmin amqpAdmin, Queue queue, DirectExchange exchange, Binding binding) {
        return args -> {
            amqpAdmin.declareQueue(queue);
            amqpAdmin.declareExchange(exchange);
            amqpAdmin.declareBinding(binding);
        };
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
