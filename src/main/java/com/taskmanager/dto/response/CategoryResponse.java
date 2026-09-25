package com.taskmanager.dto.response;

import com.taskmanager.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private int taskCount;
    private LocalDateTime createdAt;

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .taskCount(category.getTasks() != null ? category.getTasks().size() : 0)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
