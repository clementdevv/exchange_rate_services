package com.anvil_shield.main_service.controller;

import com.anvil_shield.main_service.config.SecurityConfig;
import com.anvil_shield.main_service.io.ConversionRequest;
import com.anvil_shield.main_service.io.ConversionResponse;
import com.anvil_shield.main_service.service.AppUserDetailsService;
import com.anvil_shield.main_service.service.ConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = ConversionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfig.class)
class ConversionControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private ConversionService conversionService;
    @MockitoBean private AppUserDetailsService appUserDetailsService; // Add this
    @Autowired private ObjectMapper objectMapper;

    private ConversionRequest request;
    private ConversionResponse response;

    @BeforeEach
    void setUp() {
        request = new ConversionRequest();
        request.setFrom("USD");
        request.setTo("EUR");
        request.setAmount(new BigDecimal("100.00"));

        response = new ConversionResponse(
                "USD", "EUR",
                new BigDecimal("100.00"),
                new BigDecimal("91.00"),
                new BigDecimal("0.91"),
                new Timestamp(System.currentTimeMillis())
        );
    }

    @Test
    @WithMockUser(username = "testuser@example.com")
    void testConvertSuccess() throws Exception {
        when(conversionService.convert(
                eq("USD"), eq("EUR"), eq(new BigDecimal("100.00")), eq("testuser@example.com"))
        ).thenReturn(response);

        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.originalAmount").value(100.00))
                .andExpect(jsonPath("$.convertedAmount").value(91.00))
                .andExpect(jsonPath("$.rate").value(0.91))
                .andExpect(jsonPath("$.timestamp").exists());
    }


}
