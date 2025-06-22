package com.bau.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Local development security configuration.
 * Disables authentication when 'local' profile is active.
 */
@Configuration
@EnableWebSecurity
@Profile("local")
public class LocalSecurityConfig {
    
    /**
     * Security filter chain for local development.
     * Permits all requests without authentication.
     * 
     * @param http the HTTP security configuration
     * @return the security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for local development
            .csrf(AbstractHttpConfigurer::disable)
            // Enable CORS (configuration provided by CorsConfig)
            .cors(cors -> {})
            // Permit all requests without authentication
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
} 
