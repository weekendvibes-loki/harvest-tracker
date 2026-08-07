package com.harvesttracker.features.worker.repository;

import com.harvesttracker.features.worker.domain.WorkerAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerAttendanceRepository extends JpaRepository<WorkerAttendance, Long>, JpaSpecificationExecutor<WorkerAttendance> {

    Optional<WorkerAttendance> findByIdAndDeletedAtIsNull(Long id);

    Optional<WorkerAttendance> findByWorkerIdAndAttendanceDateAndDeletedAtIsNull(Long workerId, LocalDate attendanceDate);

    boolean existsByWorkerIdAndAttendanceDateAndDeletedAtIsNull(Long workerId, LocalDate attendanceDate);

    boolean existsByWorkerIdAndAttendanceDateAndIdNotAndDeletedAtIsNull(Long workerId, LocalDate attendanceDate, Long id);

    List<WorkerAttendance> findByWorkerIdAndAttendanceDateBetweenAndIsPresentTrueAndDeletedAtIsNull(
            Long workerId, LocalDate startDate, LocalDate endDate);
}
