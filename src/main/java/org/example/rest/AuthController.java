package org.example.rest;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.example.rest.dto.UserResponseDto;
import org.example.rest.dto.AuthLoginRequestDto;
import org.example.rest.dto.AuthTokenResponseDto;
import org.example.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    private final UserServices userServices;

    public AuthController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/login")
    @SecurityRequirements
    @Operation(
            summary = "Login and get JWT token",
            description = "Returns { token, role }. Copy the token value, click Authorize in Swagger, and paste: Bearer <token>."
    )
    public ResponseEntity<AuthTokenResponseDto> login(@Valid @RequestBody AuthLoginRequestDto loginRequest) {
        return userServices.login(loginRequest);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current authenticated user",
            description = "Requires Authorization header. In Swagger, use Authorize with: Bearer <token>."
    )
    public ResponseEntity<UserResponseDto> me(Authentication authentication) {
        return userServices.getCurrentUser(authentication.getName());
    }
}

