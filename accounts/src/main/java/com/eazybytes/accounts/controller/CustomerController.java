package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.CustomerConstants;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.service.impl.CustomerServiceImpl;
import com.eazybytes.accounts.service.impl.iAllService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/customer/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
@AllArgsConstructor
@Tag(name = "Customer API", description = "APIs for managing customer data and details")
public class CustomerController {

    private final CustomerServiceImpl customerService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final iAllService iCardsService;

    @Operation(summary = "Create customer", description = "Creates a new customer with the provided details.")
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        customerService.createCustomer(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CustomerConstants.STATUS_201, CustomerConstants.MESSAGE_201));
    }

    @Operation(summary = "Fetch customer details", description = "Retrieves customer details using their mobile number.")
    @GetMapping("/fetch")
    public ResponseEntity<Customer> fetchCustomerDetails(
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        Customer customer = customerService.fetchCustomer(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    @Operation(summary = "Fetch complete customer details", description = "Retrieves full details of the customer including accounts, loans, and cards.")
    @GetMapping("/fetch/all")
    public ResponseEntity<CustomerDetailsDto> fetchAllCustomerDetails(
            @Parameter(description = "Correlation ID for distributed tracing") @RequestHeader("eazybank-correlation-id") String correlationId,
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {

        logger.info("Correlation id found: {}", correlationId);
        CustomerDetailsDto customer = iCardsService.FetchallDetails(correlationId, mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    @Operation(summary = "Update customer details", description = "Updates customer information for the provided details.")
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateCustomerDetails(@Valid @RequestBody CustomerDto customerDto) {
        boolean isUpdated = customerService.updateCustomer(customerDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CustomerConstants.STATUS_200, CustomerConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CustomerConstants.STATUS_417, CustomerConstants.MESSAGE_417_UPDATE));
        }
    }
}
