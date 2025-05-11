package com.clementdevv.rate_service.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {
    @GetMapping("/status")
    public Map<String, String> getStatus() {
        return Map.of("status", "UP");
    }
}
