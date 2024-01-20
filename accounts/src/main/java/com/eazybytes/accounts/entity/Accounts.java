package com.eazybytes.accounts.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
@Table(name="accounts")
public class Accounts{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @Column(name="account_number")
    private Long accountId;

    @OneToOne
    @JsonManagedReference
    @JoinColumn(name = "customer_id")
    public Customer customer;

    @Column(name="account_type")
    private String accountType;

    @Column(name="branch_address")
    private String branchAddress;

}
