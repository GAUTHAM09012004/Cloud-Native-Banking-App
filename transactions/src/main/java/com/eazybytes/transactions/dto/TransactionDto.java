package com.eazybytes.transactions.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {
    private Long accountNumber;
    private Double amount;
    private String transactionType;
    private String description;
    private LocalDateTime transactionDate;
}
