package com.harvesttracker.features.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderDto {

    private OrderDto() {
    }

    public static class OrderRequest {

        @NotNull(message = "Customer ID is required")
        private Long customerId;

        private LocalDate orderDate;

        @Pattern(regexp = "DRAFT|CONFIRMED|DISPATCHED|DELIVERED|INVOICED|PAID|CANCELLED", message = "Invalid order status")
        private String orderStatus = "DRAFT";

        private String notes;

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        private List<OrderItemDto.OrderItemRequest> items;

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public LocalDate getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(LocalDate orderDate) {
            this.orderDate = orderDate;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public List<OrderItemDto.OrderItemRequest> getItems() {
            return items;
        }

        public void setItems(List<OrderItemDto.OrderItemRequest> items) {
            this.items = items;
        }
    }

    public static class OrderResponse {
        private Long id;
        private Long customerId;
        private String customerName;
        private String customerPhone;
        private LocalDate orderDate;
        private String orderStatus;
        private BigDecimal totalAmount;
        private String notes;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<OrderItemDto.OrderItemResponse> items;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getCustomerId() {
            return customerId;
        }

        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustomerPhone() {
            return customerPhone;
        }

        public void setCustomerPhone(String customerPhone) {
            this.customerPhone = customerPhone;
        }

        public LocalDate getOrderDate() {
            return orderDate;
        }

        public void setOrderDate(LocalDate orderDate) {
            this.orderDate = orderDate;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean active) {
            isActive = active;
        }

        public OffsetDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public OffsetDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(OffsetDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }

        public List<OrderItemDto.OrderItemResponse> getItems() {
            return items;
        }

        public void setItems(List<OrderItemDto.OrderItemResponse> items) {
            this.items = items;
        }
    }
}
