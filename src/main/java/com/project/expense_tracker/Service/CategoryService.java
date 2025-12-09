package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.CategoryDTO;
import com.project.expense_tracker.Entity.Category;
import com.project.expense_tracker.Entity.User;
import com.project.expense_tracker.Mapper.CategoryMapper;
import com.project.expense_tracker.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryMapper categoryMapper;

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Category with ID " + id + " not found"));
    }

    public CategoryDTO createCategory(CategoryDTO category, Long userId) {
        User user = userService.getUserEntityById(userId);
        Category newCategory = Category.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .type(category.getCategoryType())
                .user(user)
                .build();
        Category savedCategory = categoryRepository.save(newCategory);
        return categoryMapper.toDTO(savedCategory);

    }
}
