package com.harvesttracker.features.farm.repository;

import com.harvesttracker.features.farm.domain.FarmDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmDocumentRepository extends JpaRepository<FarmDocument, Long> {

    List<FarmDocument> findByFarmIdAndDeletedAtIsNull(Long farmId);
}
