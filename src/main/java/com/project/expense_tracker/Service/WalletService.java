package com.project.expense_tracker.Service;

import com.project.expense_tracker.Entity.Wallet;
import com.project.expense_tracker.Repository.WalletRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class WalletService {
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Wallet findWalletById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("No wallet found with id: " + id));
    }
}
