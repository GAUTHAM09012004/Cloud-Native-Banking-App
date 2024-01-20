package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Customer;

public interface CustomerService
{
    void createCustomer(CustomerDto customerDto);
    Customer fetchCustomer(String mobileNumber);
    boolean updateCustomer(CustomerDto customerDto);
}
