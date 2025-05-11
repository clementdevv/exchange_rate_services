package com.clementdevv.rate_service.service;

import com.clementdevv.rate_service.config.ExchangeApiProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.Map;

@Service
public class ExchangeRateService {

    private final ExchangeApiProperties properties;
    private final WebClient webClient;

    @Autowired
    public ExchangeRateService(ExchangeApiProperties properties, WebClient exchangeRateWebClient) {
        this.properties = properties;
        this.webClient = exchangeRateWebClient;
        // this.webClient = WebClient.builder()
        //                           .baseUrl("https://api.exchangeratesapi.io")
        //                           .defaultHeader("Accept", "application/json")
        //                           .build();
    }

    public Mono<Map> testApiConnection() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v1/latest")  // Add the proper endpoint path
                    .queryParam("access_key", properties.getKey())
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    return Mono.just(Map.of("error", "API call failed: " + e.getRawStatusCode() + " " + e.getStatusText() +
                        " from " + e.getRequest().getMethod() + " " + e.getRequest().getURI() + 
                        ", but response failed with cause: " + e.getMessage()));
                });
    }

    public String fetchRates() {
        // For RestTemplate version — useful for synchronous calls
        return "Deprecated in WebFlux setup";
    }
}