package com.harvesttracker.features.sales.repository;

import com.harvesttracker.features.sales.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByPhoneAndDeletedAtIsNull(String phone);

    boolean existsByPhoneAndIdNotAndDeletedAtIsNull(String phone, Long id);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmailAndIdNotAndDeletedAtIsNull(String email, Long id);
}
