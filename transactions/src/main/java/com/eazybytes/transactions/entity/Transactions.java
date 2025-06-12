package com.eazybytes.transactions.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    private String transactionType; // DEBIT / CREDIT

    private Double amount;

    private String description;

    private LocalDateTime transactionDate;

    private Long accountNumber;  // Foreign key ref to accounts table if you want to enforce

}