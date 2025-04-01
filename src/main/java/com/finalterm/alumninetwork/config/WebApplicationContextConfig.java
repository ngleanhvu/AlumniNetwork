package com.finalterm.alumninetwork.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.finalterm.alumninetwork.formatter.UserFormatter;
import com.finalterm.alumninetwork.pojo.User;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.format.FormatterRegistry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
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

    @Autowired
    private Environment env;

    @Override
    public void configureDefaultServletHandling(
            DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new UserFormatter());
    }

    @Bean
    public Cloudinary cloudinary() {
        // Replace with your Cloudinary credentials
        String cloudName = env.getProperty("cloudinary.cloud_name");
        String apiKey = env.getProperty("cloudinary.cloud.api_key");
        String apiSecret = env.getProperty("cloudinary.cloud.api_secret");

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
