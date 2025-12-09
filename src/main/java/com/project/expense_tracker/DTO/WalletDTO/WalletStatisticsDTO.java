package com.project.expense_tracker.DTO.WalletDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletStatisticsDTO implements Serializable {
    private int totalTransactions;
    private int uniqueCategories;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

    public BigDecimal getNetBalance() {
        return totalIncome.subtract(totalExpense);
    }

}
