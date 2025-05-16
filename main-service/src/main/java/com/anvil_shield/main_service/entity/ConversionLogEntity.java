package com.anvil_shield.main_service.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class ConversionLogEntity {
    private Long id;
    private Long conversionId;
    private String message;
    private String performedBy;
    private Timestamp timestamp;
}
