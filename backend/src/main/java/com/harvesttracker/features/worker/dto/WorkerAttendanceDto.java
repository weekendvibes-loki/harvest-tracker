package com.harvesttracker.features.worker.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class WorkerAttendanceDto {

    private WorkerAttendanceDto() {
    }

    public static class AttendanceRequest {

        @NotNull(message = "Attendance date is required")
        private LocalDate attendanceDate;

        @NotNull(message = "Is present status is required")
        private Boolean isPresent = true;

        private Long harvestRecordId;

        @DecimalMin(value = "0.0", message = "Hours worked must be at least 0")
        @DecimalMax(value = "24.0", message = "Hours worked cannot exceed 24")
        private BigDecimal hoursWorked;

        private String remarks;

        public LocalDate getAttendanceDate() {
            return attendanceDate;
        }

        public void setAttendanceDate(LocalDate attendanceDate) {
            this.attendanceDate = attendanceDate;
        }

        public Boolean getIsPresent() {
            return isPresent;
        }

        public void setIsPresent(Boolean present) {
            isPresent = present;
        }

        public Long getHarvestRecordId() {
            return harvestRecordId;
        }

        public void setHarvestRecordId(Long harvestRecordId) {
            this.harvestRecordId = harvestRecordId;
        }

        public BigDecimal getHoursWorked() {
            return hoursWorked;
        }

        public void setHoursWorked(BigDecimal hoursWorked) {
            this.hoursWorked = hoursWorked;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }
    }

    public static class AttendanceResponse {
        private Long id;
        private Long workerId;
        private String workerName;
        private Long harvestRecordId;
        private LocalDate attendanceDate;
        private Boolean isPresent;
        private BigDecimal hoursWorked;
        private String remarks;
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

        public Long getHarvestRecordId() {
            return harvestRecordId;
        }

        public void setHarvestRecordId(Long harvestRecordId) {
            this.harvestRecordId = harvestRecordId;
        }

        public LocalDate getAttendanceDate() {
            return attendanceDate;
        }

        public void setAttendanceDate(LocalDate attendanceDate) {
            this.attendanceDate = attendanceDate;
        }

        public Boolean getIsPresent() {
            return isPresent;
        }

        public void setIsPresent(Boolean present) {
            isPresent = present;
        }

        public BigDecimal getHoursWorked() {
            return hoursWorked;
        }

        public void setHoursWorked(BigDecimal hoursWorked) {
            this.hoursWorked = hoursWorked;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
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
