package com.harvesttracker.features.masterdata.repository;

import com.harvesttracker.features.masterdata.domain.FruitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FruitTypeRepository extends JpaRepository<FruitType, Long>, JpaSpecificationExecutor<FruitType> {

    Optional<FruitType> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    boolean existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(String name, Long id);

    boolean existsByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    boolean existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(String code, Long id);
}
