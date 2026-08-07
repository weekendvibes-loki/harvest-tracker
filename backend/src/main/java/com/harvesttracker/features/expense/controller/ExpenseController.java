package com.harvesttracker.features.expense.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.expense.dto.*;
import com.harvesttracker.features.expense.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenses")
@Tag(name = "Expense Management", description = "Management of operational expenses, farm costs, harvest expenses, and category breakdowns")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated operational expenses", description = "Retrieve list of expenses with filtering by farm, harvest, category, status, date range, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseDto.ExpenseResponse>>> getAllExpenses(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "harvestRecordId", required = false) Long harvestRecordId,
            @RequestParam(name = "expenseCategoryId", required = false) Long expenseCategoryId,
            @RequestParam(name = "paymentMethodId", required = false) Long paymentMethodId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<ExpenseDto.ExpenseResponse> response = expenseService.getAllExpenses(
                page, size, sort, direction, farmId, harvestRecordId, expenseCategoryId, paymentMethodId, status, startDate, endDate, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get expense by ID", description = "Retrieve detailed operational expense record by ID")
    public ResponseEntity<ApiResponse<ExpenseDto.ExpenseResponse>> getExpenseById(@PathVariable("id") Long id) {
        ExpenseDto.ExpenseResponse response = expenseService.getExpenseById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create operational expense", description = "Record a new operational, harvest, or farm expense (ADMIN only)")
    public ResponseEntity<ApiResponse<ExpenseDto.ExpenseResponse>> createExpense(
            @Valid @RequestBody ExpenseDto.ExpenseRequest request) {

        ExpenseDto.ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Expense recorded successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update operational expense", description = "Update details of an existing operational expense record (ADMIN only)")
    public ResponseEntity<ApiResponse<ExpenseDto.ExpenseResponse>> updateExpense(
            @PathVariable("id") Long id,
            @Valid @RequestBody ExpenseDto.ExpenseRequest request) {

        ExpenseDto.ExpenseResponse response = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(ApiResponse.success("Expense updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update expense status", description = "Transition expense status (RECORDED -> APPROVED / REJECTED) (ADMIN only)")
    public ResponseEntity<ApiResponse<ExpenseDto.ExpenseResponse>> updateExpenseStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {

        ExpenseDto.ExpenseResponse response = expenseService.updateExpenseStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Expense status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete operational expense", description = "Soft delete an operational expense record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable("id") Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(ApiResponse.success("Expense deleted successfully", null));
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get expense summary", description = "Retrieve summary metrics of operational expenses across farms and categories")
    public ResponseEntity<ApiResponse<ExpenseSummaryDto.ExpenseSummaryResponse>> getExpenseSummary(
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ExpenseSummaryDto.ExpenseSummaryResponse summary = expenseService.getExpenseSummary(farmId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/monthly")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get monthly expense analytics", description = "Retrieve monthly expense trends and totals for a given year")
    public ResponseEntity<ApiResponse<List<MonthlyExpenseDto.MonthlyExpenseResponse>>> getMonthlyExpenses(
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "farmId", required = false) Long farmId) {

        List<MonthlyExpenseDto.MonthlyExpenseResponse> response = expenseService.getMonthlyExpenses(year, farmId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/by-category")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get expense breakdown by category", description = "Retrieve expense distribution and percentage breakdown across expense categories")
    public ResponseEntity<ApiResponse<List<CategoryExpenseDto.CategoryExpenseResponse>>> getExpensesByCategory(
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<CategoryExpenseDto.CategoryExpenseResponse> response = expenseService.getExpensesByCategory(farmId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
