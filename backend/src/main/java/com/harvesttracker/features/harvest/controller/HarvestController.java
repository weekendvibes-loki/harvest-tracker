package com.harvesttracker.features.harvest.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.harvest.dto.HarvestDto;
import com.harvesttracker.features.harvest.dto.HarvestSummaryDto;
import com.harvesttracker.features.harvest.dto.HarvestWorkerDto;
import com.harvesttracker.features.harvest.service.HarvestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/harvests")
@Tag(name = "Harvest Management", description = "Management of farm harvest batches, quality grades, status transitions, and worker assignments")
@SecurityRequirement(name = "bearerAuth")
public class HarvestController {

    private final HarvestService harvestService;

    public HarvestController(HarvestService harvestService) {
        this.harvestService = harvestService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated harvests", description = "Retrieve list of harvest records with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<HarvestDto.HarvestResponse>>> getAllHarvests(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "seasonId", required = false) Long seasonId,
            @RequestParam(name = "fruitTypeId", required = false) Long fruitTypeId,
            @RequestParam(name = "cropVariantId", required = false) Long cropVariantId,
            @RequestParam(name = "qualityGrade", required = false) String qualityGrade,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(name = "createdBy", required = false) Long createdBy,
            @RequestParam(name = "supervisorId", required = false) Long supervisorId,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<HarvestDto.HarvestResponse> response = harvestService.getAllHarvests(
                page, size, sort, direction, farmId, seasonId, fruitTypeId, cropVariantId,
                qualityGrade, status, startDate, endDate, createdBy, supervisorId, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get harvest summary statistics", description = "Retrieve aggregate summary metrics for harvest records")
    public ResponseEntity<ApiResponse<HarvestSummaryDto.HarvestSummaryResponse>> getHarvestSummary() {
        HarvestSummaryDto.HarvestSummaryResponse summary = harvestService.getHarvestSummary();
        return ResponseEntity.ok(ApiResponse.success(summary));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get harvest record by ID", description = "Retrieve detailed harvest record by ID including assigned workers")
    public ResponseEntity<ApiResponse<HarvestDto.HarvestResponse>> getHarvestById(@PathVariable("id") Long id) {
        HarvestDto.HarvestResponse response = harvestService.getHarvestById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create harvest record", description = "Create a new farm harvest transaction batch (ADMIN only)")
    public ResponseEntity<ApiResponse<HarvestDto.HarvestResponse>> createHarvest(
            @Valid @RequestBody HarvestDto.HarvestRequest request) {

        HarvestDto.HarvestResponse response = harvestService.createHarvest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Harvest record created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update harvest record", description = "Update details of an existing harvest record (ADMIN only)")
    public ResponseEntity<ApiResponse<HarvestDto.HarvestResponse>> updateHarvest(
            @PathVariable("id") Long id,
            @Valid @RequestBody HarvestDto.HarvestRequest request) {

        HarvestDto.HarvestResponse response = harvestService.updateHarvest(id, request);
        return ResponseEntity.ok(ApiResponse.success("Harvest record updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update harvest status", description = "Transition harvest status (DRAFT -> CONFIRMED -> STORED -> SOLD) (ADMIN only)")
    public ResponseEntity<ApiResponse<HarvestDto.HarvestResponse>> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {

        HarvestDto.HarvestResponse response = harvestService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Harvest status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete harvest record", description = "Soft delete a harvest record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteHarvest(@PathVariable("id") Long id) {
        harvestService.deleteHarvest(id);
        return ResponseEntity.ok(ApiResponse.success("Harvest record deleted successfully", null));
    }

    @PostMapping("/{id}/workers")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign worker to harvest", description = "Assign an active worker to a harvest batch (ADMIN only)")
    public ResponseEntity<ApiResponse<HarvestWorkerDto.HarvestWorkerResponse>> assignWorker(
            @PathVariable("id") Long id,
            @Valid @RequestBody HarvestWorkerDto.HarvestWorkerRequest request) {

        HarvestWorkerDto.HarvestWorkerResponse response = harvestService.assignWorker(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Worker assigned to harvest successfully", response));
    }

    @DeleteMapping("/{id}/workers/{workerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove worker from harvest", description = "Remove a worker assignment from a harvest batch (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> removeWorker(
            @PathVariable("id") Long id,
            @PathVariable("workerId") Long workerId) {

        harvestService.removeWorker(id, workerId);
        return ResponseEntity.ok(ApiResponse.success("Worker removed from harvest successfully", null));
    }
}
