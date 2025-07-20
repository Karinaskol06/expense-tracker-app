package com.project.expense_tracker.Service;

import com.project.expense_tracker.DTO.WalletDTO.WalletDTO;
import com.project.expense_tracker.DTO.WalletDTO.WalletDetailsDTO;
import com.project.expense_tracker.DTO.WalletStats;
import com.project.expense_tracker.Entity.Wallet;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
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
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;

    //get all wallets that user possesses
    public List<WalletDTO> getWalletsByUserId(Long userId) {
        return walletRepository.findByOwner_User_Id(userId).stream()
                .map(walletMapper::toWalletDTO)
                .collect(Collectors.toList());
    }

    //find wallet by ID with user auth check
    public WalletDTO findWalletByIdAndUserId(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findByIdAndOwner_User_Id(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found or you don't have access to it"));
        return walletMapper.toWalletDTO(wallet);
    }

    //find wallet entity by id (for internal use)
    public Wallet findWalletById(Long walletId){
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }


    //get wallet with detailed information including statistics
    public WalletDetailsDTO getWalletDetails(Long walletId, Long userId) {
        //user has access to the wallet
        Wallet wallet = walletRepository.findByIdAndOwner_User_Id(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found or you don't have access to it"));

        //get wallet statistics with helper method
        WalletStats stats = getWalletStats(walletId);

        return walletMapper.toWalletDetailsDTO(wallet, stats);
    }

    //helper method
    public WalletStats getWalletStats(Long walletId) {
        int categoryCount = transactionRepository.countCategoriesByWalletId(walletId);
        int transactionCount = transactionRepository.countByWalletId(walletId);
        BigDecimal totalIncome = transactionRepository.sumIncomeByWalletId(walletId);
        BigDecimal totalExpense = transactionRepository.sumExpenseByWalletId(walletId);

        return WalletStats.create(categoryCount, transactionCount, totalIncome, totalExpense);
    }

    //create a new wallet
    public WalletDTO createWallet(WalletDTO walletDTO) {
        Wallet wallet = walletMapper.toEntity(walletDTO);
        Wallet savedWallet = walletRepository.save(wallet);
        return walletMapper.toWalletDTO(savedWallet);
    }

    //update an existing wallet
    public WalletDTO updateWallet(Long walletId, WalletDTO walletDTO, Long userId) {
        Wallet wallet = walletRepository.findByIdAndOwner_User_Id(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found or you don't have access to it"));

        wallet.setName(walletDTO.getName());
        wallet.setCurrency(walletDTO.getCurrency());

        Wallet updatedWallet = walletRepository.save(wallet);
        return walletMapper.toWalletDTO(updatedWallet);
    }

    //delete a wallet
    public void deleteWallet(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findByIdAndOwner_User_Id(walletId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found or you don't have access to it"));

        walletRepository.deleteById(walletId);
    }

    //get count of wallets for a user
    public int getWalletCountByUserId(Long userId) {
        return walletRepository.countByUserId(userId);
    }

    //check if wallet exists and belongs to user
    public boolean existsByIdAndUserId(Long walletId, Long userId) {
        return walletRepository.existsByIdAndOwner_User_Id(walletId, userId);
    }

    //get wallet balance
    public BigDecimal getWalletBalance(Long walletId, Long userId) {
        BigDecimal totalIncome = transactionRepository.sumIncomeByWalletId(walletId);
        BigDecimal totalExpenses = transactionRepository.sumExpenseByWalletId(walletId);
        return totalIncome.add(totalExpenses);
    }

    //get total income for a wallet
    public BigDecimal getWalletTotalIncome(Long walletId, Long userId) {
        return transactionRepository.sumIncomeByWalletId(walletId);
    }

    // Get total expenses for a wallet
    public BigDecimal getWalletTotalExpenses(Long walletId, Long userId) {
        return transactionRepository.sumExpenseByWalletId(walletId);
    }

    //get all wallets with their details for a user
    public List<WalletDetailsDTO> getAllWalletDetails(Long userId) {
        List<Wallet> wallets = walletRepository.findByOwner_User_Id(userId);

        return wallets.stream()
                .map(wallet -> {
                    WalletStats stats = getWalletStats(wallet.getId());
                    return walletMapper.toWalletDetailsDTO(wallet, stats);
                })
                .collect(Collectors.toList());
    }


}
