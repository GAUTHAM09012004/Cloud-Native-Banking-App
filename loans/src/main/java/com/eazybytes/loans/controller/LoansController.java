package com.eazybytes.loans.controller;

import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.dto.ResponseDto;
import com.eazybytes.loans.service.ILoansService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(path = "/loans/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
@Validated
@Tag(name = "Loans API", description = "APIs for managing loan accounts and transactions")
public class LoansController {

    private final ILoansService iLoansService;

    private static final Logger logger = LoggerFactory.getLogger(LoansController.class);

    @Operation(summary = "Create loan", description = "Creates a new loan for the customer.")
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLoan(@RequestBody LoansDto loansDto) {
        iLoansService.createLoan(loansDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(LoansConstants.STATUS_201, LoansConstants.MESSAGE_201));
    }

    @Operation(
            summary = "Fetch loan details",
            description = "Retrieves loan details for the provided mobile number.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Loan details fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid mobile number", content = @Content),
                    @ApiResponse(responseCode = "503", description = "Service unavailable - fallback response", content = @Content)
            }
    )
    @CircuitBreaker(name = "fetchLoanDetailsCB", fallbackMethod = "fetchLoanDetailsFallback")
    @Retry(name = "fetchLoanDetailsRetry")
    @TimeLimiter(name = "fetchLoanDetailsTL")
    @GetMapping("/fetch")
    public CompletableFuture<ResponseEntity<LoansDto>> fetchLoanDetails(
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
            String mobileNumber) {

        return CompletableFuture.supplyAsync(() -> {
            LoansDto loansDto = iLoansService.fetchLoan(mobileNumber);
            return ResponseEntity.ok(loansDto);
        });
    }

    private CompletableFuture<ResponseEntity<LoansDto>> fetchLoanDetailsFallback(String mobileNumber, Throwable throwable) {
        logger.error("Fallback method invoked for fetchLoanDetails due to exception: {}", throwable.toString());
        LoansDto fallbackDto = new LoansDto();
        return CompletableFuture.completedFuture(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(fallbackDto));
    }

    @Operation(summary = "Delete loan details", description = "Deletes the loan linked to the provided mobile number.")
    @CircuitBreaker(name = "deleteLoanDetailsCB", fallbackMethod = "deleteLoanDetailsFallback")
    @Retry(name = "deleteLoanDetailsRetry")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteLoanDetails(
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "^[0-9]{10}$", message = "Mobile number must be 10 digits")
            String mobileNumber) {
        boolean isDeleted = iLoansService.deleteLoan(mobileNumber);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(LoansConstants.STATUS_417, LoansConstants.MESSAGE_417_DELETE));
        }
    }

    @Operation(summary = "Update loan", description = "Updates the details of an existing loan.")
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateLoan(@RequestBody LoansDto loansDto) {
        iLoansService.updateLoan(loansDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(LoansConstants.STATUS_200, LoansConstants.MESSAGE_200_UPDATE));
    }

    private ResponseEntity<ResponseDto> deleteLoanDetailsFallback(String mobileNumber, Throwable throwable) {
        logger.error("Fallback method invoked for deleteLoanDetails due to exception: {}", throwable.toString());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ResponseDto(LoansConstants.STATUS_503, LoansConstants.MESSAGE_503));
    }
}
