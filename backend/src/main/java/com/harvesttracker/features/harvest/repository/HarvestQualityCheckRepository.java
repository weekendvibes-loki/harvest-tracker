package com.harvesttracker.features.harvest.repository;

import com.harvesttracker.features.harvest.domain.HarvestQualityCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HarvestQualityCheckRepository extends JpaRepository<HarvestQualityCheck, Long> {

    List<HarvestQualityCheck> findByHarvestRecordIdAndDeletedAtIsNull(Long harvestRecordId);
}
