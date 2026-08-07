package com.harvesttracker.features.masterdata.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.ExpenseCategoryDto;
import com.harvesttracker.features.masterdata.service.ExpenseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expense-categories")
@Tag(name = "Master Data - Expense Categories", description = "Management of operational expense classifications")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseCategoryController {

    private final ExpenseCategoryService expenseCategoryService;

    public ExpenseCategoryController(ExpenseCategoryService expenseCategoryService) {
        this.expenseCategoryService = expenseCategoryService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated expense categories", description = "Retrieve list of expense categories with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ExpenseCategoryDto.ExpenseCategoryResponse>>> getAllExpenseCategories(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "sortOrder") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<ExpenseCategoryDto.ExpenseCategoryResponse> pagedResponse =
                expenseCategoryService.getAllExpenseCategories(page, size, sort, direction, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get expense category by ID", description = "Retrieve a specific expense category by its unique identifier")
    public ResponseEntity<ApiResponse<ExpenseCategoryDto.ExpenseCategoryResponse>> getExpenseCategoryById(@PathVariable("id") Long id) {
        ExpenseCategoryDto.ExpenseCategoryResponse response = expenseCategoryService.getExpenseCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new expense category", description = "Create a new expense category record (ADMIN only)")
    public ResponseEntity<ApiResponse<ExpenseCategoryDto.ExpenseCategoryResponse>> createExpenseCategory(
            @Valid @RequestBody ExpenseCategoryDto.ExpenseCategoryRequest request) {

        ExpenseCategoryDto.ExpenseCategoryResponse response = expenseCategoryService.createExpenseCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Expense category created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing expense category", description = "Update details of an existing expense category (ADMIN only)")
    public ResponseEntity<ApiResponse<ExpenseCategoryDto.ExpenseCategoryResponse>> updateExpenseCategory(
            @PathVariable("id") Long id,
            @Valid @RequestBody ExpenseCategoryDto.ExpenseCategoryRequest request) {

        ExpenseCategoryDto.ExpenseCategoryResponse response = expenseCategoryService.updateExpenseCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Expense category updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle expense category active status", description = "Activate or deactivate an expense category (ADMIN only)")
    public ResponseEntity<ApiResponse<ExpenseCategoryDto.ExpenseCategoryResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        ExpenseCategoryDto.ExpenseCategoryResponse response = expenseCategoryService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Expense category status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete expense category", description = "Soft delete an expense category record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteExpenseCategory(@PathVariable("id") Long id) {
        expenseCategoryService.deleteExpenseCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Expense category deleted successfully", null));
    }
}
