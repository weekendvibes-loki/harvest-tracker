package com.harvesttracker.features.farm.mapper;

import com.harvesttracker.features.farm.domain.FarmFruitType;
import com.harvesttracker.features.farm.dto.FarmFruitTypeDto;
import org.springframework.stereotype.Component;

@Component
public class FarmFruitTypeMapper {

    public FarmFruitTypeDto.FarmFruitTypeResponse toResponse(FarmFruitType entity) {
        if (entity == null) {
            return null;
        }
        FarmFruitTypeDto.FarmFruitTypeResponse dto = new FarmFruitTypeDto.FarmFruitTypeResponse();
        dto.setId(entity.getId());
        if (entity.getFarm() != null) {
            dto.setFarmId(entity.getFarm().getId());
        }
        if (entity.getFruitType() != null) {
            dto.setFruitTypeId(entity.getFruitType().getId());
            dto.setFruitTypeName(entity.getFruitType().getName());
            dto.setFruitTypeCode(entity.getFruitType().getCode());
        }
        dto.setIsPrimary(entity.getIsPrimary());
        dto.setFirstPlantedDate(entity.getFirstPlantedDate());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
