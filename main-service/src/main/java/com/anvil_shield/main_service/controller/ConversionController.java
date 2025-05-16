package com.anvil_shield.main_service.controller;

import com.anvil_shield.main_service.io.ConversionRequest;
import com.anvil_shield.main_service.io.ConversionResponse;
import com.anvil_shield.main_service.service.ConversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/convert")
@RequiredArgsConstructor
public class ConversionController {

    private final ConversionService service;

    @PostMapping
    public ResponseEntity<?> convert(@Valid @RequestBody ConversionRequest request, @CurrentSecurityContext(expression = "authentication?.name") String email) {

        final ConversionResponse conversionResponse = service.convert(request.getFrom(), request.getTo(), request.getAmount(), email);

        return ResponseEntity.ok(conversionResponse);
    }
}
