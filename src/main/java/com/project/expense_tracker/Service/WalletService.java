package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.WalletDTO.WalletDTO;
import com.project.expense_tracker.DTO.WalletDTO.WalletStatisticsDTO;
import com.project.expense_tracker.Entity.User;
import com.project.expense_tracker.Entity.Wallet;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import com.project.expense_tracker.Exceptions.UnauthorizedException;
import com.project.expense_tracker.Mapper.WalletMapper;
import com.project.expense_tracker.Repository.TransactionRepository;
import com.project.expense_tracker.Repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletMapper walletMapper;
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    //get all wallets that user possesses
    public List<WalletDTO> getWalletsByUserId(Long userId) {
        return walletRepository.findByOwnerId(userId).stream()
                .map(walletMapper::toDTO)
                .collect(Collectors.toList());
    }

    //find wallet by ID (user)
    public WalletDTO findWalletById(Long walletId, Long userId) {
        Wallet wallet = validateWalletOwnership(walletId, userId);
        return walletMapper.toDTO(wallet);
    }

    //find wallet entity by id (admin or internal)
    public Wallet findWalletById(Long walletId){
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }

    //helper method (+)
    public WalletStatisticsDTO getWalletStats(Long walletId, Long userId) {

        validateWalletOwnership(walletId, userId);
        int categoryCount = transactionRepository.countCategoriesByWalletId(walletId);
        int transactionCount = transactionRepository.countByWalletId(walletId);
        BigDecimal totalIncome = transactionRepository.sumIncomeByWalletId(walletId);
        BigDecimal totalExpense = transactionRepository.sumExpenseByWalletId(walletId);

        return WalletStatisticsDTO.builder()
                .totalTransactions(transactionCount)
                .totalExpense(totalExpense)
                .totalIncome(totalIncome)
                .uniqueCategories(categoryCount)
                .build();
    }

    //create a new wallet
    @Transactional
    public WalletDTO createWallet(WalletDTO walletDTO, Long userId) {
        User user = userService.getUserEntityById(userId);
        Wallet wallet = Wallet.builder()
                .owner(user)
                .id(walletDTO.getId())
                .name(walletDTO.getName())
                .balance(walletDTO.getBalance())
                .currency(walletDTO.getCurrency())
                .build();
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toDTO(savedWallet);
    }

    //update an existing wallet
    @Transactional
    public WalletDTO updateWallet(Long walletId, WalletDTO walletDTO, Long userId) {
        Wallet wallet = validateWalletOwnership(walletId, userId);
        wallet.setName(walletDTO.getName());
        wallet.setCurrency(walletDTO.getCurrency());

        Wallet updatedWallet = walletRepository.save(wallet);
        return walletMapper.toDTO(updatedWallet);
    }

    //delete a wallet
    @Transactional
    public void deleteWallet(Long walletId, Long userId) {
        Wallet wallet = validateWalletOwnership(walletId, userId);

        transactionRepository.deleteByWalletId(walletId, userId);
        walletRepository.deleteById(walletId);
    }

    //get count of wallets for a user
    public int getWalletsCountByUserId(Long userId) {
        return walletRepository.countByUserId(userId);
    }

    //get total income for a wallet
    public BigDecimal getWalletTotalIncome(Long walletId, Long userId) {
        return walletRepository.sumIncomeByWalletId(walletId);
    }

    // Get total expenses for a wallet
    public BigDecimal getWalletTotalExpenses(Long walletId, Long userId) {
        return walletRepository.sumExpenseByWalletId(walletId);
    }

    public BigDecimal calcNetBalanceFromTransactions(Long walletId, Long userId) {
        validateWalletOwnership(walletId, userId);
        BigDecimal income = walletRepository.sumIncomeByWalletId(walletId);
        BigDecimal expenses = walletRepository.sumExpenseByWalletId(walletId);
        return income.subtract(expenses);
    }

    public BigDecimal calculateTotalWalletsBalance(List<WalletDTO> wallets) {
        BigDecimal balance = BigDecimal.ZERO;
        for (WalletDTO walletDTO : wallets) {
            balance = balance.add(walletDTO.getBalance());
        }
        return balance;
    }

    /* helper methods */
    public Wallet validateWalletOwnership(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findByIdAndOwnerId(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found or access denied"));
        if (!wallet.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("User not found or access denied");
        }
        return wallet;
    }
}
