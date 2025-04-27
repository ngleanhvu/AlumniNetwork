package com.finalterm.alumninetwork.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplateString(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        // Key and hash key serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value and hash value serializers
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();

        return template;
    }

    @Bean
    public RedisTemplate<String, Integer> redisTemplateInteger(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);
        // Key and hash key serializers
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Value and hash value serializers
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Integer.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Integer.class)); //Hash to Integer

        template.afterPropertiesSet();

        return template;
    }



    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        // Định dạng date-time dưới dạng ISO
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Cấu hình khác cho ObjectMapper
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        return mapper;
    }
}
