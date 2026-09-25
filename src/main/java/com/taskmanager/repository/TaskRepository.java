package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUserId(Long userId);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    List<Task> findByUserIdAndStatus(Long userId, Status status);

    List<Task> findByUserIdAndPriority(Long userId, Priority priority);

    List<Task> findByUserIdAndCategoryId(Long userId, Long categoryId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate < :today AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate = :today")
    List<Task> findTasksDueToday(@Param("userId") Long userId, @Param("today") LocalDate today);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:categoryId IS NULL OR t.category.id = :categoryId)")
    List<Task> findByFilters(@Param("userId") Long userId,
                             @Param("status") Status status,
                             @Param("priority") Priority priority,
                             @Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Status status);
}
