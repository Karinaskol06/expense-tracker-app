package com.project.expense_tracker.DTO.WalletDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.expense_tracker.Entity.Currency;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WalletDTO implements Serializable {
    private Long id;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal balance;

    private Currency currency;
    private Long ownerId;

}
