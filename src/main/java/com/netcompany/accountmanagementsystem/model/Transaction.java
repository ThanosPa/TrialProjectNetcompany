package com.netcompany.accountmanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private int transactionId;
    private int accountId;
    private double amount;
    private String type; // "deposit" or "withdrawal"
    private LocalDate date;
}