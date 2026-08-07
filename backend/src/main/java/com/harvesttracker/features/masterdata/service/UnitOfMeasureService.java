package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.UnitOfMeasureDto;

public interface UnitOfMeasureService {

    PagedResponse<UnitOfMeasureDto.UnitOfMeasureResponse> getAllUnitsOfMeasure(
            int page, int size, String sort, String direction, String measureType, String search, Boolean isActive);

    UnitOfMeasureDto.UnitOfMeasureResponse getUnitOfMeasureById(Long id);

    UnitOfMeasureDto.UnitOfMeasureResponse createUnitOfMeasure(UnitOfMeasureDto.UnitOfMeasureRequest request);

    UnitOfMeasureDto.UnitOfMeasureResponse updateUnitOfMeasure(Long id, UnitOfMeasureDto.UnitOfMeasureRequest request);

    UnitOfMeasureDto.UnitOfMeasureResponse toggleStatus(Long id, boolean isActive);

    void deleteUnitOfMeasure(Long id);
}
