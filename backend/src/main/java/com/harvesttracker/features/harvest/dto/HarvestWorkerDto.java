package com.harvesttracker.features.harvest.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class HarvestWorkerDto {

    private HarvestWorkerDto() {
    }

    public static class HarvestWorkerRequest {

        @NotNull(message = "Worker ID is required")
        private Long workerId;

        private String roleInHarvest;

        @DecimalMin(value = "0.0", message = "Hours worked must be at least 0")
        @DecimalMax(value = "24.0", message = "Hours worked cannot exceed 24")
        private BigDecimal hoursWorked;

        public Long getWorkerId() {
            return workerId;
        }

        public void setWorkerId(Long workerId) {
            this.workerId = workerId;
        }

        public String getRoleInHarvest() {
            return roleInHarvest;
        }

        public void setRoleInHarvest(String roleInHarvest) {
            this.roleInHarvest = roleInHarvest;
        }

        public BigDecimal getHoursWorked() {
            return hoursWorked;
        }

        public void setHoursWorked(BigDecimal hoursWorked) {
            this.hoursWorked = hoursWorked;
        }
    }

    public static class HarvestWorkerResponse {
        private Long id;
        private Long harvestRecordId;
        private Long workerId;
        private String workerName;
        private String roleInHarvest;
        private BigDecimal hoursWorked;
        private Boolean isActive;
        private OffsetDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getHarvestRecordId() {
            return harvestRecordId;
        }

        public void setHarvestRecordId(Long harvestRecordId) {
            this.harvestRecordId = harvestRecordId;
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

        public String getRoleInHarvest() {
            return roleInHarvest;
        }

        public void setRoleInHarvest(String roleInHarvest) {
            this.roleInHarvest = roleInHarvest;
        }

        public BigDecimal getHoursWorked() {
            return hoursWorked;
        }

        public void setHoursWorked(BigDecimal hoursWorked) {
            this.hoursWorked = hoursWorked;
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
