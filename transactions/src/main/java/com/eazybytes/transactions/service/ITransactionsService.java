package com.eazybytes.transactions.service;

import com.eazybytes.transactions.dto.TransactionDto;

import java.util.List;

public interface ITransactionsService {
    void saveTransaction(TransactionDto transactionDto);
    List<TransactionDto> getTransactionsByAccountNumber(Long accountNumber);
}

