package com.securetaskapp.dto;

import jakarta.validation.constraints.NotNull;

public class TaskDonePatchRequest {

    @NotNull
    private Boolean done;

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}
