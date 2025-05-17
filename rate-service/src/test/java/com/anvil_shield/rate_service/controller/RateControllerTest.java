package com.anvil_shield.rate_service.controller;

import com.anvil_shield.rate_service.io.ExchangeRateResponse;
import com.anvil_shield.rate_service.security.NoSecurityWebFluxConfig;
import com.anvil_shield.rate_service.service.RateExchangeService;

import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;

@WebFluxTest(RateController.class)
@Import(NoSecurityWebFluxConfig.class)
public class RateControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private RateExchangeService rateExchangeService;

    @Test
    public void RateController_GetRate_ReturnExchangeRate() {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setSuccess(true);
        mockResponse.setBase("USD");
        mockResponse.setDate("2024-01-01");
        mockResponse.setRates(Map.of("KES", 132.55));

        Mockito.when(rateExchangeService.getExchangeRate(anyString(), anyString()))
                .thenReturn(Mono.just(mockResponse));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/rate")
                        .queryParam("from", "USD")
                        .queryParam("to", "KES")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.base").isEqualTo("USD")
                .jsonPath("$.rates.KES").isEqualTo(132.55);
    }
}
