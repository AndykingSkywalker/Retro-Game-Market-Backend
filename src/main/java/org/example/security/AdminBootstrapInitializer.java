package org.example.security;

import jakarta.annotation.PostConstruct;
import org.example.domain.User;
import org.example.domain.UserRole;
import org.example.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminBootstrapInitializer {

    private final AdminBootstrapProperties properties;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapInitializer(AdminBootstrapProperties properties,
                                     UserRepo userRepo,
                                     PasswordEncoder passwordEncoder) {
        this.properties = properties;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void ensureAdminUser() {
        if (!properties.isEnabled()) {
            return;
        }

        if (!StringUtils.hasText(properties.getUsername())
                || !StringUtils.hasText(properties.getEmail())
                || !StringUtils.hasText(properties.getPassword())) {
            throw new IllegalStateException("Admin bootstrap is enabled but username/email/password are not fully configured.");
        }

        if (userRepo.findByUsername(properties.getUsername()).isPresent()) {
            return;
        }

        User admin = new User();
        admin.setUsername(properties.getUsername());
        admin.setEmail(properties.getEmail());
        admin.setPassword(passwordEncoder.encode(properties.getPassword()));
        admin.setRole(UserRole.ADMIN);
        userRepo.save(admin);
    }
}

