package com.project.expense_tracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "transactions")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate transactionDate;
    private String note;
    private double amount;

    @Enumerated(EnumType.STRING)
    private Currencies currency;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Categories category;

    @ManyToOne()
    @JoinColumn(name = "label_id")
    private Labels label;

    @ManyToOne()
    @JoinColumn(name = "wallet_id")
    private Wallets wallet;
}
