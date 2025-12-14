package com.securetaskapp.dto;

import java.time.Instant;
import java.util.UUID;

public class UserResponse {

    private UUID id;
    private String email;
    private String name;
    private String roles;
    private Instant createdAt;

    public UserResponse(UUID id, String email, String name, String roles, Instant createdAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getRoles() { return roles; }
}
