package com.finalterm.alumninetwork.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public ObservationRegistry observationRegistry() {
        //return ObservationRegistry.create(); // Creates a basic registry
        return ObservationRegistry.NOOP;
    }

}
