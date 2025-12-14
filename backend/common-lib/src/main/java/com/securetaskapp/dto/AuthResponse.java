package com.securetaskapp.dto;

public class AuthResponse {

    private String token;
    private UserResponse user;

    public AuthResponse(String token) {
        this.token = token;
    }

    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public UserResponse getUser() {
        return user;
    }
}
