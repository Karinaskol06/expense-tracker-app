package com.project.expense_tracker.Mapper;

import com.project.expense_tracker.DTO.CategoryDTO;
import com.project.expense_tracker.Entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setCategoryName(category.getCategoryName());
        categoryDTO.setCategoryType(category.getType());
        categoryDTO.setUserId(category.getUser().getId());
    }
}
