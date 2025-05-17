package com.anvil_shield.rate_service.service;

import com.anvil_shield.rate_service.exception.ExternalApiException;
import com.anvil_shield.rate_service.io.ExchangeRateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        // Setup the API key in the service
        ReflectionTestUtils.setField(rateExchangeService, "accessKey", API_KEY);

        // Setup WebClient mock chain - this is common for all tests
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void getExchangeRate_SuccessResponse_ReturnsExchangeRateData() {
        // Setup successful JSON response
        String successResponse =
                "{\"success\":true,\"base\":\"USD\",\"date\":\"2024-05-12\",\"rates\":{\"EUR\":0.92}}";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(successResponse));

        // Execute service method
        Mono<ExchangeRateResponse> result = rateExchangeService.getExchangeRate("USD", "EUR");

        // Verify the result
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.isSuccess() &&
                                "USD".equals(response.getBase()) &&
                                "2024-05-12".equals(response.getDate()) &&
                                response.getRates().containsKey("EUR") &&
                                Math.abs(response.getRates().get("EUR").doubleValue() - 0.92) < 0.001
                )
                .verifyComplete();
    }

    @Test
    public void getExchangeRate_ErrorResponse_ThrowsExternalApiException() {
        // Setup error JSON response
        String errorResponse =
                "{\"success\":false,\"error\":{\"code\":105,\"info\":\"Access restricted\"}}";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(errorResponse));

        // Execute service method
        Mono<ExchangeRateResponse> result = rateExchangeService.getExchangeRate("USD", "EUR");

        // Verify the result is an error of the expected type
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof ExternalApiException &&
                                ((ExternalApiException) throwable).getErrorResponse().getError().getCode() == 105 &&
                                "Access restricted".equals(((ExternalApiException) throwable).getErrorResponse().getError().getInfo())
                )
                .verify();
    }

    @Test
    public void getExchangeRate_InvalidJson_ThrowsRuntimeException() {
        // Setup invalid JSON response
        String invalidJson = "invalid json";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(invalidJson));

        // Execute service method
        Mono<ExchangeRateResponse> result = rateExchangeService.getExchangeRate("USD", "EUR");

        // Verify the result is an error of the expected type
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Failed to parse API response")
                )
                .verify();
    }

    @Test
    public void getExchangeRate_WebClientError_PropagatesError() {
        // Setup WebClient to return an error
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("Network error")));

        // Execute service method
        Mono<ExchangeRateResponse> result = rateExchangeService.getExchangeRate("USD", "EUR");

        // Verify the error is propagated
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Network error")
                )
                .verify();
    }
}
