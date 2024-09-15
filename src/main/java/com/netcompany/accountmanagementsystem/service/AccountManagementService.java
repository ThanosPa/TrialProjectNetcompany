package com.netcompany.accountmanagementsystem.service;

import com.netcompany.accountmanagementsystem.model.Account;
import com.netcompany.accountmanagementsystem.model.Beneficiary;
import com.netcompany.accountmanagementsystem.model.Transaction;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.math.BigDecimal;

@Service
public class AccountManagementService {

    private static final Logger logger = LoggerFactory.getLogger(AccountManagementService.class);

    private final Map<Long, Beneficiary> beneficiaries = new HashMap<>();
    private final Map<Long, Account> accounts = new HashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public AccountManagementService() {
        loadBeneficiaries();
        loadAccounts();
        loadTransactions();
    }

    private void loadBeneficiaries() {
        try (InputStream inputStream = new ClassPathResource("beneficiaries.csv").getInputStream();
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                Beneficiary beneficiary = new Beneficiary(Long.parseLong(line[0]), line[1], line[2]);
                beneficiaries.put(beneficiary.getBeneficiaryId(), beneficiary);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error loading beneficiaries from CSV", e);
        }
    }

    private void loadAccounts() {
        try (InputStream inputStream = new ClassPathResource("accounts.csv").getInputStream();
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                Account account = new Account(Long.parseLong(line[0]), Long.parseLong(line[1]));
                accounts.put(account.getAccountId(), account);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error loading accounts from CSV", e);
        }
    }

    private void loadTransactions() {
        try (InputStream inputStream = new ClassPathResource("transactions.csv").getInputStream();
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                Transaction transaction = new Transaction(
                        Long.parseLong(line[0]),
                        Long.parseLong(line[1]),
                        Double.parseDouble(line[2]),
                        line[3],
                        LocalDate.parse(line[4], DateTimeFormatter.ofPattern("MM/dd/yy"))
                );
                transactions.add(transaction);
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error loading transactions from CSV", e);
        }
    }

    public Optional<Beneficiary> getBeneficiaryDetails(Long beneficiaryId) {
        return Optional.ofNullable(beneficiaries.get(beneficiaryId));
    }

    public List<Account> getAccountsForBeneficiary(Long beneficiaryId) {
        List<Account> result = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getBeneficiaryId().equals(beneficiaryId)) {
                result.add(account);
            }
        }
        return result;
    }

    public List<Transaction> getTransactionsForBeneficiary(Long beneficiaryId) {
        List<Long> beneficiaryAccountIds = new ArrayList<>();
        for (Account account : getAccountsForBeneficiary(beneficiaryId)) {
            beneficiaryAccountIds.add(account.getAccountId());
        }

        List<Transaction> beneficiaryTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (beneficiaryAccountIds.contains(transaction.getAccountId())) {
                beneficiaryTransactions.add(transaction);
            }
        }
        return beneficiaryTransactions;
    }

    public double getAccountBalance(Long accountId) {
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getAccountId().equals(accountId)) {
                BigDecimal amount = BigDecimal.valueOf(transaction.getAmount());
                if ("deposit".equals(transaction.getType())) {
                    balance = balance.add(amount);
                } else if ("withdrawal".equals(transaction.getType())) {
                    balance = balance.subtract(amount);
                }
            }
        }

        return balance.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public Optional<Transaction> getLargestWithdrawalLastMonth(Long beneficiaryId) {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        List<Transaction> beneficiaryTransactions = getTransactionsForBeneficiary(beneficiaryId);

        Transaction largestWithdrawal = null;
        for (Transaction transaction : beneficiaryTransactions) {
            if ("withdrawal".equals(transaction.getType()) && transaction.getDate().isAfter(oneMonthAgo)) {
                if (largestWithdrawal == null || transaction.getAmount() > largestWithdrawal.getAmount()) {
                    largestWithdrawal = transaction;
                }
            }
        }

        if (largestWithdrawal == null) {
            logger.warn("No withdrawals found for Beneficiary ID {} in the last month", beneficiaryId);
        }

        return Optional.ofNullable(largestWithdrawal);
    }
}
