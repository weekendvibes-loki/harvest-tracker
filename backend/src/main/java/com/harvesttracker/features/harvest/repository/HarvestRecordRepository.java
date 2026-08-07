package com.harvesttracker.features.harvest.repository;

import com.harvesttracker.features.harvest.domain.HarvestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface HarvestRecordRepository extends JpaRepository<HarvestRecord, Long>, JpaSpecificationExecutor<HarvestRecord> {

    Optional<HarvestRecord> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT COUNT(h) FROM HarvestRecord h WHERE h.deletedAt IS NULL")
    long countActiveHarvests();

    @Query("SELECT SUM(h.harvestQuantity) FROM HarvestRecord h WHERE h.deletedAt IS NULL")
    BigDecimal sumTotalHarvestQuantity();
}
