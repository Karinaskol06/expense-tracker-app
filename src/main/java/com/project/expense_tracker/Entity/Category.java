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

public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //for instance groceries, medicine
    //deposit, salary
    private String categoryName;

    //income or expense
    @Enumerated(EnumType.STRING)
    private CategoryType type;

    @OneToMany(mappedBy = "category")
    private List<Transaction> transactionList;

//    @ManyToOne()
//    @JoinColumn(name = "wallet_id")
//    private Wallet wallet;

   //categories belong to the user
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

}
