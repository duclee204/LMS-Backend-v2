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
        // Base path to 'uploads'
        String uploadBasePath = Paths.get("uploads").toAbsolutePath().toUri().toString(); // e.g., file:/C:/.../uploads/

        // Avatars
        registry.addResourceHandler("/images/avatars/**")
                .addResourceLocations(uploadBasePath + "avatars/");

        // Course images
        registry.addResourceHandler("/images/courses/**")
                .addResourceLocations(uploadBasePath + "imagescourse/");

        // General image fallback
        registry.addResourceHandler("/images/**")
                .addResourceLocations(uploadBasePath);

        // CVs
        registry.addResourceHandler("/cvs/**")
                .addResourceLocations(uploadBasePath + "cvs/");

        // Videos
        registry.addResourceHandler("/videos/**")
                .addResourceLocations(uploadBasePath + "videos/");

        // Module content files
        registry.addResourceHandler("/uploads/modules/**")
                .addResourceLocations(uploadBasePath + "modules/");
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

