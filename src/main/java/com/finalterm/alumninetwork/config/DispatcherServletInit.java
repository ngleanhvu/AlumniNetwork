package com.finalterm.alumninetwork.config;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class DispatcherServletInit extends AbstractAnnotationConfigDispatcherServletInitializer {
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                HibernateConfig.class,
                ThymeleafConfig.class,
                AsyncConfig.class,
                RabbitMQConfig.class,
                SecurityConfig.class,
                RedisConfig.class,
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{
                WebApplicationContextConfig.class
        };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // Cấu hình upload file đa phần
        MultipartConfigElement multipartConfig = new MultipartConfigElement(
               null, // Thư mục lưu file tạm (thư mục tạm của hệ thống)
                5 * 1024 * 1024,    // Kích thước tối đa của mỗi file (5MB)
                20 * 1024 * 1024,   // Tổng kích thước tối đa của toàn bộ request (20MB)
                1 * 1024 * 1024     // Ngưỡng kích thước file (1MB) trước khi ghi ra ổ cứng
        );

        registration.setMultipartConfig(multipartConfig);
    }
}
