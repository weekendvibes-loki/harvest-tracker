package com.harvesttracker.features.masterdata.repository;

import com.harvesttracker.features.masterdata.domain.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long>, JpaSpecificationExecutor<PaymentMethod> {

    Optional<PaymentMethod> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

    boolean existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(String name, Long id);

    boolean existsByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    boolean existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(String code, Long id);
}
