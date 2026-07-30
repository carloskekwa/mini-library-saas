package com.library.config;

import com.library.entity.User;
import com.library.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * Ensures seed user passwords are properly encoded on startup.
 */
@Configuration
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner resetSeedPasswords(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String defaultPassword = "Admin1234!";
            String encoded = passwordEncoder.encode(defaultPassword);
            
            logger.info("=== DataSeeder: Resetting passwords ===");
            logger.info("Password encoder class: {}", passwordEncoder.getClass().getName());
            logger.info("Encoded hash: {}", encoded);
            logger.info("Self-verify matches: {}", passwordEncoder.matches(defaultPassword, encoded));

            userRepository.findByUsername("admin").ifPresent(user -> {
                String oldHash = user.getPasswordHash();
                user.setPasswordHash(encoded);
                User saved = userRepository.saveAndFlush(user);
                logger.info("Admin old hash: {}", oldHash);
                logger.info("Admin new hash: {}", saved.getPasswordHash());
                logger.info("Verify after save: {}", passwordEncoder.matches(defaultPassword, saved.getPasswordHash()));
            });

            userRepository.findByUsername("librarian").ifPresent(user -> {
                user.setPasswordHash(encoded);
                userRepository.saveAndFlush(user);
            });

            userRepository.findByUsername("member1").ifPresent(user -> {
                user.setPasswordHash(encoded);
                userRepository.saveAndFlush(user);
            });

            userRepository.findByUsername("member2").ifPresent(user -> {
                user.setPasswordHash(encoded);
                userRepository.saveAndFlush(user);
            });

            logger.info("=== DataSeeder: Complete ===");
        };
    }
}
