package com.securetaskapp.dto;

import java.time.Instant;
import java.util.UUID;

public class TaskResponse {

    private UUID id;
    private String title;
    private String description;
    private boolean done;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID userId;

    public TaskResponse(UUID id,
                        String title,
                        String description,
                        boolean done,
                        Instant createdAt,
                        Instant updatedAt,
                        UUID userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.userId = userId;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isDone() { return done; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public UUID getUserId() { return userId; }
}
