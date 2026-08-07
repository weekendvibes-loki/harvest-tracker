package com.harvesttracker.features.farm.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public class FarmDto {

    private FarmDto() {
    }

    public static class FarmRequest {

        @NotNull(message = "Owner ID is required")
        private Long ownerId;

        private Long landUomId;

        @NotBlank(message = "Farm name is required")
        @Size(min = 3, max = 100, message = "Farm name must be between 3 and 100 characters")
        private String name;

        @Pattern(regexp = "OWNED|LEASED", message = "Ownership type must be OWNED or LEASED")
        private String ownershipType = "OWNED";

        @DecimalMin(value = "0.001", message = "Land size must be greater than 0")
        private BigDecimal landSize;

        @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
        private BigDecimal gpsLatitude;

        @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
        private BigDecimal gpsLongitude;

        private String address;

        @Pattern(regexp = "ACTIVE|INACTIVE|ARCHIVED", message = "Status must be ACTIVE, INACTIVE, or ARCHIVED")
        private String status = "ACTIVE";

        private LocalDate leaseStartDate;
        private LocalDate leaseEndDate;
        private String lessorName;
        private String notes;

        public Long getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(Long ownerId) {
            this.ownerId = ownerId;
        }

        public Long getLandUomId() {
            return landUomId;
        }

        public void setLandUomId(Long landUomId) {
            this.landUomId = landUomId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwnershipType() {
            return ownershipType;
        }

        public void setOwnershipType(String ownershipType) {
            this.ownershipType = ownershipType;
        }

        public BigDecimal getLandSize() {
            return landSize;
        }

        public void setLandSize(BigDecimal landSize) {
            this.landSize = landSize;
        }

        public BigDecimal getGpsLatitude() {
            return gpsLatitude;
        }

        public void setGpsLatitude(BigDecimal gpsLatitude) {
            this.gpsLatitude = gpsLatitude;
        }

        public BigDecimal getGpsLongitude() {
            return gpsLongitude;
        }

        public void setGpsLongitude(BigDecimal gpsLongitude) {
            this.gpsLongitude = gpsLongitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDate getLeaseStartDate() {
            return leaseStartDate;
        }

        public void setLeaseStartDate(LocalDate leaseStartDate) {
            this.leaseStartDate = leaseStartDate;
        }

        public LocalDate getLeaseEndDate() {
            return leaseEndDate;
        }

        public void setLeaseEndDate(LocalDate leaseEndDate) {
            this.leaseEndDate = leaseEndDate;
        }

        public String getLessorName() {
            return lessorName;
        }

        public void setLessorName(String lessorName) {
            this.lessorName = lessorName;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class FarmResponse {
        private Long id;
        private Long ownerId;
        private String ownerName;
        private Long landUomId;
        private String landUomCode;
        private String name;
        private String ownershipType;
        private BigDecimal landSize;
        private BigDecimal gpsLatitude;
        private BigDecimal gpsLongitude;
        private String address;
        private String status;
        private LocalDate leaseStartDate;
        private LocalDate leaseEndDate;
        private String lessorName;
        private String notes;
        private Boolean isActive;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;
        private List<FarmFruitTypeDto.FarmFruitTypeResponse> fruitTypes;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(Long ownerId) {
            this.ownerId = ownerId;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public Long getLandUomId() {
            return landUomId;
        }

        public void setLandUomId(Long landUomId) {
            this.landUomId = landUomId;
        }

        public String getLandUomCode() {
            return landUomCode;
        }

        public void setLandUomCode(String landUomCode) {
            this.landUomCode = landUomCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOwnershipType() {
            return ownershipType;
        }

        public void setOwnershipType(String ownershipType) {
            this.ownershipType = ownershipType;
        }

        public BigDecimal getLandSize() {
            return landSize;
        }

        public void setLandSize(BigDecimal landSize) {
            this.landSize = landSize;
        }

        public BigDecimal getGpsLatitude() {
            return gpsLatitude;
        }

        public void setGpsLatitude(BigDecimal gpsLatitude) {
            this.gpsLatitude = gpsLatitude;
        }

        public BigDecimal getGpsLongitude() {
            return gpsLongitude;
        }

        public void setGpsLongitude(BigDecimal gpsLongitude) {
            this.gpsLongitude = gpsLongitude;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDate getLeaseStartDate() {
            return leaseStartDate;
        }

        public void setLeaseStartDate(LocalDate leaseStartDate) {
            this.leaseStartDate = leaseStartDate;
        }

        public LocalDate getLeaseEndDate() {
            return leaseEndDate;
        }

        public void setLeaseEndDate(LocalDate leaseEndDate) {
            this.leaseEndDate = leaseEndDate;
        }

        public String getLessorName() {
            return lessorName;
        }

        public void setLessorName(String lessorName) {
            this.lessorName = lessorName;
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

        public List<FarmFruitTypeDto.FarmFruitTypeResponse> getFruitTypes() {
            return fruitTypes;
        }

        public void setFruitTypes(List<FarmFruitTypeDto.FarmFruitTypeResponse> fruitTypes) {
            this.fruitTypes = fruitTypes;
        }
    }
}
