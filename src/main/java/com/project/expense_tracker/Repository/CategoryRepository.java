package com.project.expense_tracker.Repository;

import com.project.expense_tracker.DTO.CategoryDTO;
import com.project.expense_tracker.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserId(Long userId);

    @Query("SELECT c FROM Category c WHERE c.id = :categoryId AND c.user.id = :userId")
    Optional<Category> findByIdAndUserId(@Param("categoryId") Long categoryId, @Param("userId") Long userId);
}
