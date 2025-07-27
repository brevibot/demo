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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Configure CORS to allow requests from the Next.js frontend
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. Configure CSRF protection
            .csrf(csrf -> csrf
                // Use CookieCsrfTokenRepository to send the token as a cookie to the frontend.
                // withHttpOnlyFalse() is necessary so JavaScript can read the cookie.
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // Use a custom handler to ensure the CSRF token is loaded on every request
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            
            // 3. Configure Authorization Rules
            .authorizeHttpRequests(authorize -> authorize
                // Allow GET requests to /api/hello without authentication to establish the session and get the CSRF token
                .requestMatchers("/api/hello").permitAll()
                // All other requests must be authenticated (in a real app)
                .anyRequest().permitAll() // For this example, we permit all to simplify.
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // IMPORTANT: Replace with your Next.js app's actual origin in production
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        // This is crucial for sending cookies across origins
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}