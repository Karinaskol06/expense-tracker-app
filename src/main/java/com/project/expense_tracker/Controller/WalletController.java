package com.project.expense_tracker.Controller;

import com.project.expense_tracker.DTO.WalletDTO.WalletDTO;
import com.project.expense_tracker.DTO.WalletDTO.WalletStatisticsDTO;
import com.project.expense_tracker.Security.SecurityUtils;
import com.project.expense_tracker.Service.WalletBalanceService;
import com.project.expense_tracker.Service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final WalletBalanceService walletBalanceService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody WalletDTO walletDTO) {
        Long userId = getCurrentUserId();
        WalletDTO wallet = walletService.createWallet(walletDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
    }

    @PutMapping("/{walletId}")
    public ResponseEntity<WalletDTO> updateWallet(
            @PathVariable Long walletId,
            @Valid @RequestBody WalletDTO walletDTO) {
        Long id = getCurrentUserId();
        WalletDTO wallet = walletService.updateWallet(walletId, walletDTO, id);
        return ResponseEntity.status(HttpStatus.OK).body(wallet);
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Void> deleteWallet(
            @PathVariable Long walletId) {
        Long id = getCurrentUserId();
        walletService.deleteWallet(walletId, id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<List<WalletDTO>> getAllWallets() {
        Long userId = getCurrentUserId();
        List<WalletDTO> wallets = walletService.getWalletsByUserId(userId);
        return buildResponse(wallets);
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletDTO> getWalletById(
            @PathVariable Long walletId) {
        Long userID = getCurrentUserId();
        WalletDTO wallet = walletService.findWalletById(userID, walletId);
        return ResponseEntity.status(HttpStatus.OK).body(wallet);
    }

    @GetMapping("/{walletId}/stats")
    public ResponseEntity<Map<String, Object>> getFullWalletStats(
            @PathVariable @NotNull Long walletId ) {
        Long userId = getCurrentUserId();
        WalletDTO walletDto = walletService.findWalletById(walletId, userId);
        WalletStatisticsDTO walletStatsDTO = walletService.getWalletStats(walletId, userId);

        Map<String, Object> walletDetails = new HashMap<>();
        walletDetails.put("wallet", walletDto);
        walletDetails.put("wallet_stats", walletStatsDTO);

        return ResponseEntity.ok(walletDetails);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getWalletsSummary() {
        Long userId = getCurrentUserId();
        List<WalletDTO> wallets = walletService.getWalletsByUserId(userId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalWallets", wallets.size());
        summary.put("walletsBalance", walletService.calculateTotalWalletsBalance(wallets));
        summary.put("wallets", wallets);

        return ResponseEntity.status(HttpStatus.OK).body(summary);
    }

    /* helper methods */
    public Long getCurrentUserId() {
        return securityUtils.getCurrentUserId();
    }

    public <T> ResponseEntity<List<T>> buildResponse(List<T> data) {
        if (data.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
