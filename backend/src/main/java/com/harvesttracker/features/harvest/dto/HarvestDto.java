package com.harvesttracker.features.harvest.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class HarvestDto {

    private HarvestDto() {
    }

    public static class HarvestRequest {

        @NotNull(message = "Farm ID is required")
        private Long farmId;

        @NotNull(message = "Season ID is required")
        private Long seasonId;

        @NotNull(message = "Fruit type ID is required")
        private Long fruitTypeId;

        private Long cropVariantId;
        private Long quantityUomId;
        private Long supervisorId;

        @NotNull(message = "Harvest date is required")
        private LocalDate harvestDate;

        @NotNull(message = "Harvest quantity is required")
        @DecimalMin(value = "0.001", message = "Harvest quantity must be greater than 0")
        private BigDecimal harvestQuantity;

        @Pattern(regexp = "A|B|C|REJECT", message = "Quality grade must be A, B, C, or REJECT")
        private String qualityGrade = "B";

        private String storageLocation;

        @Pattern(regexp = "DRAFT|CONFIRMED|STORED|SOLD", message = "Status must be DRAFT, CONFIRMED, STORED, or SOLD")
        private String status = "DRAFT";

        private String notes;

        public Long getFarmId() {
            return farmId;
        }

        public void setFarmId(Long farmId) {
            this.farmId = farmId;
        }

        public Long getSeasonId() {
            return seasonId;
        }

        public void setSeasonId(Long seasonId) {
            this.seasonId = seasonId;
        }

        public Long getFruitTypeId() {
            return fruitTypeId;
        }

        public void setFruitTypeId(Long fruitTypeId) {
            this.fruitTypeId = fruitTypeId;
        }

        public Long getCropVariantId() {
            return cropVariantId;
        }

        public void setCropVariantId(Long cropVariantId) {
            this.cropVariantId = cropVariantId;
        }

        public Long getQuantityUomId() {
            return quantityUomId;
        }

        public void setQuantityUomId(Long quantityUomId) {
            this.quantityUomId = quantityUomId;
        }

        public Long getSupervisorId() {
            return supervisorId;
        }

        public void setSupervisorId(Long supervisorId) {
            this.supervisorId = supervisorId;
        }

        public LocalDate getHarvestDate() {
            return harvestDate;
        }

        public void setHarvestDate(LocalDate harvestDate) {
            this.harvestDate = harvestDate;
        }

        public BigDecimal getHarvestQuantity() {
            return harvestQuantity;
        }

        public void setHarvestQuantity(BigDecimal harvestQuantity) {
            this.harvestQuantity = harvestQuantity;
        }

        public String getQualityGrade() {
            return qualityGrade;
        }

        public void setQualityGrade(String qualityGrade) {
            this.qualityGrade = qualityGrade;
        }

        public String getStorageLocation() {
            return storageLocation;
        }

        public void setStorageLocation(String storageLocation) {
            this.storageLocation = storageLocation;
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

    public static class HarvestResponse {
        private Long id;
        private Long farmId;
        private String farmName;
        private Long seasonId;
        private String seasonName;
        private Long fruitTypeId;
        private String fruitTypeName;
        private Long cropVariantId;
        private String cropVariantName;
        private Long quantityUomId;
        private String quantityUomCode;
        private Long supervisorId;
        private String supervisorName;
        private LocalDate harvestDate;
        private BigDecimal harvestQuantity;
        private String qualityGrade;
        private String storageLocation;
        private String status;
        private String notes;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<HarvestWorkerDto.HarvestWorkerResponse> workers;

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

        public Long getSeasonId() {
            return seasonId;
        }

        public void setSeasonId(Long seasonId) {
            this.seasonId = seasonId;
        }

        public String getSeasonName() {
            return seasonName;
        }

        public void setSeasonName(String seasonName) {
            this.seasonName = seasonName;
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

        public Long getCropVariantId() {
            return cropVariantId;
        }

        public void setCropVariantId(Long cropVariantId) {
            this.cropVariantId = cropVariantId;
        }

        public String getCropVariantName() {
            return cropVariantName;
        }

        public void setCropVariantName(String cropVariantName) {
            this.cropVariantName = cropVariantName;
        }

        public Long getQuantityUomId() {
            return quantityUomId;
        }

        public void setQuantityUomId(Long quantityUomId) {
            this.quantityUomId = quantityUomId;
        }

        public String getQuantityUomCode() {
            return quantityUomCode;
        }

        public void setQuantityUomCode(String quantityUomCode) {
            this.quantityUomCode = quantityUomCode;
        }

        public Long getSupervisorId() {
            return supervisorId;
        }

        public void setSupervisorId(Long supervisorId) {
            this.supervisorId = supervisorId;
        }

        public String getSupervisorName() {
            return supervisorName;
        }

        public void setSupervisorName(String supervisorName) {
            this.supervisorName = supervisorName;
        }

        public LocalDate getHarvestDate() {
            return harvestDate;
        }

        public void setHarvestDate(LocalDate harvestDate) {
            this.harvestDate = harvestDate;
        }

        public BigDecimal getHarvestQuantity() {
            return harvestQuantity;
        }

        public void setHarvestQuantity(BigDecimal harvestQuantity) {
            this.harvestQuantity = harvestQuantity;
        }

        public String getQualityGrade() {
            return qualityGrade;
        }

        public void setQualityGrade(String qualityGrade) {
            this.qualityGrade = qualityGrade;
        }

        public String getStorageLocation() {
            return storageLocation;
        }

        public void setStorageLocation(String storageLocation) {
            this.storageLocation = storageLocation;
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

        public List<HarvestWorkerDto.HarvestWorkerResponse> getWorkers() {
            return workers;
        }

        public void setWorkers(List<HarvestWorkerDto.HarvestWorkerResponse> workers) {
            this.workers = workers;
        }
    }
}
