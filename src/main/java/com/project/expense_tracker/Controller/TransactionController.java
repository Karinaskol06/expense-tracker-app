package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.Entity.*;
import com.project.expense_tracker.Mapper.TransactionMapper;
import com.project.expense_tracker.Service.CategoryService;
import com.project.expense_tracker.Service.LabelService;
import com.project.expense_tracker.Service.TransactionService;
import com.project.expense_tracker.Service.WalletService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@CrossOrigin(origins = "http://localhost:8080")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final LabelService labelService;
    private final WalletService walletService;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionsService, CategoryService categoryService, LabelService labelService, WalletService walletService, TransactionMapper transactionMapper) {
        this.transactionService = transactionsService;
        this.categoryService = categoryService;
        this.labelService = labelService;
        this.walletService = walletService;
        this.transactionMapper = transactionMapper;
    }

    //method for creation and updating
    private TransactionDTO getTransactionDTO(@RequestBody TransactionDTO transactionDTO) {
        if (transactionDTO == null) return null;
        Category category = categoryService.findCategoryById(transactionDTO.getCategoryId());
        Label label = labelService.findLabelById(transactionDTO.getLabelId());
        Wallet wallet = walletService.findWalletById(transactionDTO.getWalletId());
        Transaction transaction = transactionMapper.toEntity(transactionDTO, category, label, wallet);
        Transaction savedTransaction = transactionService.save(transaction);
        return transactionMapper.toDTO(savedTransaction);
    }

    //create a new transaction
    @PostMapping("/create")
    public TransactionDTO createNewTransaction (@RequestBody(required = true) TransactionDTO transactionDTO) {
        return getTransactionDTO(transactionDTO);
    }

    @PutMapping("/update")
    public TransactionDTO updateTransaction(@RequestBody TransactionDTO transactionDTO) {
        return getTransactionDTO(transactionDTO);
    }

    //receive a list of all transactions
    @GetMapping("/all")
    public List<TransactionDTO> getAllTransactions() {
        List<Transaction> transactions = transactionService.findAll();
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    //receive transaction by id
    @GetMapping("{transactionId}")
    public TransactionDTO getTransactionById(@PathVariable Long transactionId) {
        return transactionMapper.toDTO(transactionService.findById(transactionId));
    }

    @DeleteMapping("/delete/{transactionId}")
    public void deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteById(transactionId);
    }

}
