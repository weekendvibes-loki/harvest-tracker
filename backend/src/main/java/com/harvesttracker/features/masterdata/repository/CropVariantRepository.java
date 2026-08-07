package com.harvesttracker.features.masterdata.repository;

import com.harvesttracker.features.masterdata.domain.CropVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CropVariantRepository extends JpaRepository<CropVariant, Long>, JpaSpecificationExecutor<CropVariant> {

    Optional<CropVariant> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByFruitTypeIdAndNameIgnoreCaseAndDeletedAtIsNull(Long fruitTypeId, String name);

    boolean existsByFruitTypeIdAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(Long fruitTypeId, String name, Long id);

    boolean existsByFruitTypeIdAndCodeIgnoreCaseAndDeletedAtIsNull(Long fruitTypeId, String code);

    boolean existsByFruitTypeIdAndCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(Long fruitTypeId, String code, Long id);
}
