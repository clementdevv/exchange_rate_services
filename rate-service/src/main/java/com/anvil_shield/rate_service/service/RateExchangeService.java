package com.anvil_shield.rate_service.service;

import com.anvil_shield.rate_service.exception.ExternalApiException;
import com.anvil_shield.rate_service.io.ErrorResponse;
import com.anvil_shield.rate_service.io.ExchangeRateResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RateExchangeService {

    public final WebClient webClient;

    @Value("${rate-service.api-key}")
    private String accessKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mono<ExchangeRateResponse> getExchangeRate(String from, String to) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/latest")
                        .queryParam("access_key", accessKey)
                        .queryParam("base", from)
                        .queryParam("symbols", to)
                        .build())
                .retrieve()
                .bodyToMono(String.class) // fetch raw JSON first
                .flatMap(json -> {
                    try {
                        JsonNode root = objectMapper.readTree(json);
                        if (root.has("success") && !root.get("success").asBoolean()) {
                            ErrorResponse error = objectMapper.readValue(json, ErrorResponse.class);
                            return Mono.error(new ExternalApiException(error));
                        } else {
                            ExchangeRateResponse data = objectMapper.readValue(json, ExchangeRateResponse.class);
                            return Mono.just(data);
                        }
                    } catch (JsonProcessingException e) {
                        return Mono.error(new RuntimeException("Failed to parse API response", e));
                    }
                });
    }
}
