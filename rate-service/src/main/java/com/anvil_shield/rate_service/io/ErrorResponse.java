package com.anvil_shield.rate_service.io;

import lombok.Data;

@Data
public class ErrorResponse {
    private boolean success;
    private ApiError error;

    @Data
    public static class ApiError {
        private int code;
        private String info;
    }
}