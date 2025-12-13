package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.CategoryDTO;
import com.project.expense_tracker.Security.SecurityUtils;
import com.project.expense_tracker.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Validated
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        Long userId = securityUtils.getCurrentUserId();
        List<CategoryDTO> categories = categoryService.getCategoriesByUserId(userId);
        return buildResponse(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @PathVariable Long categoryId) {
        Long userId = securityUtils.getCurrentUserId();
        CategoryDTO category = categoryService.findCategoryDTOById(categoryId, userId);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
        Long userId = securityUtils.getCurrentUserId();
        CategoryDTO created = categoryService.createCategory(category, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Valid @RequestBody CategoryDTO category,
            @PathVariable Long categoryId) {
        Long userId = securityUtils.getCurrentUserId();
        CategoryDTO updated = categoryService.updateCategory(categoryId, category, userId);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

//    @DeleteMapping("/{categoryId}")
//    public ResponseEntity<Void> deleteCategory(
//            @PathVariable Long categoryId) {
//        Long userId = securityUtils.getCurrentUserId();
//        categoryService.deleteCategory(categoryId, userId);
//        return ResponseEntity.noContent().build();
//    }

    /* helper methods */
    public <T> ResponseEntity<List<T>> buildResponse(List<T> data) {
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
