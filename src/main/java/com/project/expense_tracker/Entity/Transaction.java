package com.project.expense_tracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate transactionDate;
    private String note;
    private BigDecimal amount;

    @ManyToOne()
    @JoinColumn(name = "category_id")
    private Category category;

//    @ManyToOne()
//    @JoinColumn(name = "label_id")
//    private Label label;

    @ManyToOne()
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;
}
