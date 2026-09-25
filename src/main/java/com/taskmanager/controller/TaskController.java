package com.taskmanager.controller;

import com.taskmanager.dto.request.StatusUpdateRequest;
import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.ApiResponse;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long categoryId) {

        List<TaskResponse> tasks;
        if (status != null || priority != null || categoryId != null) {
            tasks = taskService.getTasksByFilters(status, priority, categoryId);
        } else {
            tasks = taskService.getAllTasks();
        }
        return ResponseEntity.ok(ApiResponse.success(tasks));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse task = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Task created", task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTask(
            @PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Task updated", taskService.updateTask(id, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TaskResponse>> updateTaskStatus(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(taskService.updateTaskStatus(id, request.getStatus())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted"));
    }

    @GetMapping("/overdue")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getOverdueTasks() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getOverdueTasks()));
    }

    @GetMapping("/due-today")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getTasksDueToday() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTasksDueToday()));
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<TaskService.TaskStatistics>> getTaskStatistics() {
        return ResponseEntity.ok(ApiResponse.success(taskService.getTaskStatistics()));
    }
}
