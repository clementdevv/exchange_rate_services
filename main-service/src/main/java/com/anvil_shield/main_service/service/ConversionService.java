package com.anvil_shield.main_service.service;

import com.anvil_shield.main_service.entity.ConversionEntity;
import com.anvil_shield.main_service.exception.ExternalApiException;
import com.anvil_shield.main_service.io.ConversionResponse;
import com.anvil_shield.main_service.io.ErrorResponse;
import com.anvil_shield.main_service.io.ExchangeRateResponse;
import com.anvil_shield.main_service.repository.ConversionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ConversionService {

    private final ConversionRepository repository;
    public final WebClient webClient;
    ;

    @Value("${internal.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConversionResponse convert(String from, String to, BigDecimal amount, String userEmail) {
        ExchangeRateResponse rateResponse = getExchangeRate(from, to); // blocking version

        BigDecimal rate = BigDecimal.valueOf(rateResponse.getRates().get(to));
        BigDecimal converted = amount.multiply(rate);

        ConversionEntity entity = new ConversionEntity();
        entity.setFromCurrency(from);
        entity.setToCurrency(to);
        entity.setOriginalAmount(amount);
        entity.setConvertedAmount(converted);
        entity.setRate(rate);
        entity.setTimestamp(new Timestamp(System.currentTimeMillis()));

        ConversionEntity saved = repository.saveConversion(entity);

        // Log conversion
        repository.logConversion(
                saved.getId(),
                userEmail,
                String.format("Converted %.2f %s to %.2f %s at rate %s",
                        amount, from, converted, to, rate.toPlainString())
        );

        return new ConversionResponse(
                entity.getFromCurrency(),
                entity.getToCurrency(),
                entity.getOriginalAmount(),
                entity.getConvertedAmount(),
                entity.getRate(),
                entity.getTimestamp()
        );
    }


    public ExchangeRateResponse getExchangeRate(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/rate")
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .header("X-INTERNAL-API-KEY", apiKey)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("API Error: " + errorBody);
                                    try {
                                        ErrorResponse error = objectMapper.readValue(errorBody, ErrorResponse.class);
                                        return Mono.error(new ExternalApiException(error));
                                    } catch (JsonProcessingException e) {
                                        return Mono.error(new RuntimeException("Failed to parse error response", e));
                                    }
                                })
                )
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        JsonNode root = objectMapper.readTree(json);
                        if (root.has("success") && root.get("success").asBoolean()) {
                            return objectMapper.readValue(json, ExchangeRateResponse.class);
                        } else {
                            ErrorResponse error = objectMapper.readValue(json, ErrorResponse.class);
                            throw new ExternalApiException(error);
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to parse API response", e);
                    }
                })
                .block(); // block the reactive chain and return the response
    }
}

