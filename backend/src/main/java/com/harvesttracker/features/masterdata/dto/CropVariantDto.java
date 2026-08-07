package com.harvesttracker.features.masterdata.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public class CropVariantDto {

    public static class CropVariantRequest {

        @NotNull(message = "Fruit type ID is required")
        private Long fruitTypeId;

        @NotBlank(message = "Crop variant name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        private String name;

        @NotBlank(message = "Crop variant code is required")
        @Size(max = 30, message = "Code must not exceed 30 characters")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must be uppercase alphanumeric with optional underscores and no spaces")
        private String code;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        private Boolean isActive = true;

        public CropVariantRequest() {
        }

        public CropVariantRequest(Long fruitTypeId, String name, String code, String description, Boolean isActive) {
            this.fruitTypeId = fruitTypeId;
            this.name = name;
            this.code = code;
            this.description = description;
            this.isActive = isActive;
        }

        public Long getFruitTypeId() {
            return fruitTypeId;
        }

        public void setFruitTypeId(Long fruitTypeId) {
            this.fruitTypeId = fruitTypeId;
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

        public String getDescription() {
            return description != null ? description.trim() : null;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getIsActive() {
            return isActive != null ? isActive : true;
        }

        public void setIsActive(Boolean active) {
            isActive = active;
        }
    }

    public static class CropVariantResponse {
        private Long id;
        private Long fruitTypeId;
        private String fruitTypeName;
        private String fruitTypeCode;
        private String name;
        private String code;
        private String description;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public CropVariantResponse() {
        }

        public CropVariantResponse(Long id, Long fruitTypeId, String fruitTypeName, String fruitTypeCode, String name, String code, String description, Boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
            this.id = id;
            this.fruitTypeId = fruitTypeId;
            this.fruitTypeName = fruitTypeName;
            this.fruitTypeCode = fruitTypeCode;
            this.name = name;
            this.code = code;
            this.description = description;
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

        public Long getFruitTypeId() {
            return fruitTypeId;
        }

        public void setFruitTypeId(Long fruitTypeId) {
            this.fruitTypeId = fruitTypeId;
        }

        public String getFruitTypeName() {
            return fruitTypeName;
        }

        public void setFruitTypeName(String fruitTypeName) {
            this.fruitTypeName = fruitTypeName;
        }

        public String getFruitTypeCode() {
            return fruitTypeCode;
        }

        public void setFruitTypeCode(String fruitTypeCode) {
            this.fruitTypeCode = fruitTypeCode;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
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
