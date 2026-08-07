package com.harvesttracker.features.worker.repository;

import com.harvesttracker.features.worker.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long>, JpaSpecificationExecutor<Worker> {

    Optional<Worker> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByPhoneAndDeletedAtIsNull(String phone);

    boolean existsByPhoneAndIdNotAndDeletedAtIsNull(String phone, Long id);
}
