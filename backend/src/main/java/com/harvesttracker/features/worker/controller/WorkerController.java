package com.harvesttracker.features.worker.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.worker.dto.WorkerAttendanceDto;
import com.harvesttracker.features.worker.dto.WorkerDto;
import com.harvesttracker.features.worker.dto.WorkerPaymentDto;
import com.harvesttracker.features.worker.service.WorkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workers")
@Tag(name = "Worker Management", description = "Management of farm workers, daily attendance, and payments")
@SecurityRequirement(name = "bearerAuth")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated workers", description = "Retrieve list of workers with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<WorkerDto.WorkerResponse>>> getAllWorkers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "village", required = false) String village,
            @RequestParam(name = "workerTypeId", required = false) Long workerTypeId,
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<WorkerDto.WorkerResponse> pagedResponse = workerService.getAllWorkers(
                page, size, sort, direction, search, name, phone, village, workerTypeId, farmId, status, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search workers", description = "Search workers by name, phone, address/village, worker type, farm, or status")
    public ResponseEntity<ApiResponse<PagedResponse<WorkerDto.WorkerResponse>>> searchWorkers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "village", required = false) String village,
            @RequestParam(name = "workerTypeId", required = false) Long workerTypeId,
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<WorkerDto.WorkerResponse> pagedResponse = workerService.getAllWorkers(
                page, size, sort, direction, search, name, phone, village, workerTypeId, farmId, status, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get worker by ID", description = "Retrieve detailed worker information by ID")
    public ResponseEntity<ApiResponse<WorkerDto.WorkerResponse>> getWorkerById(@PathVariable("id") Long id) {
        WorkerDto.WorkerResponse response = workerService.getWorkerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new worker", description = "Create a new worker record (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerDto.WorkerResponse>> createWorker(
            @Valid @RequestBody WorkerDto.WorkerRequest request) {

        WorkerDto.WorkerResponse response = workerService.createWorker(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Worker created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing worker", description = "Update details of an existing worker (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerDto.WorkerResponse>> updateWorker(
            @PathVariable("id") Long id,
            @Valid @RequestBody WorkerDto.WorkerRequest request) {

        WorkerDto.WorkerResponse response = workerService.updateWorker(id, request);
        return ResponseEntity.ok(ApiResponse.success("Worker updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle worker active status", description = "Activate or deactivate a worker (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerDto.WorkerResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        WorkerDto.WorkerResponse response = workerService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Worker status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete worker", description = "Soft delete a worker record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteWorker(@PathVariable("id") Long id) {
        workerService.deleteWorker(id);
        return ResponseEntity.ok(ApiResponse.success("Worker deleted successfully", null));
    }

    @GetMapping("/{id}/attendance")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get worker attendance history", description = "Retrieve list of attendance records for a worker")
    public ResponseEntity<ApiResponse<List<WorkerAttendanceDto.AttendanceResponse>>> getWorkerAttendance(
            @PathVariable("id") Long id) {
        List<WorkerAttendanceDto.AttendanceResponse> list = workerService.getWorkerAttendance(id);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{id}/attendance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Record worker attendance", description = "Record daily attendance for an active worker (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerAttendanceDto.AttendanceResponse>> recordAttendance(
            @PathVariable("id") Long id,
            @Valid @RequestBody WorkerAttendanceDto.AttendanceRequest request) {

        WorkerAttendanceDto.AttendanceResponse response = workerService.recordAttendance(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Attendance recorded successfully", response));
    }

    @PutMapping("/{id}/attendance/{attendanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update worker attendance", description = "Update an existing attendance record (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerAttendanceDto.AttendanceResponse>> updateAttendance(
            @PathVariable("id") Long id,
            @PathVariable("attendanceId") Long attendanceId,
            @Valid @RequestBody WorkerAttendanceDto.AttendanceRequest request) {

        WorkerAttendanceDto.AttendanceResponse response = workerService.updateAttendance(id, attendanceId, request);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", response));
    }

    @DeleteMapping("/{id}/attendance/{attendanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete worker attendance", description = "Soft delete an attendance record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteAttendance(
            @PathVariable("id") Long id,
            @PathVariable("attendanceId") Long attendanceId) {

        workerService.deleteAttendance(id, attendanceId);
        return ResponseEntity.ok(ApiResponse.success("Attendance record deleted successfully", null));
    }

    @GetMapping("/{id}/payments")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get worker payment history", description = "Retrieve list of payment settlements for a worker")
    public ResponseEntity<ApiResponse<List<WorkerPaymentDto.PaymentResponse>>> getWorkerPayments(
            @PathVariable("id") Long id) {
        List<WorkerPaymentDto.PaymentResponse> list = workerService.getWorkerPayments(id);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Record worker payment", description = "Record a new payment settlement for a worker (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerPaymentDto.PaymentResponse>> recordPayment(
            @PathVariable("id") Long id,
            @Valid @RequestBody WorkerPaymentDto.PaymentRequest request) {

        WorkerPaymentDto.PaymentResponse response = workerService.recordPayment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Payment recorded successfully", response));
    }
}
