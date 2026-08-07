package com.harvesttracker.features.masterdata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public class FruitTypeDto {

    public static class FruitTypeRequest {

        @NotBlank(message = "Fruit type name is required")
        @Size(max = 100, message = "Name must not exceed 100 characters")
        private String name;

        @NotBlank(message = "Fruit type code is required")
        @Size(max = 20, message = "Code must not exceed 20 characters")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must be uppercase alphanumeric with optional underscores and no spaces")
        private String code;

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        private String description;

        @Pattern(regexp = "^(SUMMER|MONSOON|WINTER|YEAR_ROUND)$", message = "Season type must be one of: SUMMER, MONSOON, WINTER, YEAR_ROUND")
        private String seasonType;

        private Integer sortOrder = 0;

        private Boolean isActive = true;

        public FruitTypeRequest() {
        }

        public FruitTypeRequest(String name, String code, String description, String seasonType, Integer sortOrder, Boolean isActive) {
            this.name = name;
            this.code = code;
            this.description = description;
            this.seasonType = seasonType;
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

        public String getDescription() {
            return description != null ? description.trim() : null;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSeasonType() {
            return seasonType != null ? seasonType.trim().toUpperCase() : null;
        }

        public void setSeasonType(String seasonType) {
            this.seasonType = seasonType;
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

    public static class FruitTypeResponse {
        private Long id;
        private String name;
        private String code;
        private String description;
        private String seasonType;
        private Integer sortOrder;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public FruitTypeResponse() {
        }

        public FruitTypeResponse(Long id, String name, String code, String description, String seasonType, Integer sortOrder, Boolean isActive, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
            this.id = id;
            this.name = name;
            this.code = code;
            this.description = description;
            this.seasonType = seasonType;
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

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getSeasonType() {
            return seasonType;
        }

        public void setSeasonType(String seasonType) {
            this.seasonType = seasonType;
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
