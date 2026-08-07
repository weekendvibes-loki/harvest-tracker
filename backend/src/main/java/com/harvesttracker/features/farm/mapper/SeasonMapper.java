package com.harvesttracker.features.farm.mapper;

import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.farm.dto.SeasonDto;
import org.springframework.stereotype.Component;

@Component
public class SeasonMapper {

    public SeasonDto.SeasonResponse toResponse(Season entity) {
        if (entity == null) {
            return null;
        }
        SeasonDto.SeasonResponse dto = new SeasonDto.SeasonResponse();
        dto.setId(entity.getId());
        if (entity.getFarm() != null) {
            dto.setFarmId(entity.getFarm().getId());
            dto.setFarmName(entity.getFarm().getName());
        }
        if (entity.getFruitType() != null) {
            dto.setFruitTypeId(entity.getFruitType().getId());
            dto.setFruitTypeName(entity.getFruitType().getName());
        }
        dto.setName(entity.getName());
        dto.setYear(entity.getYear());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setStatus(entity.getStatus());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
