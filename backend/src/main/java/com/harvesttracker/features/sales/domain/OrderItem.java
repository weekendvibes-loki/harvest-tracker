package com.harvesttracker.features.sales.domain;

import com.harvesttracker.features.harvest.domain.HarvestRecord;
import com.harvesttracker.features.masterdata.domain.CropVariant;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "order_items", schema = "harvest_tracker")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "harvest_record_id")
    private HarvestRecord harvestRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fruit_type_id")
    private FruitType fruitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_variant_id")
    private CropVariant cropVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quantity_uom_id")
    private UnitOfMeasure quantityUom;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    public OrderItem() {
    }

    public OrderItem(Order order, BigDecimal quantity, BigDecimal unitPrice) {
        this.order = order;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        if (quantity != null && unitPrice != null) {
            this.lineTotal = quantity.multiply(unitPrice);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (lineTotal == null && quantity != null && unitPrice != null) {
            lineTotal = quantity.multiply(unitPrice);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
        if (quantity != null && unitPrice != null) {
            lineTotal = quantity.multiply(unitPrice);
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public HarvestRecord getHarvestRecord() {
        return harvestRecord;
    }

    public void setHarvestRecord(HarvestRecord harvestRecord) {
        this.harvestRecord = harvestRecord;
    }

    public FruitType getFruitType() {
        return fruitType;
    }

    public void setFruitType(FruitType fruitType) {
        this.fruitType = fruitType;
    }

    public CropVariant getCropVariant() {
        return cropVariant;
    }

    public void setCropVariant(CropVariant cropVariant) {
        this.cropVariant = cropVariant;
    }

    public UnitOfMeasure getQuantityUom() {
        return quantityUom;
    }

    public void setQuantityUom(UnitOfMeasure quantityUom) {
        this.quantityUom = quantityUom;
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

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public OffsetDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(OffsetDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
