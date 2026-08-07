package com.harvesttracker.features.farm.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class FarmFruitTypeDto {

    private FarmFruitTypeDto() {
    }

    public static class FarmFruitTypeRequest {

        @NotNull(message = "Fruit type ID is required")
        private Long fruitTypeId;

        private Boolean isPrimary = false;
        private LocalDate firstPlantedDate;

        public Long getFruitTypeId() {
            return fruitTypeId;
        }

        public void setFruitTypeId(Long fruitTypeId) {
            this.fruitTypeId = fruitTypeId;
        }

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean primary) {
            isPrimary = primary;
        }

        public LocalDate getFirstPlantedDate() {
            return firstPlantedDate;
        }

        public void setFirstPlantedDate(LocalDate firstPlantedDate) {
            this.firstPlantedDate = firstPlantedDate;
        }
    }

    public static class FarmFruitTypeResponse {
        private Long id;
        private Long farmId;
        private Long fruitTypeId;
        private String fruitTypeName;
        private String fruitTypeCode;
        private Boolean isPrimary;
        private LocalDate firstPlantedDate;
        private Boolean isActive;
        private OffsetDateTime createdAt;

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

        public Boolean getIsPrimary() {
            return isPrimary;
        }

        public void setIsPrimary(Boolean primary) {
            isPrimary = primary;
        }

        public LocalDate getFirstPlantedDate() {
            return firstPlantedDate;
        }

        public void setFirstPlantedDate(LocalDate firstPlantedDate) {
            this.firstPlantedDate = firstPlantedDate;
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
