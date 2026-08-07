package com.harvesttracker.features.expense.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.features.expense.dto.ProfitLossDto;
import com.harvesttracker.features.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/financial")
@Tag(name = "Financial Analytics", description = "Financial performance metrics, Profit & Loss analysis, and revenue vs cost tracking")
@SecurityRequirement(name = "bearerAuth")
public class FinancialController {

    private final ExpenseService expenseService;

    public FinancialController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/profit-loss")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get Profit & Loss statement", description = "Calculate revenue, total expenses, direct operational costs, gross profit, net profit, and profit margin percentage")
    public ResponseEntity<ApiResponse<ProfitLossDto.ProfitLossResponse>> getProfitLoss(
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "farmId", required = false) Long farmId) {

        ProfitLossDto.ProfitLossResponse response = expenseService.getFinancialProfitLoss(startDate, endDate, farmId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
