package com.project.expense_tracker.Mapper;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.Entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionMapper {

    public TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setTransactionDate(LocalDate.parse(transaction.getTransactionDate().toString()));
        dto.setNote(transaction.getNote());
        dto.setAmount(transaction.getAmount());

        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
        }

//        if (transaction.getLabel() != null) {
//            dto.setLabelId(transaction.getLabel().getId());
//        }

        if (transaction.getWallet() != null) {
            dto.setWalletId(transaction.getWallet().getId());
        }

        return dto;
    }

    public Transaction toEntity(TransactionDTO dto, Category category, Wallet wallet) {
        Transaction entity = new Transaction();
        entity.setId(dto.getId());
        entity.setTransactionDate(dto.getTransactionDate());
        entity.setNote(dto.getNote());
        entity.setAmount(dto.getAmount());

        entity.setCategory(category);
//        entity.setLabel(label);
        entity.setWallet(wallet);

        return entity;
    }
}
