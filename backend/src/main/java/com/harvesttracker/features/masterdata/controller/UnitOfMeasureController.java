package com.harvesttracker.features.masterdata.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.UnitOfMeasureDto;
import com.harvesttracker.features.masterdata.service.UnitOfMeasureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/units-of-measure")
@Tag(name = "Master Data - Units of Measure", description = "Management of system measurement units")
@SecurityRequirement(name = "bearerAuth")
public class UnitOfMeasureController {

    private final UnitOfMeasureService unitOfMeasureService;

    public UnitOfMeasureController(UnitOfMeasureService unitOfMeasureService) {
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated units of measure", description = "Retrieve list of measurement units with filtering, search, measureType, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<UnitOfMeasureDto.UnitOfMeasureResponse>>> getAllUnitsOfMeasure(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "sortOrder") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "measureType", required = false) String measureType,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<UnitOfMeasureDto.UnitOfMeasureResponse> pagedResponse =
                unitOfMeasureService.getAllUnitsOfMeasure(page, size, sort, direction, measureType, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unit of measure by ID", description = "Retrieve a specific unit of measure by its unique identifier")
    public ResponseEntity<ApiResponse<UnitOfMeasureDto.UnitOfMeasureResponse>> getUnitOfMeasureById(@PathVariable("id") Long id) {
        UnitOfMeasureDto.UnitOfMeasureResponse response = unitOfMeasureService.getUnitOfMeasureById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new unit of measure", description = "Create a new unit of measure record (ADMIN only)")
    public ResponseEntity<ApiResponse<UnitOfMeasureDto.UnitOfMeasureResponse>> createUnitOfMeasure(
            @Valid @RequestBody UnitOfMeasureDto.UnitOfMeasureRequest request) {

        UnitOfMeasureDto.UnitOfMeasureResponse response = unitOfMeasureService.createUnitOfMeasure(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Unit of measure created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing unit of measure", description = "Update details of an existing unit of measure (ADMIN only)")
    public ResponseEntity<ApiResponse<UnitOfMeasureDto.UnitOfMeasureResponse>> updateUnitOfMeasure(
            @PathVariable("id") Long id,
            @Valid @RequestBody UnitOfMeasureDto.UnitOfMeasureRequest request) {

        UnitOfMeasureDto.UnitOfMeasureResponse response = unitOfMeasureService.updateUnitOfMeasure(id, request);
        return ResponseEntity.ok(ApiResponse.success("Unit of measure updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle unit of measure active status", description = "Activate or deactivate a unit of measure (ADMIN only)")
    public ResponseEntity<ApiResponse<UnitOfMeasureDto.UnitOfMeasureResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        UnitOfMeasureDto.UnitOfMeasureResponse response = unitOfMeasureService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Unit of measure status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete unit of measure", description = "Soft delete a unit of measure record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteUnitOfMeasure(@PathVariable("id") Long id) {
        unitOfMeasureService.deleteUnitOfMeasure(id);
        return ResponseEntity.ok(ApiResponse.success("Unit of measure deleted successfully", null));
    }
}
