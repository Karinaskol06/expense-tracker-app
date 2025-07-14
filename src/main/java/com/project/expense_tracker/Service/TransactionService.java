package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.TransactionDTO;
import com.project.expense_tracker.Entity.*;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import com.project.expense_tracker.Mapper.TransactionMapper;
import com.project.expense_tracker.Repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final LabelService labelService;
    private final WalletService walletService;

    public List<TransactionDTO> findAll() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO findById(Long id) {
        //confirmation that transaction exists
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));
        return transactionMapper.toDTO(transaction);
    }

    public TransactionDTO create(TransactionDTO transactionDTO) {
        Category category = categoryService.findCategoryById(transactionDTO.getCategoryId());
        Label label = labelService.findLabelById(transactionDTO.getLabelId());
        Wallet wallet = walletService.findWalletById(transactionDTO.getWalletId());

        Transaction transaction = transactionMapper.toEntity(transactionDTO, category, label, wallet);
        transactionRepository.save(transaction);
        return transactionMapper.toDTO(transaction);
    }

    public TransactionDTO update(Long id, TransactionDTO transactionDTO) {

        //confirm and find
        Transaction transactionUpd = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id " + id));

        //find related entities
        Category category = categoryService.findCategoryById(transactionDTO.getCategoryId());
        Label label = labelService.findLabelById(transactionDTO.getLabelId());
        Wallet wallet = walletService.findWalletById(transactionDTO.getWalletId());

        //set them to the transactionUpd
        transactionUpd.setTransactionDate(LocalDate.parse(transactionDTO.getTransactionDate()));
        transactionUpd.setNote(transactionDTO.getNote());
        transactionUpd.setAmount(transactionDTO.getAmount());
        transactionUpd.setCategory(category);
        transactionUpd.setLabel(label);
        transactionUpd.setWallet(wallet);

        return transactionMapper.toDTO(transactionRepository.save(transactionUpd));
    }

    public void deleteById(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with id " + id);
        }
        transactionRepository.deleteById(id);
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
