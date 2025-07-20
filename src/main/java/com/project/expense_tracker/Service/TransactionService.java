package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.Entity.*;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import com.project.expense_tracker.Exceptions.UnauthorizedException;
import com.project.expense_tracker.Mapper.TransactionMapper;
import com.project.expense_tracker.Repository.TransactionRepository;
import com.project.expense_tracker.Repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;
//    private final LabelService labelService;
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    //get all the user's transactions from all wallets (rewritten)
    public List<TransactionDTO> findAllUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //find transactions by wallet id and user authorization (rewritten)
    public List<TransactionDTO> findUserTransactionsByWalletId(Long walletId, Long userId) {
        //verify that user owns the wallet
        if (!walletRepository.existsByIdAndOwner_User_Id(walletId, userId)) {
            throw new UnauthorizedException("Wallet not found or you don't have access to it");
        }

        return transactionRepository.findByWalletId(walletId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //find transaction by id with user auth (rewritten)
    public TransactionDTO findById(Long id, Long userId ) {
        //confirmation that transaction exists
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));

        //verify that user owns the wallet that contains transaction
        if (!transaction.getWallet().getOwner().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this transaction.");
        }
        return transactionMapper.toDTO(transaction);
    }

    //create new transaction with user auth (rewritten, no upd mathod)
    public TransactionDTO create(TransactionDTO transactionDTO, Long userId) {
        //verify that user owns the wallet
        Wallet wallet = walletService.findWalletById(transactionDTO.getWalletId());
        if (!wallet.getOwner().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this wallet.");
        }

        Category category = categoryService.findCategoryById(transactionDTO.getCategoryId());

        Transaction transaction = transactionMapper.toEntity(transactionDTO, category, wallet);

        updateWalletBalance(wallet, transaction.getAmount(), category.getType());

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    //update transaction with user auth
    public TransactionDTO update(Long id, TransactionDTO transactionDTO, Long userId) {

        Transaction transactionUpd = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));

        //verify that user owns the transaction
        if (!transactionUpd.getWallet().getOwner().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this transaction.");
        }
        //get new entities
        Category newCategory = categoryService.findCategoryById(transactionDTO.getCategoryId());
        if (!newCategory.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this category.");
        }

        //store old values for balance
        BigDecimal oldAmount = transactionUpd.getAmount();
        CategoryType oldType = transactionUpd.getCategory().getType();
        Wallet wallet = transactionUpd.getWallet();

        transactionUpd.setTransactionDate(transactionDTO.getTransactionDate());
        transactionUpd.setNote(transactionDTO.getNote());
        transactionUpd.setAmount(transactionDTO.getAmount());
        transactionUpd.setCategory(newCategory);

        updateWalletBalance(wallet, oldAmount, oldType, transactionDTO.getAmount(), newCategory.getType());

        Transaction saved = transactionRepository.save(transactionUpd);
        return transactionMapper.toDTO(saved);
    }

    //helper to update wallet balance for transaction updates (added)
    public void updateWalletBalance(Wallet wallet, BigDecimal oldAmount, CategoryType oldType,
                                    BigDecimal newAmount, CategoryType newType) {
        BigDecimal currentBalance = wallet.getBalance();

        //going back to the balance before done transaction
        if (oldType == CategoryType.INCOME) {
            currentBalance = currentBalance.subtract(oldAmount);
        } else {
            currentBalance = currentBalance.add(oldAmount);
        }

        //normally calculating new balance
        if (newType == CategoryType.INCOME) {
            currentBalance = currentBalance.add(newAmount);
        } else {
            currentBalance = currentBalance.subtract(newAmount);
        }

        wallet.setBalance(currentBalance);
        walletRepository.save(wallet);
    }

    //helper for updating wallet balance after creation/deletion of transactions
    public void updateWalletBalance(Wallet wallet, BigDecimal amount, CategoryType type){
        BigDecimal currentBalance = wallet.getBalance();
        if (type == CategoryType.INCOME) {
            currentBalance = currentBalance.add(amount);
        } else {
            currentBalance = currentBalance.subtract(amount);
        }
        wallet.setBalance(currentBalance);
        walletRepository.save(wallet);
    }

    public void deleteById(Long id, Long userId) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));

        if (!transaction.getWallet().getOwner().getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this transaction");
        }

        //update wallet balance (reverse the transaction)
        updateWalletBalance(transaction.getWallet(),
                transaction.getAmount().negate(),
                transaction.getCategory().getType());

        transactionRepository.deleteById(id);
    }

    //get transactions by category for a user
    public List<TransactionDTO> findByCategoryAndUserId(Long categoryId, Long userId) {
        Category category = categoryService.findCategoryById(categoryId);
        return transactionRepository.findByCategoryAndUserId(category, userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //get transactions by date range for a user
    public List<TransactionDTO> findByDateRangeAndUserId(LocalDate start, LocalDate end, Long userId) {
        return transactionRepository.findByTransactionDateBetweenAndUserId(start, end, userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //get transactions by date range for a wallet
    public List<TransactionDTO> findByDateRangeAndWalletId(LocalDate start, LocalDate end, Long walletId) {
        return transactionRepository.findByTransactionDateBetweenAndWalletId(start, end, walletId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

//    public BigDecimal getBalance() {
//        List<Transaction> transactions = transactionRepository.findAll();
//        return transactions.stream()
//                .map (t -> {
//                    if (t.getCategory().getType() == CategoryType.INCOME){
//                        return t.getAmount();
//                    } else {
//                        return t.getAmount().negate();
//                    }
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }

}
