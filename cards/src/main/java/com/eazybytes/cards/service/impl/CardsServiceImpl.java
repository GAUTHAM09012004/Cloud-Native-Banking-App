package com.eazybytes.cards.service.impl;

import com.eazybytes.cards.constants.CardsConstants;
import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.entity.Cards;
import com.eazybytes.cards.exception.CardAlreadyExistsException;
import com.eazybytes.cards.exception.InsufficientFundsException;
import com.eazybytes.cards.exception.ResourceNotFoundException;
import com.eazybytes.cards.repository.CardsRepository;
import com.eazybytes.cards.service.ICardsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class CardsServiceImpl implements ICardsService {

    private CardsRepository cardsRepository;

    @Override
    public void createCard(CardsDto cardsDto) {
        Cards cards = cardsRepository.findByMobileNumber(cardsDto.getMobileNumber()).orElse(null);
        if (cards != null) {
            throw new CardAlreadyExistsException("Card already registered with given mobileNumber " + cardsDto.getMobileNumber());
        }
        cards = Cards.builder()
                .mobileNumber(cardsDto.getMobileNumber())
                .cardNumber(Long.toString(100000000000L + new Random().nextInt(900000000)))
                .cardType(cardsDto.getCardType())
                .totalLimit(CardsConstants.NEW_CARD_LIMIT)
                .amountUsed(0)
                .availableAmount(cardsDto.getAvailableAmount())
                .build();
        cardsRepository.save(cards);
    }

    @Override
    public CardsDto fetchCard(String mobileNumber) {
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        return CardsDto.builder()
                .mobileNumber(cards.getMobileNumber())
                .cardNumber(cards.getCardNumber())
                .cardType(cards.getCardType())
                .totalLimit(cards.getTotalLimit())
                .amountUsed(cards.getAmountUsed())
                .availableAmount(cards.getAvailableAmount())
                .build();
    }

    @Override
    public void depositMoney(String mobileNumber, double amount) {
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );

        double currentAmount = cards.getAvailableAmount();
        double newAmount = currentAmount + amount;

        cards.setAvailableAmount((int) newAmount);
        cardsRepository.save(cards);
    }

    @Override
    public void withdrawMoney(String mobileNumber, double amount) {
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );

        double currentAmount = cards.getAvailableAmount();

        if (amount > currentAmount) {
            throw new InsufficientFundsException("Insufficient funds on the card");
        }

        double newAmount = currentAmount - amount;

        cards.setAvailableAmount((int) newAmount);
        cardsRepository.save(cards);
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        cardsRepository.deleteById(cards.getCardId());
        return true;
    }
}
