package com.anvil_shield.main_service.io;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConversionRequest {

    @NotBlank(message = "Source currency 'from' is required")
    private String from;

    @NotBlank(message = "Target currency 'to' is required")
    private String to;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
    private BigDecimal amount;
}