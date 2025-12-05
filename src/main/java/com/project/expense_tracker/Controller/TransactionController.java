package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import com.project.expense_tracker.Exceptions.UnauthorizedException;
import com.project.expense_tracker.Service.TransactionService;
import com.project.expense_tracker.Service.WalletBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    //get all transactions for the authenticated user (rewritten)
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllUserTransactions() {
        try {
            Long userId = getCurrentUserId();
            List<TransactionDTO> transactions = transactionService.findAllUserTransactions(userId);

            return transactions.isEmpty()
                    ? ResponseEntity.noContent().build() //returns 204 No Content
                    : ResponseEntity.ok(transactions); //returns 200 OK and list of transactions
        } catch (Exception e) {
            log.error("Error retrieving transactions for user {}: {}", getCurrentUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    //get transactions by wallet id (rewritten)
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByWalletId(@PathVariable Long walletId) {
        try {
            Long userId = getCurrentUserId();
            List<TransactionDTO> transactions = transactionService.findUserTransactionsByWalletId(walletId, userId);

            return transactions.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(transactions);
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized access to wallet {} by user {}", walletId, getCurrentUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error retrieving transactions for wallet {}: {}", walletId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //get one transaction by id (rewritten)
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            TransactionDTO transactionDTO = transactionService.findById(id, userId);
            return ResponseEntity.ok(transactionDTO);
        } catch (ResourceNotFoundException e) {
            log.warn("Transaction not fount with id {} for user {}", id, getCurrentUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized access to transaction {} by user {}", id, getCurrentUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error retrieving transaction with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //create a new transaction (rewritten)
    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        try {
            Long userId = getCurrentUserId();
            TransactionDTO createdTransaction = transactionService.create(transactionDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
        } catch (ResourceNotFoundException e) {
            log.warn("Related entity not found: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized transaction creation attempt by user {}: {}", getCurrentUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error creating transaction: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }

    //update an existing transaction (rewritten)
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        try {
            Long userId = getCurrentUserId();
            TransactionDTO updatedTransaction = transactionService.update(id, transactionDTO, userId);
            return ResponseEntity.ok(updatedTransaction);
        } catch (ResourceNotFoundException e) {
            log.warn("Transaction not found for update with id {} for user {}", id, getCurrentUserId());
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized transaction update attempt by user {}: {}", getCurrentUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error while updating transaction with id {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //delete the transaction (rewritten)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            transactionService.deleteById(id, userId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            log.warn("Transaction not found for deletion with id {} for user {}", id, getCurrentUserId());
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            log.warn("Unauthorized transaction deletion attempt by user {}: {}", getCurrentUserId(), e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error while deleting transaction with id {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }



    //helper to get current user ID from security context
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        //implement user details
        String username = authentication.getName();

        //implement method to get id by username
        return getUserIdByUsername(username);
    }

}
