package com.clementdevv.rate_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Configuration
public class WebClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);
    
    @Value("${exchange-rate-api.base-url}")
    private String apiBaseUrl;
    
    @Value("${exchange-rate-api.api-key:}")
    private String apiKey;
    
    @Bean
    public WebClient exchangeRateWebClient() {
        WebClient.Builder builder = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .filter(logRequest())
                .filter(logResponse());
        
        // Only add API key if it's provided
        if (apiKey != null && !apiKey.isEmpty()) {
            builder.defaultHeader("apikey", apiKey);
        }
        
        return builder.build();
    }
    
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> 
                values.forEach(value -> logger.info("{}={}", name, value))
            );
            return Mono.just(clientRequest);
        });
    }
    
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logger.info("Response status: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }
}
