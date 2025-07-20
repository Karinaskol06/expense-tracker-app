package com.project.expense_tracker.Mapper;

import com.project.expense_tracker.DTO.WalletDTO.WalletDTO;
import com.project.expense_tracker.DTO.WalletDTO.WalletDetailsDTO;
import com.project.expense_tracker.DTO.WalletStats;
import com.project.expense_tracker.Entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    //wallet entity to walletDTO
    @Mapping(source = "owner.id", target = "ownerId") //used for fields that are not the same
    @Mapping(source = "owner.name", target = "ownerName")
    WalletDTO toWalletDTO(Wallet wallet);

    @Mapping(source = "ownerId", target = "id")
    Wallet toEntity(WalletDTO walletDTO);

    //wallet entity to walletDetailsDTO (using helper stats class)
    @Mapping(source = "wallet.id", target = "id")
    @Mapping(source = "wallet.name", target = "name")
    @Mapping(source = "wallet.balance", target = "balance")
    @Mapping(source = "wallet.currency", target = "currency")
    @Mapping(source = "wallet.owner.id", target = "ownerId")
    @Mapping(source = "wallet.owner.name", target = "ownerName")
    @Mapping(source = "stats.categoryCount", target = "categoryCount")
    @Mapping(source = "stats.transactionCount", target = "transactionCount")
    @Mapping(source = "stats.totalIncome", target = "totalIncome")
    @Mapping(source = "stats.totalExpense", target = "totalExpense")
    WalletDetailsDTO toWalletDetailsDTO(Wallet wallet, WalletStats stats);


//    //walletDTO to walletDetailsDTO
//    @Mapping(source = "stats.categoryCount", target = "categoryCount")
//    @Mapping(source = "stats.transactionCount", target = "transactionCount")
//    @Mapping(source = "stats.totalIncome", target = "totalIncome")
//    @Mapping(source = "stats.totalExpense", target = "totalExpense")
//    WalletDetailsDTO toWalletDetailsDTO(WalletDTO walletDTO, WalletStats walletStats);


//    //walletDetailsDTO to walletDTO
//    @Mapping(target = "categoryCount", ignore = true) //detailed fields are not needed
//    @Mapping(target = "transactionCount", ignore = true)
//    @Mapping(target = "totalIncome", ignore = true)
//    @Mapping(target = "totalExpense", ignore = true)
//    WalletDTO toWalletDTOFromDetails(WalletDetailsDTO walletDetailsDTO);

    //default
    default WalletDetailsDTO toWalletDetailsDTODefaults(Wallet wallet) {
        if (wallet == null) return null;

        //if fields are empty, then stats are zero
        return toWalletDetailsDTO(wallet, WalletStats.empty());
    }

    //list of wallet entities to DTOs
    List<WalletDTO> toWalletDTOList(List<Wallet> wallets);

    //update DTO
    void updateWalletDTO (@MappingTarget WalletDTO walletDTO, Wallet wallet);

}
