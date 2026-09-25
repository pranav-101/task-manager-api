package com.taskmanager.service;

import com.taskmanager.dto.request.TaskRequest;
import com.taskmanager.dto.response.TaskResponse;
import com.taskmanager.entity.Category;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.CategoryRepository;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;

    public List<TaskResponse> getAllTasks() {
        Long userId = authService.getCurrentUser().getId();
        return taskRepository.findByUserId(userId).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public List<TaskResponse> getTasksByFilters(Status status, Priority priority, Long categoryId) {
        Long userId = authService.getCurrentUser().getId();
        return taskRepository.findByFilters(userId, status, priority, categoryId).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public TaskResponse getTaskById(Long id) {
        Task task = findTaskByIdAndUser(id);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse createTask(TaskRequest req) {
        User user = authService.getCurrentUser();

        Task task = Task.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .priority(req.getPriority() != null ? req.getPriority() : Priority.MEDIUM)
                .status(req.getStatus() != null ? req.getStatus() : Status.PENDING)
                .dueDate(req.getDueDate())
                .user(user)
                .build();

        if (req.getCategoryId() != null) {
            task.setCategory(findCategoryByIdAndUser(req.getCategoryId(), user.getId()));
        }

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest req) {
        Task task = findTaskByIdAndUser(id);
        User user = authService.getCurrentUser();

        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        if (req.getPriority() != null) task.setPriority(req.getPriority());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        if (req.getDueDate() != null) task.setDueDate(req.getDueDate());

        if (req.getCategoryId() != null) {
            task.setCategory(findCategoryByIdAndUser(req.getCategoryId(), user.getId()));
        } else {
            task.setCategory(null);
        }

        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, Status status) {
        Task task = findTaskByIdAndUser(id);
        task.setStatus(status);
        return TaskResponse.fromEntity(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.delete(findTaskByIdAndUser(id));
    }

    public List<TaskResponse> getOverdueTasks() {
        Long userId = authService.getCurrentUser().getId();
        return taskRepository.findOverdueTasks(userId, LocalDate.now()).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public List<TaskResponse> getTasksDueToday() {
        Long userId = authService.getCurrentUser().getId();
        return taskRepository.findTasksDueToday(userId, LocalDate.now()).stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    public TaskStatistics getTaskStatistics() {
        Long userId = authService.getCurrentUser().getId();
        return TaskStatistics.builder()
                .total(taskRepository.findByUserId(userId).size())
                .pending(taskRepository.countByUserIdAndStatus(userId, Status.PENDING))
                .inProgress(taskRepository.countByUserIdAndStatus(userId, Status.IN_PROGRESS))
                .completed(taskRepository.countByUserIdAndStatus(userId, Status.COMPLETED))
                .overdue((long) taskRepository.findOverdueTasks(userId, LocalDate.now()).size())
                .build();
    }

    private Task findTaskByIdAndUser(Long id) {
        Long userId = authService.getCurrentUser().getId();
        return taskRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
    }

    private Category findCategoryByIdAndUser(Long categoryId, Long userId) {
        return categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TaskStatistics {
        private int total;
        private Long pending;
        private Long inProgress;
        private Long completed;
        private Long overdue;
    }
}
