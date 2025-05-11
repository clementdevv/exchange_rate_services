package com.clementdevv.rate_service.controller;

import com.clementdevv.rate_service.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/")
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @Autowired
    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
        System.out.println("ExchangeRateController initialized");
    }

    @GetMapping("/test-api")
    public Mono<ResponseEntity<Map>> testApi() {
        System.out.println("Received request to /test-api");
        return exchangeRateService.testApiConnection()
                .map(response -> {
                    if (response.containsKey("error")) {
                        System.err.println("Error in controller: " + response.get("error"));
                    } else {
                        System.out.println("Successful API response: " + response);
                    }
                    return ResponseEntity.ok(response);
                });
    }
}