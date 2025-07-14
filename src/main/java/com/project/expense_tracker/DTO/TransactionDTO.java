package com.project.expense_tracker.DTO;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.DecimalMin;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter

public class TransactionDTO implements Serializable {
    private Long id;

    @NotNull
    @PastOrPresent
    private String transactionDate;

    private String note;

    @NotNull
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private Long categoryId;
    private Long labelId;
    private Long walletId;
}
