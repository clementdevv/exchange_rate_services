package com.anvil_shield.main_service.entity;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;
public class ConversionEntityTest {
    @Test
    void testAllArgsConstructorAndGetters() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ConversionEntity entity = new ConversionEntity(
                1L, "USD", "EUR", new BigDecimal("100.00"),
                new BigDecimal("92.50"), new BigDecimal("0.9250"), now
        );

        assertEquals(1L, entity.getId());
        assertEquals("USD", entity.getFromCurrency());
        assertEquals("EUR", entity.getToCurrency());
        assertEquals(new BigDecimal("100.00"), entity.getOriginalAmount());
        assertEquals(new BigDecimal("92.50"), entity.getConvertedAmount());
        assertEquals(new BigDecimal("0.9250"), entity.getRate());
        assertEquals(now, entity.getTimestamp());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        ConversionEntity entity = new ConversionEntity();
        entity.setId(2L);
        entity.setFromCurrency("GBP");
        entity.setToCurrency("USD");
        entity.setOriginalAmount(new BigDecimal("50"));
        entity.setConvertedAmount(new BigDecimal("62.5"));
        entity.setRate(new BigDecimal("1.25"));
        Timestamp now = new Timestamp(System.currentTimeMillis());
        entity.setTimestamp(now);

        assertEquals(2L, entity.getId());
        assertEquals("GBP", entity.getFromCurrency());
        assertEquals("USD", entity.getToCurrency());
        assertEquals(new BigDecimal("50"), entity.getOriginalAmount());
        assertEquals(new BigDecimal("62.5"), entity.getConvertedAmount());
        assertEquals(new BigDecimal("1.25"), entity.getRate());
        assertEquals(now, entity.getTimestamp());
    }
}
