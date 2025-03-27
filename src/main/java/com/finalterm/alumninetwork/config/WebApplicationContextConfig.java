package com.finalterm.alumninetwork.config;

import com.finalterm.alumninetwork.formatter.UserFormatter;
import com.finalterm.alumninetwork.pojo.User;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableTransactionManagement
@EnableWebMvc
@ComponentScan(basePackages = {
        "com.finalterm.alumninetwork",
        "com.finalterm.alumninetwork.controller",
        "com.finalterm.alumninetwork.service",
        "com.finalterm.alumninetwork.repository"

})
public class WebApplicationContextConfig implements WebMvcConfigurer {
    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new UserFormatter());
    }
}
