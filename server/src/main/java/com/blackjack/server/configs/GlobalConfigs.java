package com.blackjack.server.configs;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalConfigs implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowCredentials(true)
                .allowedOrigins("http://localhost:8080/")
                .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", "Application", "X-CSRF-TOKEN")
                .allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PATCH");
    }
}
