package com.harvesttracker.features.masterdata.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.WorkerTypeDto;
import com.harvesttracker.features.masterdata.service.WorkerTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/worker-types")
@Tag(name = "Master Data - Worker Types", description = "Management of worker classifications")
@SecurityRequirement(name = "bearerAuth")
public class WorkerTypeController {

    private final WorkerTypeService workerTypeService;

    public WorkerTypeController(WorkerTypeService workerTypeService) {
        this.workerTypeService = workerTypeService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated worker types", description = "Retrieve list of worker types with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<WorkerTypeDto.WorkerTypeResponse>>> getAllWorkerTypes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "sortOrder") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<WorkerTypeDto.WorkerTypeResponse> pagedResponse =
                workerTypeService.getAllWorkerTypes(page, size, sort, direction, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get worker type by ID", description = "Retrieve a specific worker type by its unique identifier")
    public ResponseEntity<ApiResponse<WorkerTypeDto.WorkerTypeResponse>> getWorkerTypeById(@PathVariable("id") Long id) {
        WorkerTypeDto.WorkerTypeResponse response = workerTypeService.getWorkerTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new worker type", description = "Create a new worker type record (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerTypeDto.WorkerTypeResponse>> createWorkerType(
            @Valid @RequestBody WorkerTypeDto.WorkerTypeRequest request) {

        WorkerTypeDto.WorkerTypeResponse response = workerTypeService.createWorkerType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Worker type created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing worker type", description = "Update details of an existing worker type (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerTypeDto.WorkerTypeResponse>> updateWorkerType(
            @PathVariable("id") Long id,
            @Valid @RequestBody WorkerTypeDto.WorkerTypeRequest request) {

        WorkerTypeDto.WorkerTypeResponse response = workerTypeService.updateWorkerType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Worker type updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle worker type active status", description = "Activate or deactivate a worker type (ADMIN only)")
    public ResponseEntity<ApiResponse<WorkerTypeDto.WorkerTypeResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        WorkerTypeDto.WorkerTypeResponse response = workerTypeService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Worker type status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete worker type", description = "Soft delete a worker type record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteWorkerType(@PathVariable("id") Long id) {
        workerTypeService.deleteWorkerType(id);
        return ResponseEntity.ok(ApiResponse.success("Worker type deleted successfully", null));
    }
}
