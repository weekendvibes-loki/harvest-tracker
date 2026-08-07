package com.harvesttracker.features.sales.repository;

import com.harvesttracker.features.sales.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    Optional<Invoice> findByIdAndDeletedAtIsNull(Long id);

    Optional<Invoice> findByOrderIdAndDeletedAtIsNull(Long orderId);

    boolean existsByInvoiceNumberAndDeletedAtIsNull(String invoiceNumber);

    boolean existsByOrderIdAndDeletedAtIsNull(Long orderId);
}
