package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.entity.Accounts;

import java.util.List;

public interface IAccountsService {

    void createAccount(AccountsDto accountsDto);
    AccountsDto fetchAccount(String mobileNumber);
    void updateAccount(AccountsDto accountsDto,String mobileNumber);
    void deleteAccount(String mobileNumber);

}
