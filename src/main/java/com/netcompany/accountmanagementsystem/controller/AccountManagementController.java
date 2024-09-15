package com.netcompany.accountmanagementsystem.controller;

import com.netcompany.accountmanagementsystem.model.Account;
import com.netcompany.accountmanagementsystem.model.Beneficiary;
import com.netcompany.accountmanagementsystem.model.Transaction;
import com.netcompany.accountmanagementsystem.service.AccountManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountManagementController {

    private final AccountManagementService accountService;
    private static final Logger logger = LoggerFactory.getLogger(AccountManagementController.class);

    public AccountManagementController(AccountManagementService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/beneficiary/{beneficiaryId}")
    public ResponseEntity<?> getBeneficiary(@PathVariable Long beneficiaryId) {
        Optional<Beneficiary> beneficiary = accountService.getBeneficiaryDetails(beneficiaryId);

        // If Beneficiary is found, return it
        if (beneficiary.isPresent()) {
            return ResponseEntity.ok(beneficiary.get());
        }

        // If not found, return a Map with the error message
        logger.warn("Beneficiary with ID {} not found", beneficiaryId);
        Map<String, String> errorResponse = Map.of("error", "Beneficiary with ID " + beneficiaryId + " not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }


    @GetMapping("/beneficiary/{beneficiaryId}/accounts")
    public ResponseEntity<?> getAccounts(@PathVariable Long beneficiaryId) {
        List<Account> accounts = accountService.getAccountsForBeneficiary(beneficiaryId);
        if (accounts.isEmpty()) {
            logger.warn("No accounts found for Beneficiary with ID {}", beneficiaryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "No accounts found for Beneficiary with ID " + beneficiaryId)
            );
        }
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/beneficiary/{beneficiaryId}/transactions")
    public ResponseEntity<?> getTransactions(@PathVariable Long beneficiaryId) {
        List<Transaction> transactions = accountService.getTransactionsForBeneficiary(beneficiaryId);
        if (transactions.isEmpty()) {
            logger.warn("No transactions found for Beneficiary with ID {}", beneficiaryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "No transactions found for Beneficiary with ID " + beneficiaryId)
            );
        }
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/account/{accountId}/balance")
    public ResponseEntity<?> getAccountBalance(@PathVariable Long accountId) {
        double balance = accountService.getAccountBalance(accountId);

        // Return 200 OK with balance, even if it's 0
        return ResponseEntity.ok(Map.of("accountId", String.valueOf(accountId), "balance", String.valueOf(balance)));
    }

    @GetMapping("/beneficiary/{beneficiaryId}/largest-withdrawal")
    public ResponseEntity<?> getLargestWithdrawal(@PathVariable Long beneficiaryId) {
        Optional<Transaction> largestWithdrawal = accountService.getLargestWithdrawalLastMonth(beneficiaryId);

        // If transaction is found, return the Transaction with 200 OK
        if (largestWithdrawal.isPresent()) {
            return ResponseEntity.ok(largestWithdrawal.get());
        }

        // If not found, return a Map with the error message as JSON
        logger.warn("No withdrawals found for Beneficiary with ID {}", beneficiaryId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "No withdrawals found for Beneficiary with ID " + beneficiaryId)
        );
    }
}
