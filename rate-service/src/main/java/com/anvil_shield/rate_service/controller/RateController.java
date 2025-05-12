package com.anvil_shield.rate_service.controller;

import com.anvil_shield.rate_service.io.ExchangeRateResponse;
import com.anvil_shield.rate_service.service.RateExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class RateController {

    private final RateExchangeService rateExchangeService;

    @GetMapping("/rate")
    public Mono<ExchangeRateResponse> getRate(
            @RequestParam String from,
            @RequestParam String to
    ) {
        return rateExchangeService.getExchangeRate(from, to);
    }
}
