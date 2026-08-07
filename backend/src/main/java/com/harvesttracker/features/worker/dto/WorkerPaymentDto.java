package com.harvesttracker.features.worker.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class WorkerPaymentDto {

    private WorkerPaymentDto() {
    }

    public static class PaymentRequest {

        private Long paymentMethodId;

        @NotNull(message = "Period start date is required")
        private LocalDate periodStart;

        @NotNull(message = "Period end date is required")
        private LocalDate periodEnd;

        @Min(value = 0, message = "Total days worked must be at least 0")
        private Integer totalDaysWorked = 0;

        @NotNull(message = "Daily wage rate is required")
        @DecimalMin(value = "0.01", message = "Daily wage rate must be greater than 0")
        private BigDecimal dailyWageRate;

        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
        private BigDecimal amount;

        @Pattern(regexp = "PENDING|PAID|CANCELLED", message = "Status must be PENDING, PAID, or CANCELLED")
        private String paymentStatus = "PENDING";

        private LocalDate paidDate;
        private String notes;

        public Long getPaymentMethodId() {
            return paymentMethodId;
        }

        public void setPaymentMethodId(Long paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
        }

        public LocalDate getPeriodStart() {
            return periodStart;
        }

        public void setPeriodStart(LocalDate periodStart) {
            this.periodStart = periodStart;
        }

        public LocalDate getPeriodEnd() {
            return periodEnd;
        }

        public void setPeriodEnd(LocalDate periodEnd) {
            this.periodEnd = periodEnd;
        }

        public Integer getTotalDaysWorked() {
            return totalDaysWorked;
        }

        public void setTotalDaysWorked(Integer totalDaysWorked) {
            this.totalDaysWorked = totalDaysWorked;
        }

        public BigDecimal getDailyWageRate() {
            return dailyWageRate;
        }

        public void setDailyWageRate(BigDecimal dailyWageRate) {
            this.dailyWageRate = dailyWageRate;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public LocalDate getPaidDate() {
            return paidDate;
        }

        public void setPaidDate(LocalDate paidDate) {
            this.paidDate = paidDate;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class PaymentResponse {
        private Long id;
        private Long workerId;
        private String workerName;
        private Long paymentMethodId;
        private String paymentMethodName;
        private LocalDate periodStart;
        private LocalDate periodEnd;
        private Integer totalDaysWorked;
        private BigDecimal dailyWageRate;
        private BigDecimal amount;
        private String paymentStatus;
        private LocalDate paidDate;
        private String notes;
        private Boolean isActive;
        private OffsetDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getWorkerId() {
            return workerId;
        }

        public void setWorkerId(Long workerId) {
            this.workerId = workerId;
        }

        public String getWorkerName() {
            return workerName;
        }

        public void setWorkerName(String workerName) {
            this.workerName = workerName;
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

        public LocalDate getPeriodStart() {
            return periodStart;
        }

        public void setPeriodStart(LocalDate periodStart) {
            this.periodStart = periodStart;
        }

        public LocalDate getPeriodEnd() {
            return periodEnd;
        }

        public void setPeriodEnd(LocalDate periodEnd) {
            this.periodEnd = periodEnd;
        }

        public Integer getTotalDaysWorked() {
            return totalDaysWorked;
        }

        public void setTotalDaysWorked(Integer totalDaysWorked) {
            this.totalDaysWorked = totalDaysWorked;
        }

        public BigDecimal getDailyWageRate() {
            return dailyWageRate;
        }

        public void setDailyWageRate(BigDecimal dailyWageRate) {
            this.dailyWageRate = dailyWageRate;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public LocalDate getPaidDate() {
            return paidDate;
        }

        public void setPaidDate(LocalDate paidDate) {
            this.paidDate = paidDate;
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
    }
}
