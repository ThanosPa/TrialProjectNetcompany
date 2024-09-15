# Account Management System

## Overview
The **Account Management System** is a Spring Boot application that provides REST APIs for managing accounts, beneficiaries, and transactions. This project reads data from CSV files and exposes endpoints for retrieving beneficiary information, their accounts, transactions, and account balances.

## Features
- Retrieve details of a beneficiary.
- Fetch all accounts for a beneficiary.
- Get transactions related to a beneficiary.
- Retrieve account balance.
- Fetch the largest withdrawal for a beneficiary in the last month.



 **Access the application:**
    - Swagger UI (API documentation): `http://localhost:8080/swagger-ui/index.html`
    - Base API URL: `http://localhost:8080/api`

## API Endpoints
### 1. Get Beneficiary Details
- **Endpoint**: `/api/beneficiary/{beneficiaryId}`
- **Method**: `GET`


### 2. Get Beneficiary Accounts
- **Endpoint**: `/api/beneficiary/{beneficiaryId}/accounts`
- **Method**: `GET`


### 3. Get Beneficiary Transactions
- **Endpoint**: `/api/beneficiary/{beneficiaryId}/transactions`
- **Method**: `GET`



### 4. Get Total Balance of Beneficiary's accounts 
- **Endpoint**: `/api/beneficiary/305/total-balance`
- **Method**: `GET`


### 5. Get Largest Withdrawal
- **Endpoint**: `/api/beneficiary/{beneficiaryId}/largest-withdrawal`
- **Method**: `GET`


## Testing
Run the tests with the following command:
```bash
mvn test
