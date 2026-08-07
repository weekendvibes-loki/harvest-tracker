package com.harvesttracker.features.farm.repository;

import com.harvesttracker.features.farm.domain.FarmFruitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmFruitTypeRepository extends JpaRepository<FarmFruitType, Long> {

    List<FarmFruitType> findByFarmIdAndDeletedAtIsNull(Long farmId);

    Optional<FarmFruitType> findByFarmIdAndFruitTypeIdAndDeletedAtIsNull(Long farmId, Long fruitTypeId);

    boolean existsByFarmIdAndFruitTypeIdAndDeletedAtIsNull(Long farmId, Long fruitTypeId);
}
