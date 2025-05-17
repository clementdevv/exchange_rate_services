package com.anvil_shield.rate_service.service;

import com.anvil_shield.rate_service.exception.ExternalApiException;
import com.anvil_shield.rate_service.io.ExchangeRateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class RateExchangeServiceTest {
    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private RateExchangeService rateExchangeService;

    private final String API_KEY = "test-api-key";

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(rateExchangeService, "accessKey", API_KEY);
    }

    @Test
    public void getExchangeRate_SuccessResponse_ReturnsExchangeRateData() {
        // Setup WebClient mock chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Setup successful JSON response
        String successResponse =
                "{\"success\":true,\"base\":\"USD\",\"date\":\"2024-05-12\",\"rates\":{\"EUR\":0.92}}";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(successResponse));

        // Execute service method
        Mono<ExchangeRateResponse> result = rateExchangeService.getExchangeRate("USD", "EUR");

        // Verify the result
        StepVerifier.create(result)
                .expectNextMatches(response -> {
                    return response.isSuccess() &&
                            "USD".equals(response.getBase()) &&
                            response.getRates().containsKey("EUR") &&
                            response.getRates().get("EUR").doubleValue() == 0.92;
                })
                .verifyComplete();
    }

    @Test
    public void getExchangeRate_ErrorResponse_ReturnsMonoError() {
        // Setup WebClient mock chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Setup error JSON response
        String errorResponse =
                "{\"success\":false,\"error\":{\"code\":105,\"type\":\"base_currency_access_restricted\",\"info\":\"Access restricted\"}}";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(errorResponse));

        // Execute service method
        Mono<ExchangeRateResponse> result = rateExchangeService.getExchangeRate("USD", "EUR");

        // Verify the result is an error
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ExternalApiException &&
                                ((ExternalApiException) throwable).getErrorResponse().getError().getCode() == 105)
                .verify();
    }

    @Test
    public void getExchangeRate_InvalidJson_ReturnsMonoError() {
        // Setup WebClient mock chain
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        // Setup invalid JSON response
        String invalidJson = "invalid json";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidJson));

        // Execute service method
        Mono<ExchangeRateResponse> result = rateExchangeService.getExchangeRate("USD", "EUR");

        // Verify the result is an error
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Failed to parse API response"))
                .verify();
    }
}
