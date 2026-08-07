package com.harvesttracker.features.masterdata.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.PaymentMethodDto;
import com.harvesttracker.features.masterdata.service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment-methods")
@Tag(name = "Master Data - Payment Methods", description = "Management of supported payment methods")
@SecurityRequirement(name = "bearerAuth")
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    public PaymentMethodController(PaymentMethodService paymentMethodService) {
        this.paymentMethodService = paymentMethodService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated payment methods", description = "Retrieve list of payment methods with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentMethodDto.PaymentMethodResponse>>> getAllPaymentMethods(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "sortOrder") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<PaymentMethodDto.PaymentMethodResponse> pagedResponse =
                paymentMethodService.getAllPaymentMethods(page, size, sort, direction, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get payment method by ID", description = "Retrieve a specific payment method by its unique identifier")
    public ResponseEntity<ApiResponse<PaymentMethodDto.PaymentMethodResponse>> getPaymentMethodById(@PathVariable("id") Long id) {
        PaymentMethodDto.PaymentMethodResponse response = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new payment method", description = "Create a new payment method record (ADMIN only)")
    public ResponseEntity<ApiResponse<PaymentMethodDto.PaymentMethodResponse>> createPaymentMethod(
            @Valid @RequestBody PaymentMethodDto.PaymentMethodRequest request) {

        PaymentMethodDto.PaymentMethodResponse response = paymentMethodService.createPaymentMethod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment method created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing payment method", description = "Update details of an existing payment method (ADMIN only)")
    public ResponseEntity<ApiResponse<PaymentMethodDto.PaymentMethodResponse>> updatePaymentMethod(
            @PathVariable("id") Long id,
            @Valid @RequestBody PaymentMethodDto.PaymentMethodRequest request) {

        PaymentMethodDto.PaymentMethodResponse response = paymentMethodService.updatePaymentMethod(id, request);
        return ResponseEntity.ok(ApiResponse.success("Payment method updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle payment method active status", description = "Activate or deactivate a payment method (ADMIN only)")
    public ResponseEntity<ApiResponse<PaymentMethodDto.PaymentMethodResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        PaymentMethodDto.PaymentMethodResponse response = paymentMethodService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Payment method status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete payment method", description = "Soft delete a payment method record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deletePaymentMethod(@PathVariable("id") Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.ok(ApiResponse.success("Payment method deleted successfully", null));
    }
}
