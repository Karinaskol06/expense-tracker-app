package com.project.expense_tracker.Service;

import com.project.expense_tracker.Entity.CategoryType;
import com.project.expense_tracker.Entity.Transaction;
import com.project.expense_tracker.Entity.Wallet;
import com.project.expense_tracker.Repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class WalletBalanceService {

    private final WalletRepository walletRepository;

    @Transactional
    public void updateWalletBalanceAfterTransCreate(Transaction transaction) {
        updateWalletBalance(
                transaction.getWallet(),
                transaction.getAmount(),
                transaction.getCategory().getType(),
                OperationType.ADD
        );
    }

    @Transactional
    public void updateWalletBalanceAfterTransUpdate(
            Wallet oldWallet, BigDecimal oldAmount, CategoryType oldType,
            Wallet newWallet, BigDecimal newAmount, CategoryType newType) {
        //roll back the transaction, go back to initial wallet state without this transaction
        if (oldWallet != null) {
            updateWalletBalance(oldWallet, oldAmount, oldType, OperationType.SUBTRACT);
        }
        updateWalletBalance(newWallet, newAmount, newType, OperationType.ADD);
    }

    //after deleting transaction
    @Transactional
    public void reverseWalletBalance(Transaction transaction) {
        updateWalletBalance(
                transaction.getWallet(),
                transaction.getAmount(),
                transaction.getCategory().getType(),
                OperationType.SUBTRACT
        );
    }


    //helper for updating wallet balance after creation/deletion of transactions
    public void updateWalletBalance(Wallet wallet, BigDecimal amount, CategoryType type, OperationType operation){
        BigDecimal adjustment = calculateAdjustment(amount, type, operation);
        BigDecimal newBalance = wallet.getBalance().add(adjustment);

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);
    }

    public BigDecimal calculateAdjustment(BigDecimal amount, CategoryType type, OperationType operation){
        BigDecimal toAdjust = switch(type) {
            case INCOME -> amount; //positive num
            case EXPENSE -> amount.negate(); //negative num
            default -> throw new IllegalArgumentException("Invalid category type" + type);
        };
        //if it was an expense - convert it to income and vice versa
        return operation == OperationType.SUBTRACT ? toAdjust.negate() : toAdjust;
    }

    private enum OperationType {
        ADD,
        SUBTRACT
    }
}
