package com.harvesttracker.features.farm.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class SeasonDto {

    private SeasonDto() {
    }

    public static class SeasonRequest {

        @NotNull(message = "Farm ID is required")
        private Long farmId;

        @NotNull(message = "Fruit type ID is required")
        private Long fruitTypeId;

        @NotBlank(message = "Season name is required")
        @Size(min = 2, max = 150, message = "Season name must be between 2 and 150 characters")
        private String name;

        @NotNull(message = "Year is required")
        @Min(value = 2000, message = "Year must be between 2000 and 2100")
        @Max(value = 2100, message = "Year must be between 2000 and 2100")
        private Integer year;

        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        private LocalDate endDate;

        @Pattern(regexp = "PLANNED|ACTIVE|CLOSED", message = "Status must be PLANNED, ACTIVE, or CLOSED")
        private String status = "PLANNED";

        private String notes;

        public Long getFarmId() {
            return farmId;
        }

        public void setFarmId(Long farmId) {
            this.farmId = farmId;
        }

        public Long getFruitTypeId() {
            return fruitTypeId;
        }

        public void setFruitTypeId(Long fruitTypeId) {
            this.fruitTypeId = fruitTypeId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class SeasonResponse {
        private Long id;
        private Long farmId;
        private String farmName;
        private Long fruitTypeId;
        private String fruitTypeName;
        private String name;
        private Integer year;
        private LocalDate startDate;
        private LocalDate endDate;
        private String status;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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
