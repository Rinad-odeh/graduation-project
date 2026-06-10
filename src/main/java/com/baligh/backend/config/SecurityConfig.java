package com.baligh.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                // 1. السماح بالوصول من الرابط المرفوع ومن الـ localhost المطور محلياً لضمان عمل التجربة الفورية
                config.setAllowedOrigins(List.of(
                    "https://baligh-admin-production.up.railway.app",
                    "http://localhost:5173", // منفذ تطبيق Vite الافتراضي للفرونتند عندك
                    "http://localhost:3000"
                ));
                // 2. أضفنا ميثود PATCH لتتمكني من تحديث الأدوار وحالة المنظمات من لوحة التحكم بنجاح
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/v1/auth/admin/login").permitAll() // مسار الأدمن مفتوح وجاهز تماماً
                .anyRequest().permitAll()  
            )
            .headers(headers -> headers.frameOptions(f -> f.sameOrigin()));

        return http.build();
    }
}
