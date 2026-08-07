package com.harvesttracker.features.masterdata.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.CropVariantDto;
import com.harvesttracker.features.masterdata.service.CropVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crop-variants")
@Tag(name = "Master Data - Crop Variants", description = "Management of crop varieties per fruit type")
@SecurityRequirement(name = "bearerAuth")
public class CropVariantController {

    private final CropVariantService cropVariantService;

    public CropVariantController(CropVariantService cropVariantService) {
        this.cropVariantService = cropVariantService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated crop variants", description = "Retrieve list of crop variants with filtering, search, fruitTypeId, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<CropVariantDto.CropVariantResponse>>> getAllCropVariants(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "fruitTypeId", required = false) Long fruitTypeId,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<CropVariantDto.CropVariantResponse> pagedResponse =
                cropVariantService.getAllCropVariants(page, size, sort, direction, fruitTypeId, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get crop variant by ID", description = "Retrieve a specific crop variant by its unique identifier")
    public ResponseEntity<ApiResponse<CropVariantDto.CropVariantResponse>> getCropVariantById(@PathVariable("id") Long id) {
        CropVariantDto.CropVariantResponse response = cropVariantService.getCropVariantById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new crop variant", description = "Create a new crop variant record (ADMIN only)")
    public ResponseEntity<ApiResponse<CropVariantDto.CropVariantResponse>> createCropVariant(
            @Valid @RequestBody CropVariantDto.CropVariantRequest request) {

        CropVariantDto.CropVariantResponse response = cropVariantService.createCropVariant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Crop variant created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing crop variant", description = "Update details of an existing crop variant (ADMIN only)")
    public ResponseEntity<ApiResponse<CropVariantDto.CropVariantResponse>> updateCropVariant(
            @PathVariable("id") Long id,
            @Valid @RequestBody CropVariantDto.CropVariantRequest request) {

        CropVariantDto.CropVariantResponse response = cropVariantService.updateCropVariant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Crop variant updated successfully", response));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle crop variant active status", description = "Activate or deactivate a crop variant (ADMIN only)")
    public ResponseEntity<ApiResponse<CropVariantDto.CropVariantResponse>> toggleStatus(
            @PathVariable("id") Long id,
            @RequestParam("isActive") boolean isActive) {

        CropVariantDto.CropVariantResponse response = cropVariantService.toggleStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Crop variant status updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete crop variant", description = "Soft delete a crop variant record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteCropVariant(@PathVariable("id") Long id) {
        cropVariantService.deleteCropVariant(id);
        return ResponseEntity.ok(ApiResponse.success("Crop variant deleted successfully", null));
    }
}
