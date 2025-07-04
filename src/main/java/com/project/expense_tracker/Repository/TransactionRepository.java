package com.project.expense_tracker.Repository;

import com.project.expense_tracker.Entity.Category;
import com.project.expense_tracker.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCategory(Category category);
    List<Transaction> findByTransactionDateBetween(LocalDate start, LocalDate end);
}
