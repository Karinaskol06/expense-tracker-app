package com.project.expense_tracker.DTO;

import com.project.expense_tracker.Entity.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String categoryName;
    private CategoryType categoryType;
    private Long userId;
}
