package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.constants.CustomerConstants;
import com.eazybytes.accounts.dto.CustomerDetailsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.dto.ResponseDto;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.service.impl.CustomerServiceImpl;
import com.eazybytes.accounts.service.impl.iAllService;
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
public class CustomerController {

    private final CustomerServiceImpl customerService;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final iAllService iCardsService;


    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        customerService.createCustomer(customerDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CustomerConstants.STATUS_201, CustomerConstants.MESSAGE_201));
    }

    @GetMapping("/fetch")
    public ResponseEntity<Customer> fetchCustomerDetails(@RequestParam
                                                         @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                         String mobileNumber) {
        Customer customer = customerService.fetchCustomer(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

    @GetMapping("/fetch/all")
    public ResponseEntity<CustomerDetailsDto> fetchAllCustomerDetails(@RequestHeader("eazybank-correlation-id") String correlationId,
                                                                      @RequestParam
                                                                      @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits")
                                                                      String mobileNumber) {

        logger.info("Correlation id found: {}", correlationId);
        CustomerDetailsDto customer = iCardsService.FetchallDetails(correlationId,mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(customer);
    }

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

