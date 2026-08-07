package com.harvesttracker.features.farm.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.farm.dto.FarmDto;
import com.harvesttracker.features.farm.dto.FarmFruitTypeDto;

import java.util.List;

public interface FarmService {

    PagedResponse<FarmDto.FarmResponse> getAllFarms(
            int page, int size, String sort, String direction,
            String search, String name, String village, String district, String state,
            String ownerName, Long ownerId, String ownershipType, String status,
            Boolean isActive, Long fruitTypeId);

    FarmDto.FarmResponse getFarmById(Long id);

    FarmDto.FarmResponse createFarm(FarmDto.FarmRequest request);

    FarmDto.FarmResponse updateFarm(Long id, FarmDto.FarmRequest request);

    FarmDto.FarmResponse toggleStatus(Long id, boolean isActive);

    void deleteFarm(Long id);

    List<FarmFruitTypeDto.FarmFruitTypeResponse> getFarmFruitTypes(Long farmId);

    FarmFruitTypeDto.FarmFruitTypeResponse addFruitTypeToFarm(Long farmId, FarmFruitTypeDto.FarmFruitTypeRequest request);

    void removeFruitTypeFromFarm(Long farmId, Long fruitTypeId);
}
