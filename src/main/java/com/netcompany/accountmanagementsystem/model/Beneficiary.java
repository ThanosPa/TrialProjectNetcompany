package com.netcompany.accountmanagementsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiary {
    private int beneficiaryId;
    private String firstName;
    private String lastName;
}