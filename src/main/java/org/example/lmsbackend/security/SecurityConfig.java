package org.example.lmsbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/login", "/api/users/register").permitAll()
                        .requestMatchers("/api/courses/list").hasAnyRole("admin", "instructor")
                        .requestMatchers("/api/enrollments/**").hasAnyRole("admin", "instructor", "student")
                        .requestMatchers("/api/contents").hasAnyRole("admin", "instructor")
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/api/users/delete/**").hasRole("admin")
                        .requestMatchers("/uploads/avatars/**").permitAll() // ✅ Cho phép ảnh avatar
                        .requestMatchers("/uploads/modules/**").permitAll() // ✅ Cho phép truy cập content files
                        .requestMatchers("/uploads/imagescourse/**").permitAll() // ✅ Cho phép ảnh khóa học
                        .requestMatchers("/cvs/**").permitAll() // ✅ Cho phép truy cập file CV công khai
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
