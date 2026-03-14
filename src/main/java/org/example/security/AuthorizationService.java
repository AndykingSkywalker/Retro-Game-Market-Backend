package org.example.security;

import org.example.repo.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("authorizationService")
public class AuthorizationService {

    private final UserRepo userRepo;

    public AuthorizationService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public boolean isSelfOrAdmin(Authentication authentication, int userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return true;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof String username)) {
            return false;
        }

        return userRepo.findByUsername(username)
                .map(user -> user.getId() == userId)
                .orElse(false);
    }

    public boolean hasUsernameOrAdmin(Authentication authentication, String username) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return true;
        }

        Object principal = authentication.getPrincipal();
        return principal instanceof String authenticatedUsername && authenticatedUsername.equals(username);
    }
}
