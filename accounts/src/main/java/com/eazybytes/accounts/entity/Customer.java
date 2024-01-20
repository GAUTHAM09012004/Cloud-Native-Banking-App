package com.eazybytes.accounts.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;

@Builder
@Entity
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
@Table(name="customers")
public class Customer  {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO,generator="native")
    @Column(name="customer_id")
    private Long customerId;

    private String name;

    private String email;

    @Column(name="mobile_number")
    private String mobileNumber;

    @JsonIgnore
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Accounts account;
}
