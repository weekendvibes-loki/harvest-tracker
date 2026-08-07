package com.harvesttracker.features.sales.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.sales.dto.InvoiceDto;
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
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoice Management", description = "Management of customer invoices, billing records, and payment tracking")
@SecurityRequirement(name = "bearerAuth")
public class InvoiceController {

    private final SalesService salesService;

    public InvoiceController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated invoices", description = "Retrieve list of customer invoices with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<InvoiceDto.InvoiceResponse>>> getAllInvoices(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "customerId", required = false) Long customerId,
            @RequestParam(name = "orderId", required = false) Long orderId,
            @RequestParam(name = "invoiceNumber", required = false) String invoiceNumber,
            @RequestParam(name = "invoiceStatus", required = false) String invoiceStatus,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<InvoiceDto.InvoiceResponse> response = salesService.getAllInvoices(
                page, size, sort, direction, customerId, orderId, invoiceNumber, invoiceStatus, startDate, endDate, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get invoice by ID", description = "Retrieve detailed invoice record by ID including payment history")
    public ResponseEntity<ApiResponse<InvoiceDto.InvoiceResponse>> getInvoiceById(@PathVariable("id") Long id) {
        InvoiceDto.InvoiceResponse response = salesService.getInvoiceById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Generate invoice for order", description = "Generate a billing invoice for a confirmed sales order (ADMIN only)")
    public ResponseEntity<ApiResponse<InvoiceDto.InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceDto.InvoiceRequest request) {

        InvoiceDto.InvoiceResponse response = salesService.createInvoiceForOrder(request.getOrderId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Invoice generated successfully", response));
    }
}
