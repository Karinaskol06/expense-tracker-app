package com.project.expense_tracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table (name = "categories")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Categories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //for instance groceries, medicine
    //deposit, salary
    private String categoryName;

    //income or expense
    @Enumerated(EnumType.STRING)
    private CategoryTypes type;

    @OneToMany(mappedBy = "category")
    private List<Transactions> transactionList;

    @ManyToOne()
    @JoinColumn(name = "wallet_id")
    private Wallets wallet;

}
