package com.harvesttracker.features.sales.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.sales.dto.OrderDto;
import com.harvesttracker.features.sales.dto.OrderItemDto;
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
@RequestMapping("/api/v1/orders")
@Tag(name = "Sales Order Management", description = "Management of sales orders, order items, stock checks, and status transitions")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final SalesService salesService;

    public OrderController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated sales orders", description = "Retrieve list of sales orders with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDto.OrderResponse>>> getAllOrders(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "customerId", required = false) Long customerId,
            @RequestParam(name = "orderStatus", required = false) String orderStatus,
            @RequestParam(name = "cropVariantId", required = false) Long cropVariantId,
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<OrderDto.OrderResponse> response = salesService.getAllOrders(
                page, size, sort, direction, customerId, orderStatus, cropVariantId, farmId, startDate, endDate, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get sales order by ID", description = "Retrieve detailed sales order information including line items")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponse>> getOrderById(@PathVariable("id") Long id) {
        OrderDto.OrderResponse response = salesService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create sales order", description = "Create a new sales order with line items (ADMIN only)")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponse>> createOrder(
            @Valid @RequestBody OrderDto.OrderRequest request) {

        OrderDto.OrderResponse response = salesService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Sales order created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update sales order", description = "Update an existing sales order and its line items (ADMIN only)")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponse>> updateOrder(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderDto.OrderRequest request) {

        OrderDto.OrderResponse response = salesService.updateOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success("Sales order updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update sales order status", description = "Transition order status (DRAFT -> CONFIRMED -> INVOICED -> PAID / CANCELLED) (ADMIN only)")
    public ResponseEntity<ApiResponse<OrderDto.OrderResponse>> updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {

        OrderDto.OrderResponse response = salesService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete sales order", description = "Soft delete a sales order record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable("id") Long id) {
        salesService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Sales order deleted successfully", null));
    }

    // --- Line Item Operations ---

    @PostMapping("/{id}/items")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add line item to order", description = "Add a new line item to an existing sales order (ADMIN only)")
    public ResponseEntity<ApiResponse<OrderItemDto.OrderItemResponse>> addOrderItem(
            @PathVariable("id") Long id,
            @Valid @RequestBody OrderItemDto.OrderItemRequest request) {

        OrderItemDto.OrderItemResponse response = salesService.addOrderItem(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Order item added successfully", response));
    }

    @PutMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update line item in order", description = "Update an existing line item in a sales order (ADMIN only)")
    public ResponseEntity<ApiResponse<OrderItemDto.OrderItemResponse>> updateOrderItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody OrderItemDto.OrderItemRequest request) {

        OrderItemDto.OrderItemResponse response = salesService.updateOrderItem(id, itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Order item updated successfully", response));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove line item from order", description = "Remove a line item from a sales order (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteOrderItem(
            @PathVariable("id") Long id,
            @PathVariable("itemId") Long itemId) {

        salesService.deleteOrderItem(id, itemId);
        return ResponseEntity.ok(ApiResponse.success("Order item removed successfully", null));
    }
}
