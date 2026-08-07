package com.harvesttracker.features.harvest.domain;

import com.harvesttracker.features.auth.domain.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "harvest_quality_checks", schema = "harvest_tracker")
public class HarvestQualityCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "harvest_record_id", nullable = false)
    private HarvestRecord harvestRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_by")
    private User checkedBy;

    @Column(name = "check_datetime", nullable = false)
    private OffsetDateTime checkDatetime;

    @Column(name = "quality_grade", nullable = false, length = 10)
    private String qualityGrade;

    @Column(name = "defect_percentage", precision = 5, scale = 2)
    private BigDecimal defectPercentage;

    @Column(name = "average_weight_grams", precision = 8, scale = 2)
    private BigDecimal averageWeightGrams;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved = false;

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

    public HarvestQualityCheck() {
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = OffsetDateTime.now();
        }
        if (checkDatetime == null) {
            checkDatetime = OffsetDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (isApproved == null) {
            isApproved = false;
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

    public HarvestRecord getHarvestRecord() {
        return harvestRecord;
    }

    public void setHarvestRecord(HarvestRecord harvestRecord) {
        this.harvestRecord = harvestRecord;
    }

    public User getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(User checkedBy) {
        this.checkedBy = checkedBy;
    }

    public OffsetDateTime getCheckDatetime() {
        return checkDatetime;
    }

    public void setCheckDatetime(OffsetDateTime checkDatetime) {
        this.checkDatetime = checkDatetime;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public BigDecimal getDefectPercentage() {
        return defectPercentage;
    }

    public void setDefectPercentage(BigDecimal defectPercentage) {
        this.defectPercentage = defectPercentage;
    }

    public BigDecimal getAverageWeightGrams() {
        return averageWeightGrams;
    }

    public void setAverageWeightGrams(BigDecimal averageWeightGrams) {
        this.averageWeightGrams = averageWeightGrams;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean approved) {
        isApproved = approved;
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
