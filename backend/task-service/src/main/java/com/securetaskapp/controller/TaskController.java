package com.securetaskapp.controller;

import com.securetaskapp.dto.TaskCreateRequest;
import com.securetaskapp.dto.TaskDonePatchRequest;
import com.securetaskapp.dto.TaskResponse;
import com.securetaskapp.dto.TaskUpdateRequest;
import com.securetaskapp.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private UUID getCurrentUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Missing authentication");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UUID userId) {
            return userId;
        }

        // In case some config sets principal as a String UUID
        if (principal instanceof String s) {
            try { return UUID.fromString(s); } catch (IllegalArgumentException ignored) {}
        }

        throw new AccessDeniedException("Invalid authentication principal");
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            Authentication authentication,
            @RequestParam(value = "done", required = false) Boolean done
    ) {
        UUID userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(taskService.getTasksForUser(userId, done));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            Authentication authentication,
            @Valid @RequestBody TaskCreateRequest request
    ) {
        UUID userId = getCurrentUserId(authentication);
        TaskResponse created = taskService.createTask(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(Authentication authentication, @PathVariable("id") UUID id) {
        UUID userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(taskService.getTask(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            Authentication authentication,
            @PathVariable("id") UUID id,
            @Valid @RequestBody TaskUpdateRequest request
    ) {
        UUID userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(taskService.updateTask(userId, id, request));
    }

    @PatchMapping("/{id}/done")
    public ResponseEntity<TaskResponse> updateTaskDone(
            Authentication authentication,
            @PathVariable("id") UUID id,
            @Valid @RequestBody TaskDonePatchRequest request
    ) {
        UUID userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(taskService.updateTaskDone(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication authentication, @PathVariable("id") UUID id) {
        UUID userId = getCurrentUserId(authentication);
        taskService.deleteTask(userId, id);
        return ResponseEntity.noContent().build();
    }
}
