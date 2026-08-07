package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.FruitTypeDto;

public interface FruitTypeService {

    PagedResponse<FruitTypeDto.FruitTypeResponse> getAllFruitTypes(
            int page, int size, String sort, String direction, String search, Boolean isActive);

    FruitTypeDto.FruitTypeResponse getFruitTypeById(Long id);

    FruitTypeDto.FruitTypeResponse createFruitType(FruitTypeDto.FruitTypeRequest request);

    FruitTypeDto.FruitTypeResponse updateFruitType(Long id, FruitTypeDto.FruitTypeRequest request);

    FruitTypeDto.FruitTypeResponse toggleStatus(Long id, boolean isActive);

    void deleteFruitType(Long id);
}
