package com.harvesttracker.features.masterdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public class UnitOfMeasureDto {

    public static class UnitOfMeasureRequest {

        @NotBlank(message = "Unit of measure name is required")
        @Size(max = 50, message = "Name must not exceed 50 characters")
        private String name;

        @NotBlank(message = "Unit of measure code is required")
        @Size(max = 10, message = "Code must not exceed 10 characters")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must be uppercase alphanumeric with optional underscores and no spaces")
        private String code;

        @NotBlank(message = "Measure type is required")
        @Pattern(regexp = "^(WEIGHT|AREA|VOLUME|COUNT|TIME)$", message = "Measure type must be one of: WEIGHT, AREA, VOLUME, COUNT, TIME")
        private String measureType;

        private Integer sortOrder = 0;

        private Boolean isActive = true;

        public UnitOfMeasureRequest() {
        }

        public UnitOfMeasureRequest(String name, String code, String measureType, Integer sortOrder, Boolean isActive) {
            this.name = name;
            this.code = code;
            this.measureType = measureType;
            this.sortOrder = sortOrder;
            this.isActive = isActive;
        }

        public String getName() {
            return name != null ? name.trim() : null;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code != null ? code.trim().toUpperCase() : null;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMeasureType() {
            return measureType != null ? measureType.trim().toUpperCase() : null;
        }

        public void setMeasureType(String measureType) {
            this.measureType = measureType;
        }

        public Integer getSortOrder() {
            return sortOrder != null ? sortOrder : 0;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public Boolean getIsActive() {
            return isActive != null ? isActive : true;
        }

        public void setIsActive(Boolean active) {
            isActive = active;
        }
    }

    public static class UnitOfMeasureResponse {
        private Long id;
        private String name;
        private String code;
        private String measureType;
        private Integer sortOrder;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public UnitOfMeasureResponse() {
        }

        public UnitOfMeasureResponse(Long id, String name, String code, String measureType, Integer sortOrder, Boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.measureType = measureType;
            this.sortOrder = sortOrder;
            this.isActive = isActive;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMeasureType() {
            return measureType;
        }

        public void setMeasureType(String measureType) {
            this.measureType = measureType;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
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
