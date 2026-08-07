package com.harvesttracker.features.farm.repository;

import com.harvesttracker.features.farm.domain.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long>, JpaSpecificationExecutor<Season> {

    Optional<Season> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByFarmIdAndFruitTypeIdAndYearAndNameIgnoreCaseAndDeletedAtIsNull(
            Long farmId, Long fruitTypeId, Integer year, String name);

    boolean existsByFarmIdAndFruitTypeIdAndYearAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(
            Long farmId, Long fruitTypeId, Integer year, String name, Long id);
}
