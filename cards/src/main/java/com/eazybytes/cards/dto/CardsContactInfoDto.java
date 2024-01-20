package com.eazybytes.cards.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "cards")
public class CardsContactInfoDto
{
    private String message;
    Map<String, String> contactDetails;
    List<String> onCallSupport;
}