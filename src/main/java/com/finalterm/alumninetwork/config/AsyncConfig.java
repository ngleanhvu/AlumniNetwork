package com.finalterm.alumninetwork.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@PropertySource("classpath:config.properties")
public class AsyncConfig {
    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create(); // Creates a basic registry
    }

}
