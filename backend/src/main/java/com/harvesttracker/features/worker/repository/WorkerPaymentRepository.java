package com.harvesttracker.features.worker.repository;

import com.harvesttracker.features.worker.domain.WorkerPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerPaymentRepository extends JpaRepository<WorkerPayment, Long>, JpaSpecificationExecutor<WorkerPayment> {

    Optional<WorkerPayment> findByIdAndDeletedAtIsNull(Long id);

    List<WorkerPayment> findByWorkerIdAndDeletedAtIsNull(Long workerId);
}
