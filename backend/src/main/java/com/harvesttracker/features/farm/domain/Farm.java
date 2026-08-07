package com.harvesttracker.features.farm.domain;

import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farms", schema = "harvest_tracker")
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "land_uom_id")
    private UnitOfMeasure landUom;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "ownership_type", nullable = false, length = 20)
    private String ownershipType = "OWNED";

    @Column(name = "land_size", precision = 10, scale = 3)
    private BigDecimal landSize;

    @Column(name = "gps_latitude", precision = 10, scale = 7)
    private BigDecimal gpsLatitude;

    @Column(name = "gps_longitude", precision = 10, scale = 7)
    private BigDecimal gpsLongitude;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "lease_start_date")
    private LocalDate leaseStartDate;

    @Column(name = "lease_end_date")
    private LocalDate leaseEndDate;

    @Column(name = "lessor_name", length = 200)
    private String lessorName;

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

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FarmFruitType> farmFruitTypes = new ArrayList<>();

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FarmDocument> farmDocuments = new ArrayList<>();

    public Farm() {
    }

    public Farm(User owner, String name, String ownershipType, BigDecimal landSize) {
        this.owner = owner;
        this.name = name;
        this.ownershipType = ownershipType != null ? ownershipType : "OWNED";
        this.landSize = landSize;
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
        if (ownershipType == null) {
            ownershipType = "OWNED";
        }
        if (status == null) {
            status = "ACTIVE";
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public UnitOfMeasure getLandUom() {
        return landUom;
    }

    public void setLandUom(UnitOfMeasure landUom) {
        this.landUom = landUom;
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

    public List<FarmFruitType> getFarmFruitTypes() {
        return farmFruitTypes;
    }

    public void setFarmFruitTypes(List<FarmFruitType> farmFruitTypes) {
        this.farmFruitTypes = farmFruitTypes;
    }

    public List<FarmDocument> getFarmDocuments() {
        return farmDocuments;
    }

    public void setFarmDocuments(List<FarmDocument> farmDocuments) {
        this.farmDocuments = farmDocuments;
    }
}
