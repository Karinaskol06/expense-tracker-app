package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.CategoryDTO;
import com.project.expense_tracker.Entity.Category;
import com.project.expense_tracker.Entity.User;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import com.project.expense_tracker.Exceptions.UnauthorizedException;
import com.project.expense_tracker.Mapper.CategoryMapper;
import com.project.expense_tracker.Repository.CategoryRepository;
import com.project.expense_tracker.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final CategoryMapper categoryMapper;
    private final TransactionRepository transactionRepository;

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Category with ID " + id + " not found"));
    }

    public CategoryDTO findCategoryDTOById(Long categoryId, Long userId) {
        Category category = validateCategoryOwnership(categoryId, userId);
        return categoryMapper.toDTO(category);
    }

    public List<CategoryDTO> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
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

    @Transactional
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO, Long userId) {
        Category updatedCategory = validateCategoryOwnership(categoryId, userId);
        updatedCategory.setCategoryName(categoryDTO.getCategoryName());
        updatedCategory.setType(categoryDTO.getCategoryType());
        Category savedCategory = categoryRepository.save(updatedCategory);

        return categoryMapper.toDTO(savedCategory);
    }

    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category deletedCategory = validateCategoryOwnership(categoryId, userId);
        //transactions may be without category
        transactionRepository.updateCategoryToNull(categoryId, userId);
        categoryRepository.delete(deletedCategory);
    }

    /* helper methods */
    public Category validateCategoryOwnership(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or access denied"));
        if (!category.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("User not found or access denied");
        }
        return category;
    }
}
