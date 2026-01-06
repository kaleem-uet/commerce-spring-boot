package com.example.commerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.commerce.entities.Role;
import com.example.commerce.entities.User;
import com.example.commerce.repositories.RoleRepository;
import com.example.commerce.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create roles first
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("ADMIN")
                            .level(3)
                            .build();
                    roleRepository.save(role);
                    log.info("ADMIN role created");
                    return role;
                });

        Role moderatorRole = roleRepository.findByName("MODERATOR")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("MODERATOR")
                            .level(2)
                            .build();
                    roleRepository.save(role);
                    log.info("MODERATOR role created");
                    return role;
                });

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = Role.builder()
                            .name("USER")
                            .level(1)
                            .build();
                    roleRepository.save(role);
                    log.info("USER role created");
                    return role;
                });

        // Create admin user if not exists (ADMIN has access to everything)
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .enabled(true)
                    .build();
            userRepository.save(admin);
            log.info("Admin user created - username: admin, password: admin123");
        }

        // Create moderator user if not exists
        if (!userRepository.existsByUsername("moderator")) {
            User moderator = User.builder()
                    .username("moderator")
                    .email("moderator@example.com")
                    .password(passwordEncoder.encode("mod123"))
                    .role(moderatorRole)
                    .enabled(true)
                    .build();
            userRepository.save(moderator);
            log.info("Moderator user created - username: moderator, password: mod123");
        }

        // Create regular user if not exists
        if (!userRepository.existsByUsername("user")) {
            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(userRole)
                    .enabled(true)
                    .build();
            userRepository.save(user);
            log.info("Regular user created - username: user, password: user123");
        }
    }
}
