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
        // Cấu hình upload file
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
                "/",   // Thư mục lưu file tạm
                5 * 1024 * 1024,  // Kích thước file tối đa (5MB)
                20 * 1024 * 1024, // Tổng kích thước request tối đa (20MB)
                0  // Ngưỡng lưu trên RAM trước khi ghi ra ổ cứng
        );

        registration.setMultipartConfig(multipartConfigElement);
    }
}
