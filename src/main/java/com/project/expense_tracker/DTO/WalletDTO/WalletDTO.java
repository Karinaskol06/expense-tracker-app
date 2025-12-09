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
public class WalletDTO implements Serializable {
    private Long id;
    private String name;
    private BigDecimal balance;
    private Currency currency;
    private Long ownerId;
}
