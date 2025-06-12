package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.AccountsConstants;
import com.eazybytes.accounts.dto.AccountsContactInfoDto;
import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.service.IAccountsService;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/accounts/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
@Tag(name = "Accounts API", description = "APIs for managing accounts operations")
public class AccountsController {

    private final IAccountsService iAccountsService;

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    public AccountsController(IAccountsService iAccountsService) {
        this.iAccountsService = iAccountsService;
    }

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private AccountsContactInfoDto accountsContactInfoDto;

    @Operation(summary = "Create a new account", description = "Creates a new customer account based on the provided details.")
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createAccount(@Valid @RequestBody AccountsDto accountsDto) {
        iAccountsService.createAccount(accountsDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto(AccountsConstants.STATUS_201, AccountsConstants.MESSAGE_201));
    }

    @Operation(summary = "Fetch account details", description = "Retrieves account details for the provided mobile number.")
    @GetMapping("/fetch")
    public ResponseEntity<AccountsDto> fetchAccountDetails(
            @Parameter(description = "Mobile number of the account holder. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        AccountsDto account = iAccountsService.fetchAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(account);
    }

    @Operation(summary = "Update account details", description = "Updates an existing account using the provided account data and mobile number.")
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccountDetails(
            @Valid @RequestBody AccountsDto accountsDto,
            @RequestParam(required = false) Long AccountId,
            @Parameter(description = "Mobile number of the account holder. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        iAccountsService.updateAccount(accountsDto, mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
    }

    @Operation(summary = "Delete an account", description = "Deletes an account using the provided mobile number.")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCustomerDetails(
            @Valid @RequestBody AccountsDto accountsDto,
            @RequestParam(required = false) Long AccountId,
            @Parameter(description = "Mobile number of the account holder. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        iAccountsService.deleteAccount(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto(AccountsConstants.STATUS_200, AccountsConstants.MESSAGE_200));
    }

    @Operation(summary = "Get build version", description = "Returns the build version of this microservice.")
    @GetMapping("/build-info")
    @Retry(name = "getBuildInfo", fallbackMethod = "getBuildInfoFallback")
    public ResponseEntity<String> getBuildInfo() {
        logger.debug("getBuildInfo() method Invoked");
        return ResponseEntity.status(HttpStatus.OK).body(buildVersion);
    }

    public ResponseEntity<String> getBuildInfoFallback(Throwable throwable) {
        logger.debug("getBuildInfoFallback() method Invoked");
        return ResponseEntity.status(HttpStatus.OK).body("0.9");
    }

    @Operation(summary = "Get Java version", description = "Returns the Java version used to run this service.")
    @GetMapping("/java-version")
    @RateLimiter(name = "getJavaVersion", fallbackMethod = "getJavaVersionFallback")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity.status(HttpStatus.OK).body(environment.getProperty("JAVA_HOME"));
    }

    public ResponseEntity<String> getJavaVersionFallback(Throwable throwable) {
        return ResponseEntity.status(HttpStatus.OK).body("Java 17");
    }

    @Operation(summary = "Get contact info", description = "Returns the contact information for the Accounts microservice.")
    @GetMapping("/contact-info")
    @Bulkhead(name = "BULKHEAD_SERVICE", fallbackMethod = "bulkheadFallBack")
    public ResponseEntity<AccountsContactInfoDto> getContactInfo() {
        return ResponseEntity.status(HttpStatus.OK).body(accountsContactInfoDto);
    }

    public ResponseEntity<AccountsContactInfoDto> bulkheadFallBack(Exception t) {
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
