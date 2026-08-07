package com.harvesttracker.features.farm.mapper;

import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.dto.FarmDto;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FarmMapper {

    private final FarmFruitTypeMapper farmFruitTypeMapper;

    public FarmMapper(FarmFruitTypeMapper farmFruitTypeMapper) {
        this.farmFruitTypeMapper = farmFruitTypeMapper;
    }

    public FarmDto.FarmResponse toResponse(Farm entity) {
        if (entity == null) {
            return null;
        }
        FarmDto.FarmResponse dto = new FarmDto.FarmResponse();
        dto.setId(entity.getId());
        if (entity.getOwner() != null) {
            dto.setOwnerId(entity.getOwner().getId());
            dto.setOwnerName(entity.getOwner().getName());
        }
        if (entity.getLandUom() != null) {
            dto.setLandUomId(entity.getLandUom().getId());
            dto.setLandUomCode(entity.getLandUom().getCode());
        }
        dto.setName(entity.getName());
        dto.setOwnershipType(entity.getOwnershipType());
        dto.setLandSize(entity.getLandSize());
        dto.setGpsLatitude(entity.getGpsLatitude());
        dto.setGpsLongitude(entity.getGpsLongitude());
        dto.setAddress(entity.getAddress());
        dto.setStatus(entity.getStatus());
        dto.setLeaseStartDate(entity.getLeaseStartDate());
        dto.setLeaseEndDate(entity.getLeaseEndDate());
        dto.setLessorName(entity.getLessorName());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getFarmFruitTypes() != null) {
            dto.setFruitTypes(entity.getFarmFruitTypes().stream()
                    .filter(fft -> fft.getDeletedAt() == null)
                    .map(farmFruitTypeMapper::toResponse)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public void updateEntity(Farm entity, FarmDto.FarmRequest request, User owner, UnitOfMeasure landUom) {
        entity.setOwner(owner);
        entity.setLandUom(landUom);
        entity.setName(request.getName().trim());
        entity.setOwnershipType(request.getOwnershipType() != null ? request.getOwnershipType().toUpperCase().trim() : "OWNED");
        entity.setLandSize(request.getLandSize());
        entity.setGpsLatitude(request.getGpsLatitude());
        entity.setGpsLongitude(request.getGpsLongitude());
        entity.setAddress(request.getAddress());
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus().toUpperCase().trim());
        }
        entity.setLeaseStartDate(request.getLeaseStartDate());
        entity.setLeaseEndDate(request.getLeaseEndDate());
        entity.setLessorName(request.getLessorName());
        entity.setNotes(request.getNotes());
    }
}
