package com.project.expense_tracker.Repository;

import com.project.expense_tracker.Entity.Category;
import com.project.expense_tracker.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //transactions by wallet id
    List<Transaction> findByWalletId(Long walletId);

    //all user's transactions (from all the wallets)
    @Query("SELECT t FROM Transaction t WHERE t.wallet.owner.id = :userId")
    List<Transaction> findByUserId(@Param("userId") Long userId);

    //count transactions in a wallet
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.wallet.id = :walletId")
    int countByWalletId(@Param("walletId") Long walletId);

    //count distinct (different) categories in a wallet
    @Query("SELECT COUNT(DISTINCT t.category) FROM Transaction t WHERE t.wallet.id = :walletId")
    int countCategoriesByWalletId(@Param("walletId") Long walletId);

    //sum income for a wallet
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.wallet.id = :walletId AND t.amount > 0")
    BigDecimal sumIncomeByWalletId(@Param("walletId") Long walletId);

    //sum expenses for a wallet
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.wallet.id = :walletId AND t.amount < 0")
    BigDecimal sumExpenseByWalletId(@Param("walletId") Long walletId);

    //transactions by category for a user
    @Query("SELECT t FROM Transaction t WHERE t.category = :category AND t.wallet.owner.id = :userId")
    List<Transaction> findByCategoryAndUserId(@Param("category") Category category, @Param("userId") Long userId);

    //transactions by date range for a user
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end AND t.wallet.owner.id = :userId")
    List<Transaction> findByTransactionDateBetweenAndUserId(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("userId") Long userId);

    //transactions by date range for a wallet
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :start AND :end AND t.wallet.id = :walletId")
    List<Transaction> findByTransactionDateBetweenAndWalletId(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("walletId") Long walletId);

}
