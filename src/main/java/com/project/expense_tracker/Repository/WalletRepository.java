package com.project.expense_tracker.Repository;

import com.project.expense_tracker.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("SELECT w FROM Wallet w WHERE w.id = :walletId AND w.owner.id = :userId")
    Optional<Wallet> findByIdAndOwnerId(@Param("walletId") Long walletId, @Param("userId") Long userId);

    @Query("SELECT COUNT(w) > 0 FROM Wallet w WHERE w.id = :walletId AND w.owner.id = :userId")
    boolean existsByIdAndOwnerId(@Param("walletId") Long walletId, @Param("userId") Long userId);

    //find all the wallets for the specific user
    List<Wallet> findByOwnerId(Long userId);

    //get count of wallets that belong to the user
    @Query("SELECT COUNT(w) FROM Wallet w WHERE w.owner.id = :userId")
    int countByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId AND t.category.type = 'EXPENSE'")
    BigDecimal sumExpenseByWalletId(@Param("walletId") Long walletId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.wallet.id = :walletId AND t.category.type = 'INCOME'")
    BigDecimal sumIncomeByWalletId(@Param("walletId") Long walletId);
}
