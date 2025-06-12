package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.AccountsMsgDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService
{

    private static final Logger log = LoggerFactory.getLogger(AccountsServiceImpl.class);

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    @Override
    public void createAccount(AccountsDto accountsDto) {

        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(accountsDto.getMobileNumber());
        if (optionalCustomer.isEmpty()) {
            throw new ResourceNotFoundException("Customer","Mobile number",accountsDto.getMobileNumber());
        }
        Customer savedCustomer = optionalCustomer.get();
        Accounts accounts = Accounts.builder().
                customer(savedCustomer).
                accountType(accountsDto.getAccountType()).
                branchAddress(AccountsConstants.ADDRESS).
                build();
        sendCommunication(accounts, savedCustomer);
        accountsRepository.save(accounts);
    }

    private void sendCommunication(Accounts account, Customer customer) {
        var accountsMsgDto = new AccountsMsgDto(account.getAccountId(), customer.getName(),
                customer.getEmail(), customer.getMobileNumber());
        log.info("Sending Communication request for the details: {}", accountsMsgDto);
        var result = streamBridge.send("sendCommunication-out-0", accountsMsgDto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
    }

    @Override
    public AccountsDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomer(customer).orElseThrow(
                () -> new ResourceNotFoundException("Accounts", "mobileNumber", mobileNumber)
        );
        return AccountsDto.builder().
                accountType(accounts.getAccountType()).
                branchAddress(accounts.getBranchAddress()).
                build();
    }

    @Override
    @Transactional
    public void updateAccount(AccountsDto accountsDto, String mobileNumber) {

        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomer(customer).orElseThrow(
                () -> new ResourceNotFoundException("Accounts", "mobileNumber", mobileNumber)
        );
        accounts.setAccountType(accountsDto.getAccountType());
        accounts.setBranchAddress(accountsDto.getBranchAddress());
        accountsRepository.save(accounts);
    }

    @Transactional
    @Override
    public void deleteAccount(String mobileNumber) {
        log.info("Starting deleteAccount for mobileNumber: {}", mobileNumber);
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> {
                    log.error("Customer not found for mobileNumber: {}", mobileNumber);
                    return new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber);
                });
        log.info("Customer found: {}", customer.getName()); //
        Accounts accounts = accountsRepository.findByCustomer(customer)
                .orElseThrow(() -> {
                    log.error("Account not found for customer with mobileNumber: {}", mobileNumber);
                    return new ResourceNotFoundException("Accounts", "MobileNumber", mobileNumber);
                });
        log.info("Account found with ID: {}", accounts.getAccountId());
        accountsRepository.delete(accounts);
        accountsRepository.flush();
        log.info("Successfully deleted account for mobileNumber: {}", mobileNumber);
    }

}
