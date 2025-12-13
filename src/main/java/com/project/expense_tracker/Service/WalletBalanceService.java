package com.project.expense_tracker.Service;

import com.project.expense_tracker.Entity.CategoryType;
import com.project.expense_tracker.Entity.Transaction;
import com.project.expense_tracker.Entity.Wallet;
import com.project.expense_tracker.Repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.project.expense_tracker.Entity.CategoryType.EXPENSE;
import static com.project.expense_tracker.Entity.CategoryType.INCOME;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletBalanceService {

    private final WalletRepository walletRepository;

    public void handleTransactionCreated(Transaction transaction) {
        updateBalance(transaction.getWallet(), transaction.getAmount(),
                transaction.getCategory().getType(), true);
    }

    public void handleTransactionUpdated(Transaction oldTransaction, Transaction newTransaction) {
        if (oldTransaction != null){
            updateBalance(oldTransaction.getWallet(), oldTransaction.getAmount(),
                    oldTransaction.getCategory().getType(), false);
        }
        updateBalance(newTransaction.getWallet(), newTransaction.getAmount(),
                newTransaction.getCategory().getType(), true);
    }

    public void handleTransactionDeleted(Transaction transaction) {
        updateBalance(transaction.getWallet(), transaction.getAmount(),
                transaction.getCategory().getType(), false);
    }

    private void updateBalance(Wallet wallet, BigDecimal amount, CategoryType type, boolean isAdd) {
        BigDecimal adjustment = calculateAdjustment(amount, type, isAdd);
        BigDecimal newBalance = wallet.getBalance().add(adjustment);

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }

    public BigDecimal calculateAdjustment(BigDecimal amount, CategoryType type, boolean isAdd){
        if (type == CategoryType.EXPENSE){
            return isAdd ? amount.negate() : amount;
        } else {
            return isAdd ? amount : amount.negate();
        }
    }
}
