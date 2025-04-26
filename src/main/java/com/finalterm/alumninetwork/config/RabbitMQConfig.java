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
    public Queue emailQueue() {
        return new Queue(Objects.requireNonNull(environment.getProperty("rabbitmq.queue.name")), true);
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange("emailExchange", true, false);
    }


    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(environment.getProperty("rabbitmq.routing.key.name"));
    }

    @Bean
    public DirectExchange reactionExchange() {
        return new DirectExchange(Objects.requireNonNull(environment.getProperty("rabbitmq.post.reaction.exchange.name")));
    }

    @Bean
    public Queue reactionQueue() {
        return new Queue(Objects.requireNonNull(environment.getProperty("rabbitmq.post.reaction.db.queue")));
    }

    @Bean
    public Binding reactionBinding(Queue reactionQueue, DirectExchange reactionExchange) {
        return BindingBuilder.bind(reactionQueue).to(reactionExchange).with(environment.getProperty("rabbitmq.post.reaction.save"));
    }

    @Bean
    public Queue reactionDeleteQueue() {
        return new Queue(Objects.requireNonNull(environment.getProperty("rabbitmq.post.reaction.delete.queue")));
    }

    @Bean
    public Binding reactionDeleteBinding(Queue reactionDeleteQueue, DirectExchange reactionExchange) {
        return BindingBuilder.bind(reactionDeleteQueue).to(reactionExchange()).with(environment.getProperty("rabbitmq.post.reaction.delete"));
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
    public ApplicationRunner runner(AmqpAdmin admin) {
        return args -> {
            admin.declareExchange(emailExchange());
            admin.declareQueue(emailQueue());
            admin.declareBinding(emailBinding(emailQueue(), emailExchange()));

            admin.declareExchange(reactionExchange());
            admin.declareQueue(reactionQueue());
            admin.declareBinding(reactionBinding(reactionQueue(), reactionExchange()));

            admin.declareBinding(reactionDeleteBinding(reactionDeleteQueue(), reactionExchange()));
            admin.declareQueue(reactionDeleteQueue());
        };
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
