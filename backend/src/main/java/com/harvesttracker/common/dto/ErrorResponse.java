package com.harvesttracker.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String correlationId,
        List<FieldErrorDetail> fieldErrors
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path, getCorrelationId(), null);
    }

    public static ErrorResponse of(int status, String error, String message, String path, List<FieldErrorDetail> fieldErrors) {
        return new ErrorResponse(Instant.now(), status, error, message, path, getCorrelationId(), fieldErrors);
    }

    private static String getCorrelationId() {
        return MDC.get("correlationId");
    }

    public record FieldErrorDetail(
            String field,
            String rejectedValue,
            String message
    ) {}
}
