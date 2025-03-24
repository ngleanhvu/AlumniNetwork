package com.finalterm.alumninetwork.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableTransactionManagement
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.finalterm.alumninetwork",
        "com.finalterm.alumninetwork.controller"
})
public class WebApplicationContextConfig implements WebMvcConfigurer {
    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
}
