package com.harvesttracker.features.harvest.repository;

import com.harvesttracker.features.harvest.domain.HarvestWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HarvestWorkerRepository extends JpaRepository<HarvestWorker, Long> {

    List<HarvestWorker> findByHarvestRecordIdAndDeletedAtIsNull(Long harvestRecordId);

    Optional<HarvestWorker> findByHarvestRecordIdAndWorkerIdAndDeletedAtIsNull(Long harvestRecordId, Long workerId);

    boolean existsByHarvestRecordIdAndWorkerIdAndDeletedAtIsNull(Long harvestRecordId, Long workerId);
}
