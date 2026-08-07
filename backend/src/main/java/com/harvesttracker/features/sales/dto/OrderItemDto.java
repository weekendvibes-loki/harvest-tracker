package com.harvesttracker.features.sales.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrderItemDto {

    private OrderItemDto() {
    }

    public static class OrderItemRequest {

        private Long harvestRecordId;
        private Long fruitTypeId;
        private Long cropVariantId;
        private Long quantityUomId;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.001", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.00", message = "Unit price must be non-negative")
        private BigDecimal unitPrice;

        private String notes;

        public Long getHarvestRecordId() {
            return harvestRecordId;
        }

        public void setHarvestRecordId(Long harvestRecordId) {
            this.harvestRecordId = harvestRecordId;
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

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public static class OrderItemResponse {
        private Long id;
        private Long orderId;
        private Long harvestRecordId;
        private Long fruitTypeId;
        private String fruitTypeName;
        private Long cropVariantId;
        private String cropVariantName;
        private Long quantityUomId;
        private String quantityUomCode;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private String notes;
        private Boolean isActive;
        private OffsetDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public Long getHarvestRecordId() {
            return harvestRecordId;
        }

        public void setHarvestRecordId(Long harvestRecordId) {
            this.harvestRecordId = harvestRecordId;
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

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getLineTotal() {
            return lineTotal;
        }

        public void setLineTotal(BigDecimal lineTotal) {
            this.lineTotal = lineTotal;
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
    }
}
