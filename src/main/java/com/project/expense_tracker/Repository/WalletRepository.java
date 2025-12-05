package com.project.expense_tracker.Repository;

import com.project.expense_tracker.Entity.Wallet;
import com.project.expense_tracker.Exceptions.ResourceNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    //find all the wallets for the specific user
    List<Wallet> findByOwner_User_Id(Long userId);

    //find a specific wallet by id
    default Wallet findByWalletId(Long walletId){
        return findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + walletId));
    }

    //check
    boolean existsByIdAndOwnerId(Long walletId, Long userId);

    Optional<Wallet> findByIdAndOwner_User_Id(Long walletId, Long userId);

    //get count of wallets that belong to the user
    @Query("SELECT COUNT(w) FROM Wallet w WHERE w.owner.user.id = :userId")
    int countByUserId(@Param("userId") Long userId);
}
