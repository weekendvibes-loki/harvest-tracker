package com.harvesttracker.features.expense.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class ExpenseDto {

    private ExpenseDto() {
    }

    public static class ExpenseRequest {

        @NotNull(message = "Farm ID is required")
        private Long farmId;

        private Long harvestRecordId;

        @NotNull(message = "Expense category ID is required")
        private Long expenseCategoryId;

        private Long paymentMethodId;

        @NotNull(message = "Expense date is required")
        private LocalDate expenseDate;

        @NotNull(message = "Expense amount is required")
        @DecimalMin(value = "0.01", message = "Expense amount must be greater than 0")
        private BigDecimal amount;

        @Pattern(regexp = "RECORDED|APPROVED|REJECTED", message = "Status must be RECORDED, APPROVED, or REJECTED")
        private String status = "RECORDED";

        private String description;
        private String notes;

        public Long getFarmId() {
            return farmId;
        }

        public void setFarmId(Long farmId) {
            this.farmId = farmId;
        }

        public Long getHarvestRecordId() {
            return harvestRecordId;
        }

        public void setHarvestRecordId(Long harvestRecordId) {
            this.harvestRecordId = harvestRecordId;
        }

        public Long getExpenseCategoryId() {
            return expenseCategoryId;
        }

        public void setExpenseCategoryId(Long expenseCategoryId) {
            this.expenseCategoryId = expenseCategoryId;
        }

        public Long getPaymentMethodId() {
            return paymentMethodId;
        }

        public void setPaymentMethodId(Long paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }

        public LocalDate getExpenseDate() {
            return expenseDate;
        }

        public void setExpenseDate(LocalDate expenseDate) {
            this.expenseDate = expenseDate;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class ExpenseResponse {
        private Long id;
        private Long farmId;
        private String farmName;
        private Long harvestRecordId;
        private Long expenseCategoryId;
        private String expenseCategoryName;
        private String expenseCategoryCode;
        private Long paymentMethodId;
        private String paymentMethodName;
        private LocalDate expenseDate;
        private BigDecimal amount;
        private String status;
        private String description;
        private String notes;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getFarmId() {
            return farmId;
        }

        public void setFarmId(Long farmId) {
            this.farmId = farmId;
        }

        public String getFarmName() {
            return farmName;
        }

        public void setFarmName(String farmName) {
            this.farmName = farmName;
        }

        public Long getHarvestRecordId() {
            return harvestRecordId;
        }

        public void setHarvestRecordId(Long harvestRecordId) {
            this.harvestRecordId = harvestRecordId;
        }

        public Long getExpenseCategoryId() {
            return expenseCategoryId;
        }

        public void setExpenseCategoryId(Long expenseCategoryId) {
            this.expenseCategoryId = expenseCategoryId;
        }

        public String getExpenseCategoryName() {
            return expenseCategoryName;
        }

        public void setExpenseCategoryName(String expenseCategoryName) {
            this.expenseCategoryName = expenseCategoryName;
        }

        public String getExpenseCategoryCode() {
            return expenseCategoryCode;
        }

        public void setExpenseCategoryCode(String expenseCategoryCode) {
            this.expenseCategoryCode = expenseCategoryCode;
        }

        public Long getPaymentMethodId() {
            return paymentMethodId;
        }

        public void setPaymentMethodId(Long paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }

        public String getPaymentMethodName() {
            return paymentMethodName;
        }

        public void setPaymentMethodName(String paymentMethodName) {
            this.paymentMethodName = paymentMethodName;
        }

        public LocalDate getExpenseDate() {
            return expenseDate;
        }

        public void setExpenseDate(LocalDate expenseDate) {
            this.expenseDate = expenseDate;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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
    }
}
