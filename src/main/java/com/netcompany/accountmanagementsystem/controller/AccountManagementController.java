package com.netcompany.accountmanagementsystem.controller;

import com.netcompany.accountmanagementsystem.model.Account;
import com.netcompany.accountmanagementsystem.model.Beneficiary;
import com.netcompany.accountmanagementsystem.model.Transaction;
import com.netcompany.accountmanagementsystem.service.AccountManagementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AccountManagementController {

    private final AccountManagementService accountService;

    public AccountManagementController(AccountManagementService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/beneficiary/{beneficiaryId}")
    public Beneficiary getBeneficiary(@PathVariable int beneficiaryId) {
        return accountService.getBeneficiaryDetails(beneficiaryId);
    }


    @GetMapping("/beneficiary/{beneficiaryId}/accounts")
    public List<Account> getAccounts(@PathVariable int beneficiaryId) {
        return accountService.getAccountsForBeneficiary(beneficiaryId);
    }

    @GetMapping("/beneficiary/{beneficiaryId}/transactions")
    public List<Transaction> getTransactions(@PathVariable int beneficiaryId) {
        return accountService.getTransactionsForBeneficiary(beneficiaryId);
    }

    @GetMapping("/account/{accountId}/balance")
    public double getAccountBalance(@PathVariable int accountId) {
        return accountService.getAccountBalance(accountId);
    }

    @GetMapping("/beneficiary/{beneficiaryId}/largest-withdrawal")
    public Transaction getLargestWithdrawal(@PathVariable int beneficiaryId) {
        System.out.println(beneficiaryId);
        System.out.println(accountService.getLargestWithdrawalLastMonth(beneficiaryId));
        return accountService.getLargestWithdrawalLastMonth(beneficiaryId);
    }
}
