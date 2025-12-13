package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.DTO.WalletDTO.WalletStatisticsDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;
    private final WalletService walletService;
    private final WalletBalanceService walletBalanceService;
    private final WalletRepository walletRepository;
    private final UserService userService;

    //get all the user's transactions
    public List<TransactionDTO> findAllUserTransactions(Long userId) {
        return transactionRepository.findTransactionsByUserId(userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //find user transactions by wallet id
    public List<TransactionDTO> findUserTransactionsByWalletId(Long walletId, Long userId) {
        //verify user owns the wallet
        if (!walletRepository.existsByIdAndOwnerId(walletId, userId)) {
            throw new UnauthorizedException("Wallet not found or access denied");
        }
        List<Transaction> transactions = transactionRepository.findTransactionsByWalletId(walletId);
        List<TransactionDTO> transactionDTOs = new ArrayList<>(transactions.size());

        for (Transaction transaction : transactions) {
            transactionDTOs.add(transactionMapper.toDTO(transaction));
        }
        return transactionDTOs;
    }

    //find a single transaction
    public TransactionDTO findById(Long transactionId, Long userId ) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));

        if (!transaction.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("User not found or access denied");
        }
        return transactionMapper.toDTO(transaction);
    }

    @Transactional
    public TransactionDTO create(TransactionDTO transactionDTO, Long userId) {
        User user = userService.getUserEntityById(userId);
        Wallet wallet = walletService.findWalletById(transactionDTO.getWalletId());
        validateWalletOwnership(wallet, userId);

        Category category = categoryService.findCategoryById(transactionDTO.getCategoryId());

        Transaction transaction = Transaction.builder()
                .transactionDate(transactionDTO.getTransactionDate())
                .note(transactionDTO.getNote())
                .amount(transactionDTO.getAmount())
                .category(category)
                .wallet(wallet)
                .user(user)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        walletBalanceService.handleTransactionCreated(saved);
        return transactionMapper.toDTO(saved);
    }

    //update existing transaction
    @Transactional
    public TransactionDTO update(Long transactionId, TransactionDTO transactionDTO, Long userId) {
        Transaction existingTransaction = getTransactionById(transactionId);
        validateTransactionOwnership(existingTransaction, userId);

        Wallet oldWallet = existingTransaction.getWallet();
        BigDecimal oldAmount = existingTransaction.getAmount();
        CategoryType oldType = existingTransaction.getCategory().getType() != null ?
                existingTransaction.getCategory().getType() : null;

        Transaction oldTransaction = copyTransaction(existingTransaction);
        updateTransactionDetails(existingTransaction, transactionDTO, userId);
        Transaction updatedTrans = transactionRepository.save(existingTransaction);

        walletBalanceService.handleTransactionUpdated(oldTransaction, updatedTrans);

        return transactionMapper.toDTO(updatedTrans);
    }

    //delete transaction
    @Transactional
    public void deleteById(Long id, Long userId) {
        Transaction transaction = getTransactionById(id);
        validateTransactionOwnership(transaction, userId);

        walletBalanceService.handleTransactionDeleted(transaction);
        transactionRepository.delete(transaction);
    }

    //get transactions by category
    public List<TransactionDTO> findTransactionsByCategory(Long categoryId, Long userId) {
        Category category = categoryService.findCategoryById(categoryId);
        return transactionRepository
                .findByCategoryAndUserId(category, userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //get transactions by date range for user
    public List<TransactionDTO> findTransactionsByDateRange(LocalDate start, LocalDate end, Long userId) {
        validateDateRange(start, end);

        return transactionRepository
                .findByTransactionDateBetweenAndUserId(start, end, userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //get transactions by date range for wallet
    public List<TransactionDTO> findByDateRangeAndWalletId(LocalDate start, LocalDate end, Long walletId, Long userId) {
        Wallet wallet = walletService.findWalletById(walletId);
        validateWalletOwnership(wallet, wallet.getOwner().getId());
        validateDateRange(start, end);

        return transactionRepository
                .findByTransactionDateBetweenAndWalletId(start, end, walletId, userId).stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    //get wallet statistics
    //last written
    public WalletStatisticsDTO getWalletStatistics(Long walletId, Long userId) {
        validateWalletOwnership(walletService.findWalletById(walletId), userId);

        return WalletStatisticsDTO.builder()
                .totalTransactions(transactionRepository.countByWalletId(walletId))
                .uniqueCategories(transactionRepository.countCategoriesByWalletId(walletId))
                .totalIncome(transactionRepository.sumIncomeByWalletId(walletId))
                .totalExpense(transactionRepository.sumExpenseByWalletId(walletId))
                .build();
    }


    /* helper methods */
    public void validateTransactionOwnership(Transaction transaction, Long userId) {
        if (!transaction.getWallet().getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this wallet.");
        }
    }

    public void validateWalletOwnership(Wallet wallet, Long userId) {
        if (!wallet.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have access to this wallet.");
        }
    }

    public void validateDateRange(LocalDate start, LocalDate end) {
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Enter correct dates");
        }
    }

    public void updateTransactionDetails(Transaction transaction, TransactionDTO transactionDTO, Long userId) {
        if (!transaction.getWallet().getId().equals(transactionDTO.getWalletId())) {
            Wallet newWallet = walletService.findWalletById(transactionDTO.getWalletId());
            validateWalletOwnership(newWallet, userId);
            transaction.setWallet(newWallet);
        }
        if (!transaction.getCategory().getId().equals(transactionDTO.getCategoryId())) {
            Category newCategory = categoryService.findCategoryById(transactionDTO.getCategoryId());
            transaction.setCategory(newCategory);
        }
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setNote(transactionDTO.getNote());
        transaction.setTransactionDate(transactionDTO.getTransactionDate());
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    private Transaction copyTransaction(Transaction original) {
        return Transaction.builder()
                .id(original.getId())
                .amount(original.getAmount())
                .category(original.getCategory())
                .wallet(original.getWallet())
                .build();
    }

}
