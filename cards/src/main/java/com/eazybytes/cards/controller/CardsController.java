package com.eazybytes.cards.controller;

import com.eazybytes.cards.constants.CardsConstants;
import com.eazybytes.cards.dto.CardsContactInfoDto;
import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.dto.ResponseDto;
import com.eazybytes.cards.service.ICardsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
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
@RequestMapping(path = "/cards/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Cards API", description = "APIs for managing cards and transactions")
public class CardsController {

    @Value("${info.build.version}")
    private String buildVersion;

    private static final Logger logger = LoggerFactory.getLogger(CardsController.class);

    @Autowired
    private Environment environment;

    @Autowired
    private CardsContactInfoDto cardsContactInfoDto;

    @Autowired
    private ICardsService iCardsService;

    @Operation(summary = "Create card", description = "Creates a new card for the customer.")
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createCard(@RequestBody CardsDto cardsDto) {
        iCardsService.createCard(cardsDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(CardsConstants.STATUS_201, CardsConstants.MESSAGE_201));
    }

    @Operation(summary = "Fetch card details", description = "Retrieves the card details for a given mobile number.")
    @GetMapping("/fetch")
    @CircuitBreaker(name = "detailsForCardSupport", fallbackMethod = "fallbackFetchCardDetails")
    @Retry(name = "detailsForCardSupport")
    @RateLimiter(name = "detailsForCardSupport")
    public ResponseEntity<CardsDto> fetchCardDetails(
            @Parameter(description = "Correlation ID for distributed tracing") @RequestHeader("eazybank-correlation-id") String correlationId,
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        logger.debug("Correlation id found: {}", correlationId);
        CardsDto cards = iCardsService.fetchCard(mobileNumber);
        return ResponseEntity.status(HttpStatus.OK).body(cards);
    }

    public ResponseEntity<CardsDto> fallbackFetchCardDetails(String correlationId, String mobileNumber, Throwable throwable) {
        logger.error("Fallback method executed due to: {}", throwable.toString());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new CardsDto());
    }

    @Operation(summary = "Deposit money", description = "Deposits the specified amount to the card linked to the provided mobile number.")
    @PostMapping("/deposit")
    public ResponseEntity<ResponseDto> depositMoney(
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber,
            @Parameter(description = "Amount to deposit") @RequestParam double amount) {
        iCardsService.depositMoney(mobileNumber, amount);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(CardsConstants.STATUS_200, "Amount deposited successfully"));
    }

    @Operation(summary = "Withdraw money", description = "Withdraws the specified amount from the card linked to the provided mobile number.")
    @PostMapping("/withdraw")
    public ResponseEntity<ResponseDto> withdrawMoney(
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber,
            @Parameter(description = "Amount to withdraw") @RequestParam double amount) {
        iCardsService.withdrawMoney(mobileNumber, amount);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(CardsConstants.STATUS_200, "Amount withdrawn successfully"));
    }

    @Operation(summary = "Delete card details", description = "Deletes the card linked to the provided mobile number.")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteCardDetails(
            @Parameter(description = "Mobile number of the customer. Must be exactly 10 digits.")
            @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
        boolean isDeleted = iCardsService.deleteCard(mobileNumber);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto(CardsConstants.STATUS_200, CardsConstants.MESSAGE_200));
        } else {
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ResponseDto(CardsConstants.STATUS_417, CardsConstants.MESSAGE_417_DELETE));
        }
    }

    @Operation(summary = "Get build info", description = "Returns the build version of the cards service.")
    @GetMapping("/build-info")
    public ResponseEntity<String> getBuildInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(buildVersion);
    }

    @Operation(summary = "Get Java version", description = "Returns the Java version of the running environment.")
    @GetMapping("/java-version")
    public ResponseEntity<String> getJavaVersion() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(environment.getProperty("JAVA_HOME"));
    }

    @Operation(summary = "Get contact information", description = "Returns contact details for the cards service.")
    @GetMapping("/contact-info")
    public ResponseEntity<CardsContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardsContactInfoDto);
    }
}
