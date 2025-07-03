package com.project.expense_tracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "wallets")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Wallets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double balance;

    @Enumerated(EnumType.STRING)
    private Currencies currency;

    @ManyToOne()
    @JoinColumn(name = "client_id")
    private Clients owner;

    @OneToMany(mappedBy = "wallet")
    private List<Categories> categoriesList;

    @OneToMany(mappedBy = "wallet")
    private List<Transactions> transactionsList;
}
