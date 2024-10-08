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
        MockitoAnnotations.openMocks(this);  // Replace deprecated initMocks
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

        String expectedErrorMessage = "{\"error\":\"Beneficiary with ID 999 not found\"}";

        mockMvc.perform(get("/api/beneficiary/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedErrorMessage));

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

        String expectedErrorMessage = "{\"error\":\"No accounts found for Beneficiary with ID 305\"}";

        mockMvc.perform(get("/api/beneficiary/305/accounts"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedErrorMessage));

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

        String expectedErrorMessage = "{\"error\":\"No transactions found for Beneficiary with ID 305\"}";

        mockMvc.perform(get("/api/beneficiary/305/transactions"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedErrorMessage));

        verify(accountService, times(1)).getTransactionsForBeneficiary(305L);
    }

    // Test for total balance across all accounts for a beneficiary
    @Test
    void testGetTotalBalanceForBeneficiary() throws Exception {
        // Mock the accounts for the beneficiary, ensuring there's at least one account to avoid the 404
        List<Account> mockAccounts = Arrays.asList(new Account(1L, 305L), new Account(2L, 305L));
        when(accountService.getAccountsForBeneficiary(305L)).thenReturn(mockAccounts);  // Ensure non-empty list

        // Mock the total balance calculation
        when(accountService.getTotalBalanceForBeneficiary(305L)).thenReturn(300.50);

        String expectedResponse = """
            {
                "beneficiaryId": "305",
                "totalBalance": 300.50
            }""";

        mockMvc.perform(get("/api/beneficiary/305/total-balance"))
                .andExpect(status().isOk())  // Expecting 200 OK status
                .andExpect(content().json(expectedResponse));

        verify(accountService, times(1)).getTotalBalanceForBeneficiary(305L);
    }


    // Test for total balance - No accounts found for beneficiary
    @Test
    void testGetTotalBalanceForBeneficiaryNoAccounts() throws Exception {
        when(accountService.getAccountsForBeneficiary(305L)).thenReturn(Collections.emptyList());

        String expectedErrorMessage = "{\"error\":\"No accounts found for Beneficiary with ID 305\"}";

        mockMvc.perform(get("/api/beneficiary/305/total-balance"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedErrorMessage));

        verify(accountService, times(1)).getAccountsForBeneficiary(305L);
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

        String expectedErrorMessage = "{\"error\":\"No withdrawals found for Beneficiary with ID 305\"}";

        mockMvc.perform(get("/api/beneficiary/305/largest-withdrawal"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(expectedErrorMessage));

        verify(accountService, times(1)).getLargestWithdrawalLastMonth(305L);
    }
}
