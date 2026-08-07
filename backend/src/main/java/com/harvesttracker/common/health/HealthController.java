package com.harvesttracker.common.health;

import com.harvesttracker.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "System Health and Environment Diagnostic API")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Check backend application status")
    public ApiResponse<Map<String, String>> health() {
        return ApiResponse.success(Map.of(
                "status", "UP",
                "service", "harvest-tracker",
                "version", "0.1.0"
        ));
    }
}
