package com.finalterm.alumninetwork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:dummy.properties", factory = DotenvPropertySourceFactory.class)
public class DotenvPropertyConfig {

}
