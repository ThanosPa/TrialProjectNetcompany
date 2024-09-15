package com.netcompany.accountmanagementsystem.controller;

import com.netcompany.accountmanagementsystem.model.Account;
import com.netcompany.accountmanagementsystem.model.Beneficiary;
import com.netcompany.accountmanagementsystem.model.Transaction;
import com.netcompany.accountmanagementsystem.service.AccountManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AccountManagementControllerTest {

    @Mock
    private AccountManagementService accountService;

    @InjectMocks
    private AccountManagementController accountController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    // Test for getBeneficiary
    @Test
    void testGetBeneficiary() throws Exception {
        Beneficiary beneficiary = new Beneficiary(305L, "John", "Doe");
        when(accountService.getBeneficiaryDetails(305L)).thenReturn(Optional.of(beneficiary));

        mockMvc.perform(get("/api/beneficiary/305"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"beneficiaryId\":305,\"firstName\":\"John\",\"lastName\":\"Doe\"}"));

        verify(accountService, times(1)).getBeneficiaryDetails(305L);
    }

    // Test for getBeneficiary - Not Found
    @Test
    void testGetBeneficiaryNotFound() throws Exception {
        when(accountService.getBeneficiaryDetails(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/beneficiary/999"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getBeneficiaryDetails(999L);
    }

    // Test for getAccounts
    @Test
    void testGetAccounts() throws Exception {
        List<Account> accounts = Arrays.asList(
                new Account(1L, 305L),
                new Account(2L, 305L)
        );
        when(accountService.getAccountsForBeneficiary(305L)).thenReturn(accounts);

        mockMvc.perform(get("/api/beneficiary/305/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"accountId\":1,\"beneficiaryId\":305},{\"accountId\":2,\"beneficiaryId\":305}]"));

        verify(accountService, times(1)).getAccountsForBeneficiary(305L);
    }

    // Test for getAccounts - No Accounts Found
    @Test
    void testGetAccountsNotFound() throws Exception {
        when(accountService.getAccountsForBeneficiary(305L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/beneficiary/305/accounts"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getAccountsForBeneficiary(305L);
    }

    // Test for getTransactions
    @Test
    void testGetTransactions() throws Exception {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1L, 1L, 100.0, "deposit", LocalDate.of(2023, 9, 1)),
                new Transaction(2L, 2L, 50.0, "withdrawal", LocalDate.of(2023, 9, 2))
        );
        when(accountService.getTransactionsForBeneficiary(305L)).thenReturn(transactions);

        mockMvc.perform(get("/api/beneficiary/305/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"transactionId\":1,\"accountId\":1,\"amount\":100.0,\"type\":\"deposit\",\"date\":\"2023-09-01\"}," +
                        "{\"transactionId\":2,\"accountId\":2,\"amount\":50.0,\"type\":\"withdrawal\",\"date\":\"2023-09-02\"}]"));

        verify(accountService, times(1)).getTransactionsForBeneficiary(305L);
    }



    // Test for getTransactions - No Transactions Found
    @Test
    void testGetTransactionsNotFound() throws Exception {
        when(accountService.getTransactionsForBeneficiary(305L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/beneficiary/305/transactions"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getTransactionsForBeneficiary(305L);
    }

    // Test for getAccountBalance
    @Test
    void testGetAccountBalance() throws Exception {
        when(accountService.getAccountBalance(1L)).thenReturn(150.75);

        mockMvc.perform(get("/api/account/1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("150.75"));

        verify(accountService, times(1)).getAccountBalance(1L);
    }

    // Test for getLargestWithdrawal
    @Test
    void testGetLargestWithdrawal() throws Exception {
        Transaction largestWithdrawal = new Transaction(2L, 2L, 200.0, "withdrawal", LocalDate.of(2023, 9, 10));
        when(accountService.getLargestWithdrawalLastMonth(305L)).thenReturn(Optional.of(largestWithdrawal));

        mockMvc.perform(get("/api/beneficiary/305/largest-withdrawal"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionId\":2,\"accountId\":2,\"amount\":200.0,\"type\":\"withdrawal\",\"date\":\"2023-09-10\"}"));

        verify(accountService, times(1)).getLargestWithdrawalLastMonth(305L);
    }

    // Test for getLargestWithdrawal - Not Found
    @Test
    void testGetLargestWithdrawalNotFound() throws Exception {
        when(accountService.getLargestWithdrawalLastMonth(305L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/beneficiary/305/largest-withdrawal"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getLargestWithdrawalLastMonth(305L);
    }
}
