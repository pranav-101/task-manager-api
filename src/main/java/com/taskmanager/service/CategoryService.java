package com.taskmanager.service;

import com.taskmanager.dto.request.CategoryRequest;
import com.taskmanager.dto.response.CategoryResponse;
import com.taskmanager.entity.Category;
import com.taskmanager.entity.User;
import com.taskmanager.exception.BadRequestException;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuthService authService;

    public List<CategoryResponse> getAllCategories() {
        Long userId = authService.getCurrentUser().getId();
        return categoryRepository.findByUserId(userId).stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {
        return CategoryResponse.fromEntity(findByIdAndUser(id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest req) {
        User user = authService.getCurrentUser();

        if (categoryRepository.existsByNameAndUserId(req.getName(), user.getId())) {
            throw new BadRequestException("Category already exists");
        }

        Category category = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .user(user)
                .build();

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest req) {
        Category category = findByIdAndUser(id);
        category.setName(req.getName());
        category.setDescription(req.getDescription());
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.delete(findByIdAndUser(id));
    }

    private Category findByIdAndUser(Long id) {
        Long userId = authService.getCurrentUser().getId();
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
    }
}
