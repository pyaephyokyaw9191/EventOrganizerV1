package com.eventorganizer.auth.config;

import com.eventorganizer.auth.model.Role;
import com.eventorganizer.auth.model.User;
import com.eventorganizer.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Check if test user already exists
            if (!userRepository.existsByUsername("testuser")) {
                User testUser = User.builder()
                        .username("testuser")
                        .email("test@example.com")
                        .password(passwordEncoder.encode("Test123!"))
                        .role(Role.ATTENDEE)
                        .build();
                userRepository.save(testUser);
                System.out.println("Test user created: testuser / Test123!");
            }
        };
    }
} 