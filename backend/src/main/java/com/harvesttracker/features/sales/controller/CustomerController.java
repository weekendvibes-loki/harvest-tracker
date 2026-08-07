package com.harvesttracker.features.sales.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.sales.dto.CustomerDto;
import com.harvesttracker.features.sales.service.SalesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Management", description = "Management of buyer profiles, contacts, and customer classifications")
@SecurityRequirement(name = "bearerAuth")
public class CustomerController {

    private final SalesService salesService;

    public CustomerController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated customers", description = "Retrieve list of customer profiles with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<CustomerDto.CustomerResponse>>> getAllCustomers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "customerType", required = false) String customerType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<CustomerDto.CustomerResponse> response = salesService.getAllCustomers(
                page, size, sort, direction, search, name, phone, email, customerType, status, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get customer by ID", description = "Retrieve detailed customer profile by ID")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponse>> getCustomerById(@PathVariable("id") Long id) {
        CustomerDto.CustomerResponse response = salesService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create customer profile", description = "Create a new buyer customer profile (ADMIN only)")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponse>> createCustomer(
            @Valid @RequestBody CustomerDto.CustomerRequest request) {

        CustomerDto.CustomerResponse response = salesService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Customer created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update customer profile", description = "Update details of an existing customer profile (ADMIN only)")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponse>> updateCustomer(
            @PathVariable("id") Long id,
            @Valid @RequestBody CustomerDto.CustomerRequest request) {

        CustomerDto.CustomerResponse response = salesService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete customer", description = "Soft delete a customer profile (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable("id") Long id) {
        salesService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
    }
}
