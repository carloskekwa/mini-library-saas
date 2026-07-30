package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Main Spring Boot application entry point for Mini Library Management System.
 * 
 * Features enabled:
 * - Caching with @EnableCaching for performance
 * - Scheduled tasks with @EnableScheduling for automated jobs
 * - Method-level security with @EnableMethodSecurity for fine-grained authorization
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

}
