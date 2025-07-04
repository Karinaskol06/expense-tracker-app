package com.project.expense_tracker.Service;

import com.project.expense_tracker.Entity.Category;
import com.project.expense_tracker.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("Category with ID " + id + " not found"));
    }
}
