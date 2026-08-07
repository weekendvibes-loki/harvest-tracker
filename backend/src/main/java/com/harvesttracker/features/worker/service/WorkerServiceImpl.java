package com.harvesttracker.features.worker.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.masterdata.domain.PaymentMethod;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import com.harvesttracker.features.masterdata.domain.WorkerType;
import com.harvesttracker.features.masterdata.repository.PaymentMethodRepository;
import com.harvesttracker.features.masterdata.repository.UnitOfMeasureRepository;
import com.harvesttracker.features.masterdata.repository.WorkerTypeRepository;
import com.harvesttracker.features.worker.domain.Worker;
import com.harvesttracker.features.worker.domain.WorkerAttendance;
import com.harvesttracker.features.worker.domain.WorkerPayment;
import com.harvesttracker.features.worker.dto.WorkerAttendanceDto;
import com.harvesttracker.features.worker.dto.WorkerDto;
import com.harvesttracker.features.worker.dto.WorkerPaymentDto;
import com.harvesttracker.features.worker.mapper.WorkerAttendanceMapper;
import com.harvesttracker.features.worker.mapper.WorkerMapper;
import com.harvesttracker.features.worker.mapper.WorkerPaymentMapper;
import com.harvesttracker.features.worker.repository.WorkerAttendanceRepository;
import com.harvesttracker.features.worker.repository.WorkerPaymentRepository;
import com.harvesttracker.features.worker.repository.WorkerRepository;
import com.harvesttracker.features.worker.specification.WorkerSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkerServiceImpl implements WorkerService {

    private final WorkerRepository workerRepository;
    private final WorkerAttendanceRepository attendanceRepository;
    private final WorkerPaymentRepository paymentRepository;
    private final FarmRepository farmRepository;
    private final WorkerTypeRepository workerTypeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final WorkerMapper workerMapper;
    private final WorkerAttendanceMapper attendanceMapper;
    private final WorkerPaymentMapper paymentMapper;

    public WorkerServiceImpl(
            WorkerRepository workerRepository,
            WorkerAttendanceRepository attendanceRepository,
            WorkerPaymentRepository paymentRepository,
            FarmRepository farmRepository,
            WorkerTypeRepository workerTypeRepository,
            UnitOfMeasureRepository unitOfMeasureRepository,
            PaymentMethodRepository paymentMethodRepository,
            WorkerMapper workerMapper,
            WorkerAttendanceMapper attendanceMapper,
            WorkerPaymentMapper paymentMapper) {
        this.workerRepository = workerRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
        this.farmRepository = farmRepository;
        this.workerTypeRepository = workerTypeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.workerMapper = workerMapper;
        this.attendanceMapper = attendanceMapper;
        this.paymentMapper = paymentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<WorkerDto.WorkerResponse> getAllWorkers(
            int page, int size, String sort, String direction,
            String search, String name, String phone, String village,
            Long workerTypeId, Long farmId, String status, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Worker> spec = WorkerSpecification.filterWorkers(
                search, name, phone, village, workerTypeId, farmId, status, isActive);

        Page<Worker> workerPage = workerRepository.findAll(spec, pageable);
        Page<WorkerDto.WorkerResponse> dtoPage = workerPage.map(workerMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), workerPage);
    }

    @Override
    @Transactional(readOnly = true)
    public WorkerDto.WorkerResponse getWorkerById(Long id) {
        Worker worker = workerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + id));
        return workerMapper.toResponse(worker);
    }

    @Override
    public WorkerDto.WorkerResponse createWorker(WorkerDto.WorkerRequest request) {
        validateWorkerRequest(request, null);

        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(request.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + request.getFarmId()));

        WorkerType workerType = null;
        if (request.getWorkerTypeId() != null) {
            workerType = workerTypeRepository.findByIdAndDeletedAtIsNull(request.getWorkerTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Worker type not found with id: " + request.getWorkerTypeId()));
        }

        UnitOfMeasure wageUom = null;
        if (request.getWageUomId() != null) {
            wageUom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getWageUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Wage unit of measure not found with id: " + request.getWageUomId()));
        }

        Worker worker = new Worker();
        workerMapper.updateEntity(worker, request, farm, workerType, wageUom);

        Worker saved = workerRepository.save(worker);
        return workerMapper.toResponse(saved);
    }

    @Override
    public WorkerDto.WorkerResponse updateWorker(Long id, WorkerDto.WorkerRequest request) {
        Worker worker = workerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + id));

        validateWorkerRequest(request, id);

        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(request.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + request.getFarmId()));

        WorkerType workerType = null;
        if (request.getWorkerTypeId() != null) {
            workerType = workerTypeRepository.findByIdAndDeletedAtIsNull(request.getWorkerTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Worker type not found with id: " + request.getWorkerTypeId()));
        }

        UnitOfMeasure wageUom = null;
        if (request.getWageUomId() != null) {
            wageUom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getWageUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Wage unit of measure not found with id: " + request.getWageUomId()));
        }

        workerMapper.updateEntity(worker, request, farm, workerType, wageUom);

        Worker updated = workerRepository.save(worker);
        return workerMapper.toResponse(updated);
    }

    @Override
    public WorkerDto.WorkerResponse toggleStatus(Long id, boolean isActive) {
        Worker worker = workerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + id));

        worker.setIsActive(isActive);
        worker.setStatus(isActive ? "ACTIVE" : "INACTIVE");
        Worker updated = workerRepository.save(worker);
        return workerMapper.toResponse(updated);
    }

    @Override
    public void deleteWorker(Long id) {
        Worker worker = workerRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + id));

        worker.setDeletedAt(OffsetDateTime.now());
        worker.setIsActive(false);
        worker.setStatus("INACTIVE");
        workerRepository.save(worker);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerAttendanceDto.AttendanceResponse> getWorkerAttendance(Long workerId) {
        if (!workerRepository.existsById(workerId)) {
            throw new ResourceNotFoundException("Worker not found with id: " + workerId);
        }
        return attendanceRepository.findAll((root, query, cb) -> cb.and(
                        cb.equal(root.get("worker").get("id"), workerId),
                        cb.isNull(root.get("deletedAt"))
                )).stream()
                .map(attendanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkerAttendanceDto.AttendanceResponse recordAttendance(Long workerId, WorkerAttendanceDto.AttendanceRequest request) {
        Worker worker = workerRepository.findByIdAndDeletedAtIsNull(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + workerId));

        if (!worker.getIsActive() || "INACTIVE".equalsIgnoreCase(worker.getStatus())) {
            throw new IllegalArgumentException("Inactive workers cannot receive attendance records");
        }

        if (attendanceRepository.existsByWorkerIdAndAttendanceDateAndDeletedAtIsNull(workerId, request.getAttendanceDate())) {
            throw new DuplicateResourceException("Attendance already recorded for worker " + workerId + " on date: " + request.getAttendanceDate());
        }

        WorkerAttendance attendance = new WorkerAttendance();
        attendance.setWorker(worker);
        attendance.setHarvestRecordId(request.getHarvestRecordId());
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setIsPresent(request.getIsPresent());
        attendance.setHoursWorked(request.getHoursWorked());
        attendance.setRemarks(request.getRemarks());

        WorkerAttendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toResponse(saved);
    }

    @Override
    public WorkerAttendanceDto.AttendanceResponse updateAttendance(Long workerId, Long attendanceId, WorkerAttendanceDto.AttendanceRequest request) {
        if (!workerRepository.existsById(workerId)) {
            throw new ResourceNotFoundException("Worker not found with id: " + workerId);
        }

        WorkerAttendance attendance = attendanceRepository.findByIdAndDeletedAtIsNull(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + attendanceId));

        if (attendanceRepository.existsByWorkerIdAndAttendanceDateAndIdNotAndDeletedAtIsNull(workerId, request.getAttendanceDate(), attendanceId)) {
            throw new DuplicateResourceException("Attendance already recorded for worker " + workerId + " on date: " + request.getAttendanceDate());
        }

        attendance.setHarvestRecordId(request.getHarvestRecordId());
        attendance.setAttendanceDate(request.getAttendanceDate());
        attendance.setIsPresent(request.getIsPresent());
        attendance.setHoursWorked(request.getHoursWorked());
        attendance.setRemarks(request.getRemarks());

        WorkerAttendance updated = attendanceRepository.save(attendance);
        return attendanceMapper.toResponse(updated);
    }

    @Override
    public void deleteAttendance(Long workerId, Long attendanceId) {
        if (!workerRepository.existsById(workerId)) {
            throw new ResourceNotFoundException("Worker not found with id: " + workerId);
        }

        WorkerAttendance attendance = attendanceRepository.findByIdAndDeletedAtIsNull(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + attendanceId));

        attendance.setDeletedAt(OffsetDateTime.now());
        attendance.setIsActive(false);
        attendanceRepository.save(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkerPaymentDto.PaymentResponse> getWorkerPayments(Long workerId) {
        if (!workerRepository.existsById(workerId)) {
            throw new ResourceNotFoundException("Worker not found with id: " + workerId);
        }
        return paymentRepository.findByWorkerIdAndDeletedAtIsNull(workerId).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public WorkerPaymentDto.PaymentResponse recordPayment(Long workerId, WorkerPaymentDto.PaymentRequest request) {
        Worker worker = workerRepository.findByIdAndDeletedAtIsNull(workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + workerId));

        if (request.getPeriodEnd().isBefore(request.getPeriodStart())) {
            throw new IllegalArgumentException("Payment period end date must be on or after start date");
        }

        PaymentMethod paymentMethod = null;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findByIdAndDeletedAtIsNull(request.getPaymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + request.getPaymentMethodId()));
        }

        WorkerPayment payment = new WorkerPayment();
        payment.setWorker(worker);
        payment.setPaymentMethod(paymentMethod);
        payment.setPeriodStart(request.getPeriodStart());
        payment.setPeriodEnd(request.getPeriodEnd());
        payment.setTotalDaysWorked(request.getTotalDaysWorked() != null ? request.getTotalDaysWorked() : 0);
        payment.setDailyWageRate(request.getDailyWageRate());
        payment.setAmount(request.getAmount());
        payment.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus().toUpperCase().trim() : "PENDING");
        payment.setPaidDate(request.getPaidDate());
        payment.setNotes(request.getNotes());

        WorkerPayment saved = paymentRepository.save(payment);
        return paymentMapper.toResponse(saved);
    }

    private void validateWorkerRequest(WorkerDto.WorkerRequest request, Long existingId) {
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            String phone = request.getPhone().trim();
            if (existingId == null) {
                if (workerRepository.existsByPhoneAndDeletedAtIsNull(phone)) {
                    throw new DuplicateResourceException("Worker with phone number '" + phone + "' already exists");
                }
            } else {
                if (workerRepository.existsByPhoneAndIdNotAndDeletedAtIsNull(phone, existingId)) {
                    throw new DuplicateResourceException("Worker with phone number '" + phone + "' already exists");
                }
            }
        }
    }
}
