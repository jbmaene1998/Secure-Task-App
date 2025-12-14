package com.securetaskapp.service;

import com.securetaskapp.dto.TaskCreateRequest;
import com.securetaskapp.dto.TaskDonePatchRequest;
import com.securetaskapp.dto.TaskResponse;
import com.securetaskapp.dto.TaskUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse createTask(UUID userId, TaskCreateRequest request);

    List<TaskResponse> getTasksForUser(UUID userId, Boolean doneFilter);

    TaskResponse getTask(UUID userId, UUID taskId);

    TaskResponse updateTask(UUID userId, UUID taskId, TaskUpdateRequest request);

    TaskResponse updateTaskDone(UUID userId, UUID taskId, TaskDonePatchRequest request);

    void deleteTask(UUID userId, UUID taskId);
}
