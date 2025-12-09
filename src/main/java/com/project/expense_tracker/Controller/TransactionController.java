package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.DTO.WalletDTO.WalletStatisticsDTO;
import com.project.expense_tracker.Security.SecurityUtils;
import com.project.expense_tracker.Service.TransactionService;
import com.project.expense_tracker.Service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;
    private final WalletService walletService;

    //get all transactions for authenticated user
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllUserTransactions() {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionDTO> transactions = transactionService.findAllUserTransactions(userId);
        return buildResponse(transactions);
    }

    //get transactions by wallet id
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByWalletId(
            @PathVariable @NotNull Long walletId) {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionDTO> transactions = transactionService.findUserTransactionsByWalletId(walletId, userId);
        return buildResponse(transactions);
    }

    //get one transaction by id
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> getTransactionById(
            @PathVariable @NotNull Long transactionId) {
        Long userId = securityUtils.getCurrentUserId();
        TransactionDTO transaction = transactionService.findById(transactionId, userId);
        return ResponseEntity.ok(transaction);
    }

    //create a new transaction
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        Long userId = securityUtils.getCurrentUserId();
        TransactionDTO transaction = transactionService.create(transactionDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
    }

    //update an existing transaction
    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        Long userId = securityUtils.getCurrentUserId();
        TransactionDTO updTransaction = transactionService.update(transactionId, transactionDTO, userId);
        return ResponseEntity.ok(updTransaction);
    }

    //delete the transaction
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        Long userId = securityUtils.getCurrentUserId();
        transactionService.deleteById(transactionId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCategory(
            @PathVariable @NotNull Long categoryId) {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionDTO> transactions = transactionService.findTransactionsByCategory(categoryId, userId);
        return buildResponse(transactions);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate end) {
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionDTO> transactions = transactionService.findTransactionsByDateRange(start, end, userId);
        return buildResponse(transactions);
    }

    @GetMapping("/wallet/{walletId}/date-range")
    public ResponseEntity<List<TransactionDTO>> getWalletTransactionsByDateRange(
            @PathVariable Long walletId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end){
        Long userId = securityUtils.getCurrentUserId();
        List<TransactionDTO> transactions = transactionService.findByDateRangeAndWalletId(start, end, walletId, userId);
        return buildResponse(transactions);
    }

    @GetMapping("/wallet/{walletId}/statistics")
    public ResponseEntity<WalletStatisticsDTO> getWalletStats(
            @PathVariable @NotNull Long walletId) {
        Long userId = securityUtils.getCurrentUserId();
        WalletStatisticsDTO stats = walletService.getWalletStats(walletId, userId);
        return ResponseEntity.ok(stats);
    }

    /* helper methods */
    private <T> ResponseEntity<List<T>> buildResponse(List<T> data) {
        if (data.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(data);
    }



}
