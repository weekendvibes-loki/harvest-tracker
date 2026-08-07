package com.harvesttracker.features.masterdata.mapper;

import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.dto.FruitTypeDto;
import org.springframework.stereotype.Component;

@Component
public class FruitTypeMapper {

    public FruitType toEntity(FruitTypeDto.FruitTypeRequest request) {
        if (request == null) {
            return null;
        }
        FruitType fruitType = new FruitType();
        fruitType.setName(request.getName());
        fruitType.setCode(request.getCode());
        fruitType.setDescription(request.getDescription());
        fruitType.setSeasonType(request.getSeasonType());
        fruitType.setSortOrder(request.getSortOrder());
        fruitType.setIsActive(request.getIsActive());
        return fruitType;
    }

    public void updateEntity(FruitType fruitType, FruitTypeDto.FruitTypeRequest request) {
        if (fruitType == null || request == null) {
            return;
        }
        fruitType.setName(request.getName());
        fruitType.setCode(request.getCode());
        fruitType.setDescription(request.getDescription());
        fruitType.setSeasonType(request.getSeasonType());
        if (request.getSortOrder() != null) {
            fruitType.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            fruitType.setIsActive(request.getIsActive());
        }
    }

    public FruitTypeDto.FruitTypeResponse toResponse(FruitType fruitType) {
        if (fruitType == null) {
            return null;
        }
        return new FruitTypeDto.FruitTypeResponse(
                fruitType.getId(),
                fruitType.getName(),
                fruitType.getCode(),
                fruitType.getDescription(),
                fruitType.getSeasonType(),
                fruitType.getSortOrder(),
                fruitType.getIsActive(),
                fruitType.getCreatedAt(),
                fruitType.getUpdatedAt()
        );
    }
}
