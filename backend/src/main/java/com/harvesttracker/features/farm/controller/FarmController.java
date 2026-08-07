package com.harvesttracker.features.farm.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.farm.dto.FarmDto;
import com.harvesttracker.features.farm.dto.FarmFruitTypeDto;
import com.harvesttracker.features.farm.service.FarmService;
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
@RequestMapping("/api/v1/farms")
@Tag(name = "Farm Management", description = "Management of agricultural farms and fruit type associations")
@SecurityRequirement(name = "bearerAuth")
public class FarmController {

    private final FarmService farmService;

    public FarmController(FarmService farmService) {
        this.farmService = farmService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated farms", description = "Retrieve list of farms with filtering, search, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<FarmDto.FarmResponse>>> getAllFarms(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "village", required = false) String village,
            @RequestParam(name = "district", required = false) String district,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "ownerName", required = false) String ownerName,
            @RequestParam(name = "ownerId", required = false) Long ownerId,
            @RequestParam(name = "ownershipType", required = false) String ownershipType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            @RequestParam(name = "fruitTypeId", required = false) Long fruitTypeId) {

        PagedResponse<FarmDto.FarmResponse> pagedResponse = farmService.getAllFarms(
                page, size, sort, direction, search, name, village, district, state,
                ownerName, ownerId, ownershipType, status, isActive, fruitTypeId);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search farms", description = "Search farms by name, location fields (village, district, state), owner, or fruit type")
    public ResponseEntity<ApiResponse<PagedResponse<FarmDto.FarmResponse>>> searchFarms(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "village", required = false) String village,
            @RequestParam(name = "district", required = false) String district,
            @RequestParam(name = "state", required = false) String state,
            @RequestParam(name = "ownerName", required = false) String ownerName,
            @RequestParam(name = "ownerId", required = false) Long ownerId,
            @RequestParam(name = "ownershipType", required = false) String ownershipType,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "isActive", required = false) Boolean isActive,
            @RequestParam(name = "fruitTypeId", required = false) Long fruitTypeId) {

        PagedResponse<FarmDto.FarmResponse> pagedResponse = farmService.getAllFarms(
                page, size, sort, direction, search, name, village, district, state,
                ownerName, ownerId, ownershipType, status, isActive, fruitTypeId);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get farm by ID", description = "Retrieve detailed farm information by ID")
    public ResponseEntity<ApiResponse<FarmDto.FarmResponse>> getFarmById(@PathVariable("id") Long id) {
        FarmDto.FarmResponse response = farmService.getFarmById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new farm", description = "Create a new farm record (ADMIN only)")
    public ResponseEntity<ApiResponse<FarmDto.FarmResponse>> createFarm(
            @Valid @RequestBody FarmDto.FarmRequest request) {

        FarmDto.FarmResponse response = farmService.createFarm(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Farm created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing farm", description = "Update details of an existing farm (ADMIN only)")
    public ResponseEntity<ApiResponse<FarmDto.FarmResponse>> updateFarm(
            @PathVariable("id") Long id,
            @Valid @RequestBody FarmDto.FarmRequest request) {

        FarmDto.FarmResponse response = farmService.updateFarm(id, request);
        return ResponseEntity.ok(ApiResponse.success("Farm updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle farm active status", description = "Activate or deactivate a farm (ADMIN only)")
    public ResponseEntity<ApiResponse<FarmDto.FarmResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        FarmDto.FarmResponse response = farmService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Farm status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete farm", description = "Soft delete a farm record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteFarm(@PathVariable("id") Long id) {
        farmService.deleteFarm(id);
        return ResponseEntity.ok(ApiResponse.success("Farm deleted successfully", null));
    }

    @GetMapping("/{id}/fruit-types")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get farm fruit types", description = "Retrieve list of fruit types cultivated on the farm")
    public ResponseEntity<ApiResponse<List<FarmFruitTypeDto.FarmFruitTypeResponse>>> getFarmFruitTypes(
            @PathVariable("id") Long id) {
        List<FarmFruitTypeDto.FarmFruitTypeResponse> list = farmService.getFarmFruitTypes(id);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{id}/fruit-types")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Associate fruit type to farm", description = "Associate a fruit type with a farm (ADMIN only)")
    public ResponseEntity<ApiResponse<FarmFruitTypeDto.FarmFruitTypeResponse>> addFruitTypeToFarm(
            @PathVariable("id") Long id,
            @Valid @RequestBody FarmFruitTypeDto.FarmFruitTypeRequest request) {

        FarmFruitTypeDto.FarmFruitTypeResponse response = farmService.addFruitTypeToFarm(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Fruit type added to farm successfully", response));
    }

    @DeleteMapping("/{id}/fruit-types/{fruitTypeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove fruit type from farm", description = "Disassociate a fruit type from a farm (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> removeFruitTypeFromFarm(
            @PathVariable("id") Long id,
            @PathVariable("fruitTypeId") Long fruitTypeId) {

        farmService.removeFruitTypeFromFarm(id, fruitTypeId);
        return ResponseEntity.ok(ApiResponse.success("Fruit type removed from farm successfully", null));
    }
}
