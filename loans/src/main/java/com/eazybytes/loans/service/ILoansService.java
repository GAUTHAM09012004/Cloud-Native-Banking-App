package com.eazybytes.loans.service;

import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;

public interface ILoansService {


    void createLoan(LoansDto loansDto);

    LoansDto fetchLoan(String mobileNumber);

    int getOutstandingAmount(String mobileNumber);

    void makePayment(String mobileNumber, int amount);

    int getAmountPaid(String mobileNumber);

    boolean deleteLoan(String mobileNumber);

}
