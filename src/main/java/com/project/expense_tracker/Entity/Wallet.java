package com.project.expense_tracker.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "wallets")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User owner;

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    private List<Transaction> transactionsList;
}
