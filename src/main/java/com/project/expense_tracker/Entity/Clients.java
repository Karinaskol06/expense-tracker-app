package com.project.expense_tracker.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table (name = "clients")

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Clients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String email;
    private String phone;
    private String country;

    @OneToOne()
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "owner")
    private List<Wallets> walletsList;
}
