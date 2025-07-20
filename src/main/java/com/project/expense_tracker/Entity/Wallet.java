package com.project.expense_tracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "wallets")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @ManyToOne()
    @JoinColumn(name = "client_id")
    private Client owner;

//    @OneToMany(mappedBy = "wallet")
//    private List<Category> categoriesList;

    @OneToMany(mappedBy = "wallet")
    private List<Transaction> transactionsList;
}
