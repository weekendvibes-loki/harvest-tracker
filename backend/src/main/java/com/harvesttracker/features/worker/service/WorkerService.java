package com.harvesttracker.features.worker.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.worker.dto.WorkerAttendanceDto;
import com.harvesttracker.features.worker.dto.WorkerDto;
import com.harvesttracker.features.worker.dto.WorkerPaymentDto;

import java.util.List;

public interface WorkerService {

    PagedResponse<WorkerDto.WorkerResponse> getAllWorkers(
            int page, int size, String sort, String direction,
            String search, String name, String phone, String village,
            Long workerTypeId, Long farmId, String status, Boolean isActive);

    WorkerDto.WorkerResponse getWorkerById(Long id);

    WorkerDto.WorkerResponse createWorker(WorkerDto.WorkerRequest request);

    WorkerDto.WorkerResponse updateWorker(Long id, WorkerDto.WorkerRequest request);

    WorkerDto.WorkerResponse toggleStatus(Long id, boolean isActive);

    void deleteWorker(Long id);

    List<WorkerAttendanceDto.AttendanceResponse> getWorkerAttendance(Long workerId);

    WorkerAttendanceDto.AttendanceResponse recordAttendance(Long workerId, WorkerAttendanceDto.AttendanceRequest request);

    WorkerAttendanceDto.AttendanceResponse updateAttendance(Long workerId, Long attendanceId, WorkerAttendanceDto.AttendanceRequest request);

    void deleteAttendance(Long workerId, Long attendanceId);

    List<WorkerPaymentDto.PaymentResponse> getWorkerPayments(Long workerId);

    WorkerPaymentDto.PaymentResponse recordPayment(Long workerId, WorkerPaymentDto.PaymentRequest request);
}
