package com.eazybytes.transactions.controller;

import com.eazybytes.transactions.dto.TransactionDto;
import com.eazybytes.transactions.service.ITransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions/api")
@RequiredArgsConstructor
public class TransactionsController {

    private final ITransactionsService transactionsService;

    @PostMapping("/create")
    public ResponseEntity<String> createTransaction(@RequestBody TransactionDto transactionDto) {
        transactionsService.saveTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transaction saved successfully");
    }

    @GetMapping("/fetch/{accountNumber}")
    public ResponseEntity<List<TransactionDto>> fetchTransactions(@PathVariable Long accountNumber) {
        return ResponseEntity.ok(transactionsService.getTransactionsByAccountNumber(accountNumber));
    }
}
