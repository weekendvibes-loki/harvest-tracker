package com.harvesttracker.features.harvest.domain;

import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.masterdata.domain.CropVariant;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "harvest_records", schema = "harvest_tracker")
public class HarvestRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fruit_type_id", nullable = false)
    private FruitType fruitType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crop_variant_id")
    private CropVariant cropVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quantity_uom_id")
    private UnitOfMeasure quantityUom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate;

    @Column(name = "harvest_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal harvestQuantity;

    @Column(name = "quality_grade", nullable = false, length = 10)
    private String qualityGrade = "B";

    @Column(name = "storage_location", length = 200)
    private String storageLocation;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "DRAFT";

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

    @OneToMany(mappedBy = "harvestRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HarvestWorker> harvestWorkers = new ArrayList<>();

    @OneToMany(mappedBy = "harvestRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HarvestQualityCheck> qualityChecks = new ArrayList<>();

    public HarvestRecord() {
    }

    public HarvestRecord(Farm farm, Season season, FruitType fruitType, LocalDate harvestDate, BigDecimal harvestQuantity) {
        this.farm = farm;
        this.season = season;
        this.fruitType = fruitType;
        this.harvestDate = harvestDate;
        this.harvestQuantity = harvestQuantity;
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
        if (qualityGrade == null) {
            qualityGrade = "B";
        }
        if (status == null) {
            status = "DRAFT";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
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

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
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

    public List<HarvestWorker> getHarvestWorkers() {
        return harvestWorkers;
    }

    public void setHarvestWorkers(List<HarvestWorker> harvestWorkers) {
        this.harvestWorkers = harvestWorkers;
    }

    public List<HarvestQualityCheck> getQualityChecks() {
        return qualityChecks;
    }

    public void setQualityChecks(List<HarvestQualityCheck> qualityChecks) {
        this.qualityChecks = qualityChecks;
    }
}
