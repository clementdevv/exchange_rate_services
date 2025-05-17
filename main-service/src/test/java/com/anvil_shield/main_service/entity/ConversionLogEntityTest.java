package com.anvil_shield.main_service.entity;

import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

class ConversionLogEntityTest {

    @Test
    void testSettersAndGetters() {
        ConversionLogEntity log = new ConversionLogEntity();

        Timestamp now = new Timestamp(System.currentTimeMillis());

        log.setId(1L);
        log.setConversionId(100L);
        log.setMessage("Conversion successful");
        log.setPerformedBy("admin@example.com");
        log.setTimestamp(now);

        assertEquals(1L, log.getId());
        assertEquals(100L, log.getConversionId());
        assertEquals("Conversion successful", log.getMessage());
        assertEquals("admin@example.com", log.getPerformedBy());
        assertEquals(now, log.getTimestamp());
    }
}

