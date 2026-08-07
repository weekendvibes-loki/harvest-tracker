package com.harvesttracker.features.sales.repository;

import com.harvesttracker.features.sales.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByIdAndDeletedAtIsNull(Long id);

    List<Payment> findByInvoiceIdAndDeletedAtIsNull(Long invoiceId);
}
