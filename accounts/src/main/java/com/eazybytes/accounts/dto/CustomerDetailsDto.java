package com.eazybytes.accounts.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDetailsDto {

    private CustomerDto customerDto;

    private AccountsDto accountsDto;

    private CardsDto cardsDto;

}