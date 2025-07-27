package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Enable and Configure CSRF
            .csrf(csrf -> csrf
                // Use CookieCsrfTokenRepository to send the token as a cookie.
                // withHttpOnlyFalse() allows JavaScript on the frontend to read it.
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // Use a handler to ensure the token is available to the frontend.
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            
            // 3. Configure Authorization
            .authorizeHttpRequests(authorize -> authorize
                // You can permit all for this example, or secure endpoints as needed.
                .anyRequest().permitAll()
            );

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Ensure your frontend origin is allowed
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        // Allow common methods, including OPTIONS for pre-flight requests
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow credentials (cookies)
        configuration.setAllowCredentials(true);
        // Allow all headers, including the custom X-XSRF-TOKEN
        configuration.setAllowedHeaders(List.of("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}