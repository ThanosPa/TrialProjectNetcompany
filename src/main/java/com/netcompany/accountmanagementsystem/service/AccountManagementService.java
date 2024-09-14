package com.netcompany.accountmanagementsystem.service;

import com.netcompany.accountmanagementsystem.model.Account;
import com.netcompany.accountmanagementsystem.model.Beneficiary;
import com.netcompany.accountmanagementsystem.model.Transaction;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class AccountManagementService {

    private Map<Integer, Beneficiary> beneficiaries = new HashMap<>();
    private Map<Integer, Account> accounts = new HashMap<>();
    private List<Transaction> transactions = new ArrayList<>();

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
                Beneficiary beneficiary = new Beneficiary(Integer.parseInt(line[0]), line[1], line[2]);
                beneficiaries.put(beneficiary.getBeneficiaryId(), beneficiary);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadAccounts() {
        try (InputStream inputStream = new ClassPathResource("accounts.csv").getInputStream();
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                Account account = new Account(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
                accounts.put(account.getAccountId(), account);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private void loadTransactions() {
        try (InputStream inputStream = new ClassPathResource("transactions.csv").getInputStream();
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {

            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                Transaction transaction = new Transaction(
                        Integer.parseInt(line[0]),
                        Integer.parseInt(line[1]),
                        Double.parseDouble(line[2]),
                        line[3],
                        LocalDate.parse(line[4], DateTimeFormatter.ofPattern("MM/dd/yy"))
                );
                transactions.add(transaction);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public Beneficiary getBeneficiaryDetails(int beneficiaryId) {
        return beneficiaries.get(beneficiaryId);
    }

    public List<Account> getAccountsForBeneficiary(int beneficiaryId) {
        return accounts.values().stream()
                .filter(account -> account.getBeneficiaryId() == beneficiaryId)
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsForBeneficiary(int beneficiaryId) {
        List<Integer> beneficiaryAccountIds = getAccountsForBeneficiary(beneficiaryId)
                .stream()
                .map(Account::getAccountId)
                .collect(Collectors.toList());

        return transactions.stream()
                .filter(transaction -> beneficiaryAccountIds.contains(transaction.getAccountId()))
                .collect(Collectors.toList());
    }

    public double getAccountBalance(int accountId) {
        return transactions.stream()
                .filter(t -> t.getAccountId() == accountId)
                .mapToDouble(t -> "deposit".equals(t.getType()) ? t.getAmount() : -t.getAmount())
                .sum();
    }

    public Transaction getLargestWithdrawalLastMonth(int beneficiaryId) {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

        // Retrieve all transactions for the given beneficiary
        List<Transaction> transactions = getTransactionsForBeneficiary(beneficiaryId);

        System.out.println("Transactions for beneficiary " + beneficiaryId + ": " + transactions);

        if (transactions == null || transactions.isEmpty()) {
            System.out.println("No transactions found for beneficiary " + beneficiaryId);
            return null;  // No transactions to process
        }

        // Filter and get the largest withdrawal
        return transactions.stream()
                .filter(t -> "withdrawal".equals(t.getType()) && t.getDate().isAfter(oneMonthAgo))
                .max(Comparator.comparingDouble(Transaction::getAmount))
                .orElse(null);
    }

}

