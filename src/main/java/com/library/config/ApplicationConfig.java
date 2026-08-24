package com.library.config;

import com.library.security.JwtAuthenticationFilter;
import com.library.security.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * General application configuration and bean definitions.
 * 
 * Includes:
 * - ModelMapper for entity-to-DTO conversions
 * - Auditor provider for tracking entity changes
 * - JWT Authentication Filter bean
 */
@Configuration
public class ApplicationConfig {

    /**
     * ModelMapper bean for convenient DTO conversions.
     * Used for transforming JPA entities to DTOs and vice versa.
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
            .setAmbiguityIgnored(true);
        return mapper;
    }

    /**
     * JWT Authentication Filter bean.
     * Validates JWT tokens on each request.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                                           UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    }

    /**
     * Provides the current authenticated user for entity auditing.
     * Tracks who created or modified entities.
     */
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return Optional.of(authentication.getName());
            }
            return Optional.of("SYSTEM");
        };
    }

}
