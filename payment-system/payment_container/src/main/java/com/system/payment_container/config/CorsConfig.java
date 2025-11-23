package com.system.payment_container.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${spring.web.cors.allowed-origins:http://localhost:3000,http://127.0.0.1:3000}")
    private String allowedOrigins;

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;

    @Value("${spring.web.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${spring.web.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${spring.web.cors.max-age:3600}")
    private long maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply CORS to all endpoints with wildcard pattern
        registry.addMapping("/**")
                .allowedOrigins(parseOrigins().toArray(new String[0]))
                .allowedMethods(parseMethods().toArray(new String[0]))
                .allowedHeaders(parseHeaders().toArray(new String[0]))
                .allowCredentials(allowCredentials)
                .maxAge(maxAge);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Apply configuration from properties
        configuration.setAllowedOrigins(parseOrigins());
        configuration.setAllowedMethods(parseMethods());
        configuration.setAllowedHeaders(parseHeaders());
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        // Expose useful headers for frontend
        configuration.setExposedHeaders(Arrays.asList(
            "Content-Type",
            "Authorization",
            "X-Total-Count",
            "X-Page-Count",
            "X-Request-ID"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private List<String> parseOrigins() {
        return Arrays.asList(allowedOrigins.split(","));
    }

    private List<String> parseMethods() {
        return Arrays.asList(allowedMethods.split(","));
    }

    private List<String> parseHeaders() {
        if ("*".equals(allowedHeaders.trim())) {
            return List.of("*");
        }
        return Arrays.asList(allowedHeaders.split(","));
    }
}