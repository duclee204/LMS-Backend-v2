package org.example.lmsbackend.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.Arrays;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map cho thư mục uploads tổng quát
        String uploadPath = Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadPath);
        registry.addResourceHandler("/cvs/**")
                .addResourceLocations(uploadPath + "cvs/");
        // Map riêng cho ảnh khóa học
        String courseImagesPath = Paths.get("uploads", "imagescourse").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/images/courses/**")
                .addResourceLocations(courseImagesPath);
        registry.addResourceHandler("/images/avatars/**")
                .addResourceLocations(uploadPath + "avatars/");

        // Map cho video
        String videosPath = Paths.get("uploads", "videos").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/videos/**")
                .addResourceLocations(videosPath);

        // Map cho module content files  
        String modulesPath = Paths.get("uploads", "modules").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/modules/**")
                .addResourceLocations(modulesPath);

    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Chỉ định origins cụ thể thay vì "*"
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        
        // Cho phép tất cả methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Cho phép tất cả headers
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Cho phép credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Áp dụng cho tất cả endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

