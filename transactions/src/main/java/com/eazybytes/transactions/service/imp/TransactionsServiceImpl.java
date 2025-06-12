package com.eazybytes.transactions.service.impl;

import com.eazybytes.transactions.dto.TransactionDto;
import com.eazybytes.transactions.entity.Transaction;
import com.eazybytes.transactions.repository.TransactionsRepository;
import com.eazybytes.transactions.service.ITransactionsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionsServiceImpl implements ITransactionsService {

    private final TransactionsRepository transactionsRepository;

    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        BeanUtils.copyProperties(transactionDto, transaction);
        transactionsRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getTransactionsByAccountNumber(Long accountNumber) {
        return transactionsRepository.findByAccountNumber(accountNumber)
                .stream()
                .map(tx -> {
                    TransactionDto dto = new TransactionDto();
                    BeanUtils.copyProperties(tx, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
