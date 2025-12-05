package com.project.expense_tracker.DTO;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class WalletStatisticsDTO {
    private int totalTransactions;
    private int uniqueCategories;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    public BigDecimal getNetBalance() {
        return totalIncome.subtract(totalExpense);
    }
}
