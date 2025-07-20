package com.project.expense_tracker.DTO.WalletDTO;

import com.project.expense_tracker.Entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WalletDetailsDTO implements Serializable {
    private Long id;
    private String name;
    private BigDecimal balance;
    private Currency currency;
    private Long ownerId;
    private String ownerName;
    private int categoryCount;
    private int transactionCount;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;

}
