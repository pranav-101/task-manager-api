package com.taskmanager.dto.request;

import com.taskmanager.enums.Priority;
import com.taskmanager.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 500)
    private String description;

    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Long categoryId;
}
