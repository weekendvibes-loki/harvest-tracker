package com.harvesttracker.features.sales.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class InvoiceDto {

    private InvoiceDto() {
    }

    public static class InvoiceRequest {

        @NotNull(message = "Order ID is required")
        private Long orderId;

        @NotNull(message = "Due date is required")
        private LocalDate dueDate;

        @Pattern(regexp = "DRAFT|ISSUED|PARTIALLY_PAID|PAID|OVERDUE|CANCELLED", message = "Invalid invoice status")
        private String invoiceStatus = "DRAFT";

        private String notes;

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public String getInvoiceStatus() {
            return invoiceStatus;
        }

        public void setInvoiceStatus(String invoiceStatus) {
            this.invoiceStatus = invoiceStatus;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class InvoiceResponse {
        private Long id;
        private Long orderId;
        private Long customerId;
        private String customerName;
        private String invoiceNumber;
        private OffsetDateTime issuedAt;
        private LocalDate dueDate;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private String invoiceStatus;
        private String notes;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<PaymentDto.PaymentResponse> payments;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
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

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public OffsetDateTime getIssuedAt() {
            return issuedAt;
        }

        public void setIssuedAt(OffsetDateTime issuedAt) {
            this.issuedAt = issuedAt;
        }

        public LocalDate getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(BigDecimal paidAmount) {
            this.paidAmount = paidAmount;
        }

        public String getInvoiceStatus() {
            return invoiceStatus;
        }

        public void setInvoiceStatus(String invoiceStatus) {
            this.invoiceStatus = invoiceStatus;
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

        public List<PaymentDto.PaymentResponse> getPayments() {
            return payments;
        }

        public void setPayments(List<PaymentDto.PaymentResponse> payments) {
            this.payments = payments;
        }
    }
}
