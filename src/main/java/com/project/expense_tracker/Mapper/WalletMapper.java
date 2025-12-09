package com.project.expense_tracker.Mapper;

import com.project.expense_tracker.DTO.WalletDTO.WalletDTO;
import com.project.expense_tracker.Entity.Wallet;
import org.springframework.stereotype.Component;


@Component
public class WalletMapper {
    public WalletDTO toDTO(Wallet wallet) {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId(wallet.getId());
        walletDTO.setName(wallet.getName());
        walletDTO.setBalance(wallet.getBalance());
        walletDTO.setCurrency(wallet.getCurrency());
        walletDTO.setOwnerId(wallet.getOwner().getId());

        return walletDTO;
    }
}