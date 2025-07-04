package com.project.expense_tracker.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter

public class TransactionDTO {
    private String transactionDate;
    private String note;
    private BigDecimal amount;
    private String currency;
    private Long categoryId;
    private Long labelId;
    private Long walletId;
}
