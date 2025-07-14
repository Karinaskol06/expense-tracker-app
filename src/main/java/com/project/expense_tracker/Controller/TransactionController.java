package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import com.project.expense_tracker.Service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "http://localhost:8080")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    //get all transactions (main page)
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<TransactionDTO> transactions = transactionService.findAll();
        return transactions.isEmpty()
                ? ResponseEntity.noContent().build() //returns 204 No Content
                : ResponseEntity.ok(transactions); //returns 200 OK and list of transactions
    }

    //get transaction by id
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable Long id) {
        try{
            TransactionDTO transactionDTO = transactionService.findById(id);
            return ResponseEntity.ok(transactionDTO);
        } catch (ResourceNotFoundException e) {
            log.warn("Transaction not found with id {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    //create a new transaction
    @PostMapping
    public ResponseEntity<TransactionDTO> createNewTransaction (@Valid @RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionDTO createdDTO = transactionService.create(transactionDTO);
            return ResponseEntity.ok(createdDTO);
        } catch (ResourceNotFoundException e) {
            log.warn("Related entity nor found: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error while creating transaction {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }

    }

    //update an existing transaction
    @PutMapping("/{id}")
    public ResponseEntity<TransactionDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionDTO updatedDTO = transactionService.update(id, transactionDTO);
            return ResponseEntity.ok(updatedDTO);
        } catch (ResourceNotFoundException e) {
            log.warn("Transaction not found for update with id {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error while updating transaction with id {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            log.warn("Transaction not found for deletion with id {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error while deleting transaction with id {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

}
