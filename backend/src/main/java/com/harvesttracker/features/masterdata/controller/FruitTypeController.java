package com.harvesttracker.features.masterdata.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.FruitTypeDto;
import com.harvesttracker.features.masterdata.service.FruitTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fruit-types")
@Tag(name = "Master Data - Fruit Types", description = "Management of supported fruit types")
@SecurityRequirement(name = "bearerAuth")
public class FruitTypeController {

    private final FruitTypeService fruitTypeService;

    public FruitTypeController(FruitTypeService fruitTypeService) {
        this.fruitTypeService = fruitTypeService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated fruit types", description = "Retrieve list of fruit types with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<FruitTypeDto.FruitTypeResponse>>> getAllFruitTypes(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "sortOrder") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<FruitTypeDto.FruitTypeResponse> pagedResponse =
                fruitTypeService.getAllFruitTypes(page, size, sort, direction, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get fruit type by ID", description = "Retrieve a specific fruit type by its unique identifier")
    public ResponseEntity<ApiResponse<FruitTypeDto.FruitTypeResponse>> getFruitTypeById(@PathVariable("id") Long id) {
        FruitTypeDto.FruitTypeResponse response = fruitTypeService.getFruitTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new fruit type", description = "Create a new fruit type master record (ADMIN only)")
    public ResponseEntity<ApiResponse<FruitTypeDto.FruitTypeResponse>> createFruitType(
            @Valid @RequestBody FruitTypeDto.FruitTypeRequest request) {

        FruitTypeDto.FruitTypeResponse response = fruitTypeService.createFruitType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Fruit type created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing fruit type", description = "Update details of an existing fruit type (ADMIN only)")
    public ResponseEntity<ApiResponse<FruitTypeDto.FruitTypeResponse>> updateFruitType(
            @PathVariable("id") Long id,
            @Valid @RequestBody FruitTypeDto.FruitTypeRequest request) {

        FruitTypeDto.FruitTypeResponse response = fruitTypeService.updateFruitType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Fruit type updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle fruit type active status", description = "Activate or deactivate a fruit type (ADMIN only)")
    public ResponseEntity<ApiResponse<FruitTypeDto.FruitTypeResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        FruitTypeDto.FruitTypeResponse response = fruitTypeService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Fruit type status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete fruit type", description = "Soft delete a fruit type record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteFruitType(@PathVariable("id") Long id) {
        fruitTypeService.deleteFruitType(id);
        return ResponseEntity.ok(ApiResponse.success("Fruit type deleted successfully", null));
    }
}
