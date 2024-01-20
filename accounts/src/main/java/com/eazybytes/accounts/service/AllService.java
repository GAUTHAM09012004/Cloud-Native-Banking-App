package com.eazybytes.accounts.service;

import com.eazybytes.accounts.dto.CustomerDetailsDto;

public interface AllService {
    CustomerDetailsDto FetchallDetails(String mobileNumber, String correlationId);
}
