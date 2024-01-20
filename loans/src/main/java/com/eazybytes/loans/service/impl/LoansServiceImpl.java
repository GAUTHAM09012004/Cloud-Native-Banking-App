package com.eazybytes.loans.service.impl;

import com.eazybytes.loans.constants.LoansConstants;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.exception.InvalidPaymentException;
import com.eazybytes.loans.exception.LoanAlreadyExistsException;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.repository.LoansRepository;
import com.eazybytes.loans.service.ILoansService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class LoansServiceImpl implements ILoansService {

    private LoansRepository loansRepository;

    @Override
    public void createLoan(LoansDto loansDto) {
        Optional<Loans> loans = loansRepository.findByMobileNumber(loansDto.getMobileNumber());
        if (loans.isPresent()) {
            throw new LoanAlreadyExistsException("Loan already registered with given mobileNumber " + loansDto.getMobileNumber());
        }
        Loans loans1 = Loans.builder()
                .mobileNumber(loansDto.getMobileNumber())
                .loanNumber(Long.toString(100000000000L + new Random().nextInt(900000000)))
                .loanType(loansDto.getLoanType())
                .totalLoan(loansDto.getTotalLoan())
                .amountPaid(0)
                .outstandingAmount(LoansConstants.NEW_LOAN_LIMIT)
                .build();
        loansRepository.save(loans1);
    }

    @Override
    public LoansDto fetchLoan(String mobileNumber) {
        Optional<Loans> loans = loansRepository.findByMobileNumber(mobileNumber);
        if (!loans.isPresent()) {
            throw new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber);
        }
        LoansDto loansDto = LoansDto.builder()
                .mobileNumber(loans.get().getMobileNumber())
                .loanNumber(loans.get().getLoanNumber())
                .loanType(loans.get().getLoanType())
                .totalLoan(loans.get().getTotalLoan())
                .amountPaid(loans.get().getAmountPaid())
                .outstandingAmount(loans.get().getOutstandingAmount())
                .build();
        return loansDto;
    }


    @Override
    public int getOutstandingAmount(String mobileNumber) {
        Loans loan = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        return loan.getOutstandingAmount();
    }

    @Override
    public int getAmountPaid(String mobileNumber) {
        Loans loan = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        return loan.getAmountPaid();
    }


    @Override
    public void makePayment(String mobileNumber, int amount) {
        Loans loan = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );

        double currentAmountPaid = loan.getAmountPaid();
        double newAmountPaid = currentAmountPaid + amount;

        if (newAmountPaid > loan.getTotalLoan()) {
            throw new InvalidPaymentException("Amount paid exceeds the total loan amount");
        }

        double currentOutstandingAmount = loan.getOutstandingAmount();
        double newOutstandingAmount = currentOutstandingAmount - amount;

        loan.setAmountPaid((int) newAmountPaid);
        loan.setOutstandingAmount((int) newOutstandingAmount);
        loansRepository.save(loan);
    }


    @Override
    public boolean deleteLoan(String mobileNumber) {
        Loans loans = loansRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Loan", "mobileNumber", mobileNumber)
        );
        loansRepository.deleteById(loans.getLoanId());
        return true;
    }

}
