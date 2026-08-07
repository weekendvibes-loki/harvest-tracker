package com.harvesttracker.features.worker.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class WorkerDto {

    private WorkerDto() {
    }

    public static class WorkerRequest {

        @NotNull(message = "Farm ID is required")
        private Long farmId;

        private Long workerTypeId;
        private Long wageUomId;

        @NotBlank(message = "Worker name is required")
        @Size(min = 2, max = 200, message = "Worker name must be between 2 and 200 characters")
        private String name;

        private String phone;
        private String idCardNumber;

        @NotNull(message = "Daily wage rate is required")
        @DecimalMin(value = "0.01", message = "Daily wage rate must be greater than 0")
        private BigDecimal dailyWageRate;

        @Pattern(regexp = "ACTIVE|INACTIVE", message = "Status must be ACTIVE or INACTIVE")
        private String status = "ACTIVE";

        private LocalDate joiningDate;
        private String address;
        private String notes;

        public Long getFarmId() {
            return farmId;
        }

        public void setFarmId(Long farmId) {
            this.farmId = farmId;
        }

        public Long getWorkerTypeId() {
            return workerTypeId;
        }

        public void setWorkerTypeId(Long workerTypeId) {
            this.workerTypeId = workerTypeId;
        }

        public Long getWageUomId() {
            return wageUomId;
        }

        public void setWageUomId(Long wageUomId) {
            this.wageUomId = wageUomId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getIdCardNumber() {
            return idCardNumber;
        }

        public void setIdCardNumber(String idCardNumber) {
            this.idCardNumber = idCardNumber;
        }

        public BigDecimal getDailyWageRate() {
            return dailyWageRate;
        }

        public void setDailyWageRate(BigDecimal dailyWageRate) {
            this.dailyWageRate = dailyWageRate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDate getJoiningDate() {
            return joiningDate;
        }

        public void setJoiningDate(LocalDate joiningDate) {
            this.joiningDate = joiningDate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class WorkerResponse {
        private Long id;
        private Long farmId;
        private String farmName;
        private Long workerTypeId;
        private String workerTypeName;
        private Long wageUomId;
        private String wageUomCode;
        private String name;
        private String phone;
        private String idCardNumber;
        private BigDecimal dailyWageRate;
        private String status;
        private LocalDate joiningDate;
        private String address;
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

        public Long getWorkerTypeId() {
            return workerTypeId;
        }

        public void setWorkerTypeId(Long workerTypeId) {
            this.workerTypeId = workerTypeId;
        }

        public String getWorkerTypeName() {
            return workerTypeName;
        }

        public void setWorkerTypeName(String workerTypeName) {
            this.workerTypeName = workerTypeName;
        }

        public Long getWageUomId() {
            return wageUomId;
        }

        public void setWageUomId(Long wageUomId) {
            this.wageUomId = wageUomId;
        }

        public String getWageUomCode() {
            return wageUomCode;
        }

        public void setWageUomCode(String wageUomCode) {
            this.wageUomCode = wageUomCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getIdCardNumber() {
            return idCardNumber;
        }

        public void setIdCardNumber(String idCardNumber) {
            this.idCardNumber = idCardNumber;
        }

        public BigDecimal getDailyWageRate() {
            return dailyWageRate;
        }

        public void setDailyWageRate(BigDecimal dailyWageRate) {
            this.dailyWageRate = dailyWageRate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDate getJoiningDate() {
            return joiningDate;
        }

        public void setJoiningDate(LocalDate joiningDate) {
            this.joiningDate = joiningDate;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
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
