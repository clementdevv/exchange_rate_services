package com.anvil_shield.main_service.controller;

import com.anvil_shield.main_service.config.CustomAuthenticationEntryPoint;
import com.anvil_shield.main_service.config.SecurityConfig;
import com.anvil_shield.main_service.exception.GlobalExceptionHandler;
import com.anvil_shield.main_service.io.ConversionRequest;
import com.anvil_shield.main_service.io.ConversionResponse;
import com.anvil_shield.main_service.service.AppUserDetailsService;
import com.anvil_shield.main_service.service.ConversionService;
import com.anvil_shield.main_service.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConversionController.class)
@Import({SecurityConfig.class, MethodValidationPostProcessor.class})
class ConversionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @MockitoBean
    private GlobalExceptionHandler globalExceptionHandler;

    @MockitoBean
    private ConversionService conversionService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Test
    @WithMockUser(username = "user@example.com")
    void testConvert_Success() throws Exception {
        // Given
        ConversionRequest request = new ConversionRequest();
        request.setFrom("USD");
        request.setTo("EUR");
        request.setAmount(new BigDecimal("100.00"));
        ConversionResponse response = new ConversionResponse(
                "USD", "EUR", new BigDecimal("100.00"), new BigDecimal("90.00"),
                new BigDecimal("0.9"), new Timestamp(System.currentTimeMillis())
        );

        Mockito.when(conversionService.convert(anyString(), anyString(), any(BigDecimal.class), eq("user@example.com")))
                .thenReturn(response);

        // When / Then
        mockMvc.perform(post("/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromCurrency").value("USD"))
                .andExpect(jsonPath("$.toCurrency").value("EUR"))
                .andExpect(jsonPath("$.originalAmount").value(100.00))
                .andExpect(jsonPath("$.convertedAmount").value(90.00));
    }
}
