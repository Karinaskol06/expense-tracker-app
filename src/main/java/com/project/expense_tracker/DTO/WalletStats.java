package com.project.expense_tracker.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;

@Value //Lombok annotation for immutable class (generates final fields, getters, equals, hashCode, toString)
@Builder
@Getter
//statistic fields
public class WalletStats {
    int categoryCount;
    int transactionCount;
    BigDecimal totalIncome;
    BigDecimal totalExpense;
    BigDecimal balance;

    //static factory method for empty fields
    public static WalletStats empty() {
        return WalletStats.builder()
                .categoryCount(0)
                .transactionCount(0)
                .totalIncome(BigDecimal.ZERO)
                .totalExpense(BigDecimal.ZERO)
                .balance(BigDecimal.ZERO)
                .build();
    }

    //static factory method for fields with values
    public static WalletStats of(int categoryCount,
                                 int transactionCount,
                                 BigDecimal totalIncome,
                                 BigDecimal totalExpense,
                                 BigDecimal balance) {
        return WalletStats.builder()
                .categoryCount(categoryCount)
                .transactionCount(transactionCount)
                .totalIncome(totalIncome != null ? totalIncome : BigDecimal.ZERO)
                .totalExpense(totalExpense != null ? totalExpense : BigDecimal.ZERO)
                .balance(balance != null ? balance : BigDecimal.ZERO)
                .build();
    }

    //automatic balance calc
    public static WalletStats create(int categoryCount,
                                     int transactionCount,
                                     BigDecimal totalIncome,
                                     BigDecimal totalExpense) {
        BigDecimal safeIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        BigDecimal safeExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
        BigDecimal balance = safeIncome.add(safeExpense);

        return WalletStats.builder()
                .categoryCount(categoryCount)
                .transactionCount(transactionCount)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .build();
    }
}
