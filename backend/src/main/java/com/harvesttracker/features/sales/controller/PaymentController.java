package com.harvesttracker.features.sales.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.sales.dto.PaymentDto;
import com.harvesttracker.features.sales.service.SalesService;
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

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Management", description = "Management of customer payment transactions, settlements, and partial payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final SalesService salesService;

    public PaymentController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated payments", description = "Retrieve list of payment transactions with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentDto.PaymentResponse>>> getAllPayments(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "invoiceId", required = false) Long invoiceId,
            @RequestParam(name = "paymentMethodId", required = false) Long paymentMethodId,
            @RequestParam(name = "paymentStatus", required = false) String paymentStatus,
            @RequestParam(name = "referenceNumber", required = false) String referenceNumber,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<PaymentDto.PaymentResponse> response = salesService.getAllPayments(
                page, size, sort, direction, invoiceId, paymentMethodId, paymentStatus, referenceNumber, startDate, endDate, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/invoices/{invoiceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Record payment for invoice", description = "Record a new payment transaction against a customer invoice (ADMIN only)")
    public ResponseEntity<ApiResponse<PaymentDto.PaymentResponse>> recordPayment(
            @PathVariable("invoiceId") Long invoiceId,
            @Valid @RequestBody PaymentDto.PaymentRequest request) {

        PaymentDto.PaymentResponse response = salesService.recordPaymentForInvoice(invoiceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded successfully", response));
    }
}
