package org.example.rest.dto;

import org.example.domain.UserRole;

public class AuthTokenResponseDto {

    private String token;
    private UserRole role;

    public AuthTokenResponseDto() {
    }

    public AuthTokenResponseDto(String token, UserRole role) {
        this.token = token;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
