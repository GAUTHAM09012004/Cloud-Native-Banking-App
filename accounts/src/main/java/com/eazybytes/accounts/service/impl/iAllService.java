package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CardsDto;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.AllService;
import com.eazybytes.accounts.service.Feign.CardFeignClient;
import com.eazybytes.accounts.service.Feign.LoanFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class iAllService implements AllService
{
    AccountsRepository accountsRepository;
    CustomerRepository customerRepository;
    CardFeignClient cardFeignClient;
    LoanFeignClient loanFeignClient;
    public CustomerDetailsDto FetchallDetails(String mobileNumber,String correlationId)
    {
        ResponseEntity<CardsDto> cardsDtoResponseEntity =cardFeignClient.fetchCardDetails(correlationId,mobileNumber);
        CardsDto cardsDto = cardsDtoResponseEntity.getBody();
        Customer customer=customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()->new ResourceNotFoundException("Customer","mobileNumber",mobileNumber)
        );
        CustomerDto customerDto=CustomerDto.builder().
                name(customer.getName()).
                email(customer.getEmail()).
                mobileNumber(customer.getMobileNumber()).
                build();
        Accounts accounts=accountsRepository.findByCustomer(customer).orElseThrow(
                ()->new ResourceNotFoundException("Accounts","mobileNumber",mobileNumber)
        );
        AccountsDto accountsDto=AccountsDto.builder().
                accountType(accounts.getAccountType()).
                branchAddress(accounts.getBranchAddress()).
                build();
        CustomerDetailsDto customerDetailsDto=CustomerDetailsDto.builder().
                customerDto(customerDto).
                accountsDto(accountsDto).
                cardsDto(cardsDto).
                build();
        return customerDetailsDto;
    }
}
