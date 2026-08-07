package com.harvesttracker.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        ErrorResponse error,
        String correlationId,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", data, null, getCorrelationId(), Instant.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, getCorrelationId(), Instant.now());
    }

    public static <T> ApiResponse<T> error(ErrorResponse errorResponse) {
        return new ApiResponse<>(false, errorResponse.message(), null, errorResponse, getCorrelationId(), Instant.now());
    }

    private static String getCorrelationId() {
        return MDC.get("correlationId");
    }
}
