package com.anvil_shield.main_service.io;

import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateResponse {
    private boolean success;
    private long timestamp;
    private String base;
    private String date;
    private Map<String, Double> rates;
}