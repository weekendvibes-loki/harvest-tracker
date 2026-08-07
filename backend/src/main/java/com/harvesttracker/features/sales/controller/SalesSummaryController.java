package com.harvesttracker.features.sales.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.features.sales.dto.RevenueSummaryDto;
import com.harvesttracker.features.sales.dto.SalesSummaryDto;
import com.harvesttracker.features.sales.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales")
@Tag(name = "Sales Analytics & Summaries", description = "Summary statistics for sales orders, customer volume, and revenue collection")
@SecurityRequirement(name = "bearerAuth")
public class SalesSummaryController {

    private final SalesService salesService;

    public SalesSummaryController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get sales order summary", description = "Retrieve aggregate summary metrics for customers and sales orders")
    public ResponseEntity<ApiResponse<SalesSummaryDto.SalesSummaryResponse>> getSalesSummary() {
        SalesSummaryDto.SalesSummaryResponse summary = salesService.getSalesSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/revenue")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get revenue summary", description = "Retrieve aggregate revenue collection metrics, invoice totals, and pending balances")
    public ResponseEntity<ApiResponse<RevenueSummaryDto.RevenueSummaryResponse>> getRevenueSummary() {
        RevenueSummaryDto.RevenueSummaryResponse summary = salesService.getRevenueSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}
