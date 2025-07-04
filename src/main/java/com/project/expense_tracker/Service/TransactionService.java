package com.project.expense_tracker.Service;

import com.project.expense_tracker.Entity.CategoryType;
import com.project.expense_tracker.Entity.Transaction;
import com.project.expense_tracker.Repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TransactionService {

    private final TransactionRepository transactionsRepository;

    public TransactionService(TransactionRepository transactionsRepository) {
        this.transactionsRepository = transactionsRepository;
    }

    public Transaction save(Transaction transact) {
        return transactionsRepository.save(transact);
    }

    public List<Transaction> findAll() {
        return transactionsRepository.findAll();
    }

    public Transaction findById(Long id) {
        return transactionsRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("No transaction found with id " + id));
    }

    public void deleteById(Long id) {
        transactionsRepository.deleteById(id);
    }

    public BigDecimal getBalance() {
        List<Transaction> transactions = transactionsRepository.findAll();
        return transactions.stream()
                .map (t -> {
                    if (t.getCategory().getType() == CategoryType.INCOME){
                        return t.getAmount();
                    } else {
                        return t.getAmount().negate();
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
