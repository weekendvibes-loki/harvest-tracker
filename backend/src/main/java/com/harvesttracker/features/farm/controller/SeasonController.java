package com.harvesttracker.features.farm.controller;

import com.harvesttracker.common.dto.ApiResponse;
import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.farm.dto.SeasonDto;
import com.harvesttracker.features.farm.service.SeasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seasons")
@Tag(name = "Season Management", description = "Management of harvest time windows and seasons")
@SecurityRequirement(name = "bearerAuth")
public class SeasonController {

    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get paginated seasons", description = "Retrieve list of seasons with filtering by farm, fruit type, year, status, and pagination")
    public ResponseEntity<ApiResponse<PagedResponse<SeasonDto.SeasonResponse>>> getAllSeasons(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "farmId", required = false) Long farmId,
            @RequestParam(name = "fruitTypeId", required = false) Long fruitTypeId,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isActive", required = false) Boolean isActive) {

        PagedResponse<SeasonDto.SeasonResponse> pagedResponse = seasonService.getAllSeasons(
                page, size, sort, direction, farmId, fruitTypeId, year, status, search, isActive);
        return ResponseEntity.ok(ApiResponse.success(pagedResponse));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get season by ID", description = "Retrieve a specific season by its unique identifier")
    public ResponseEntity<ApiResponse<SeasonDto.SeasonResponse>> getSeasonById(@PathVariable("id") Long id) {
        SeasonDto.SeasonResponse response = seasonService.getSeasonById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new season", description = "Create a new harvest season record (ADMIN only)")
    public ResponseEntity<ApiResponse<SeasonDto.SeasonResponse>> createSeason(
            @Valid @RequestBody SeasonDto.SeasonRequest request) {

        SeasonDto.SeasonResponse response = seasonService.createSeason(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Season created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update existing season", description = "Update details of an existing season (ADMIN only)")
    public ResponseEntity<ApiResponse<SeasonDto.SeasonResponse>> updateSeason(
            @PathVariable("id") Long id,
            @Valid @RequestBody SeasonDto.SeasonRequest request) {

        SeasonDto.SeasonResponse response = seasonService.updateSeason(id, request);
        return ResponseEntity.ok(ApiResponse.success("Season updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete season", description = "Soft delete a season record (ADMIN only)")
    public ResponseEntity<ApiResponse<Void>> deleteSeason(@PathVariable("id") Long id) {
        seasonService.deleteSeason(id);
        return ResponseEntity.ok(ApiResponse.success("Season deleted successfully", null));
    }
}
