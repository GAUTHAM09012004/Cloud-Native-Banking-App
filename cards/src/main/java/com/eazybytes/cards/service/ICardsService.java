package com.eazybytes.cards.service;

import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.entity.Cards;

public interface ICardsService {

    void createCard(CardsDto cardsDto);

    CardsDto fetchCard(String mobileNumber);

    void depositMoney(String mobileNumber, double amount);

    void withdrawMoney(String mobileNumber, double amount);

    boolean deleteCard(String mobileNumber);

}
