package com.securetaskapp.service;

import com.securetaskapp.common.NotFoundException;
import com.securetaskapp.dto.TaskCreateRequest;
import com.securetaskapp.dto.TaskDonePatchRequest;
import com.securetaskapp.dto.TaskResponse;
import com.securetaskapp.dto.TaskUpdateRequest;
import com.securetaskapp.model.Task;
import com.securetaskapp.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskResponse createTask(UUID userId, TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDone(false);
        task.setUserId(userId);

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksForUser(UUID userId, Boolean doneFilter) {
        List<Task> tasks = (doneFilter == null)
                ? taskRepository.findByUserId(userId)
                : taskRepository.findByUserIdAndDone(userId, doneFilter);

        return tasks.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTask(UUID userId, UUID taskId) {
        return mapToResponse(findTaskForUserOrThrow(userId, taskId));
    }

    @Override
    public TaskResponse updateTask(UUID userId, UUID taskId, TaskUpdateRequest request) {
        Task task = findTaskForUserOrThrow(userId, taskId);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        if (request.getDone() != null) {
            task.setDone(request.getDone());
        }

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse updateTaskDone(UUID userId, UUID taskId, TaskDonePatchRequest request) {
        Task task = findTaskForUserOrThrow(userId, taskId);
        task.setDone(request.getDone());

        return mapToResponse(taskRepository.save(task));
    }

    @Override
    public void deleteTask(UUID userId, UUID taskId) {
        taskRepository.delete(findTaskForUserOrThrow(userId, taskId));
    }

    private Task findTaskForUserOrThrow(UUID userId, UUID taskId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Task not found"));
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isDone(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getUserId()
        );
    }
}
