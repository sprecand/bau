package com.bau.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration.
 * Enables JPA auditing for automatic timestamp management.
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
} 