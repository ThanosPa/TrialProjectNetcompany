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
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AccountManagementController {

    private final AccountManagementService accountService;
    private static final Logger logger = LoggerFactory.getLogger(AccountManagementController.class);

    public AccountManagementController(AccountManagementService accountService) {
        this.accountService = accountService;
    }

    // Improved Error Handling: Use Optional and return 404 if not found
    @GetMapping("/beneficiary/{beneficiaryId}")
    public ResponseEntity<Beneficiary> getBeneficiary(@PathVariable Long beneficiaryId) {
        Optional<Beneficiary> beneficiary = accountService.getBeneficiaryDetails(beneficiaryId);
        return beneficiary.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("Beneficiary with ID {} not found", beneficiaryId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    // Improved Error Handling: Return 404 if no accounts found for the beneficiary
    @GetMapping("/beneficiary/{beneficiaryId}/accounts")
    public ResponseEntity<List<Account>> getAccounts(@PathVariable Long beneficiaryId) {
        List<Account> accounts = accountService.getAccountsForBeneficiary(beneficiaryId);
        if (accounts.isEmpty()) {
            logger.warn("No accounts found for Beneficiary with ID {}", beneficiaryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(accounts);
    }

    // Improved Error Handling: Return 404 if no transactions found for the beneficiary
    @GetMapping("/beneficiary/{beneficiaryId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long beneficiaryId) {
        List<Transaction> transactions = accountService.getTransactionsForBeneficiary(beneficiaryId);
        if (transactions.isEmpty()) {
            logger.warn("No transactions found for Beneficiary with ID {}", beneficiaryId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(transactions);
    }

    // Return account balance, if balance is 0, respond with 404 if necessary
    @GetMapping("/account/{accountId}/balance")
    public ResponseEntity<Double> getAccountBalance(@PathVariable Long accountId) {
        double balance = accountService.getAccountBalance(accountId);
        if (balance == 0) {
            logger.warn("No balance found for Account with ID {}", accountId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(balance);
        }
        return ResponseEntity.ok(balance);
    }

    // Improved error handling for largest withdrawal, return 404 if no withdrawal found
    @GetMapping("/beneficiary/{beneficiaryId}/largest-withdrawal")
    public ResponseEntity<Transaction> getLargestWithdrawal(@PathVariable Long beneficiaryId) {
        Optional<Transaction> largestWithdrawal = accountService.getLargestWithdrawalLastMonth(beneficiaryId);
        return largestWithdrawal.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("No withdrawals found for Beneficiary with ID {}", beneficiaryId);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }
}
