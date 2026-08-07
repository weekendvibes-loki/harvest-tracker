package com.harvesttracker.features.harvest.mapper;

import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.harvest.domain.HarvestRecord;
import com.harvesttracker.features.harvest.dto.HarvestDto;
import com.harvesttracker.features.masterdata.domain.CropVariant;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class HarvestMapper {

    private final HarvestWorkerMapper harvestWorkerMapper;

    public HarvestMapper(HarvestWorkerMapper harvestWorkerMapper) {
        this.harvestWorkerMapper = harvestWorkerMapper;
    }

    public HarvestDto.HarvestResponse toResponse(HarvestRecord entity) {
        if (entity == null) {
            return null;
        }
        HarvestDto.HarvestResponse dto = new HarvestDto.HarvestResponse();
        dto.setId(entity.getId());
        if (entity.getFarm() != null) {
            dto.setFarmId(entity.getFarm().getId());
            dto.setFarmName(entity.getFarm().getName());
        }
        if (entity.getSeason() != null) {
            dto.setSeasonId(entity.getSeason().getId());
            dto.setSeasonName(entity.getSeason().getName());
        }
        if (entity.getFruitType() != null) {
            dto.setFruitTypeId(entity.getFruitType().getId());
            dto.setFruitTypeName(entity.getFruitType().getName());
        }
        if (entity.getCropVariant() != null) {
            dto.setCropVariantId(entity.getCropVariant().getId());
            dto.setCropVariantName(entity.getCropVariant().getName());
        }
        if (entity.getQuantityUom() != null) {
            dto.setQuantityUomId(entity.getQuantityUom().getId());
            dto.setQuantityUomCode(entity.getQuantityUom().getCode());
        }
        if (entity.getSupervisor() != null) {
            dto.setSupervisorId(entity.getSupervisor().getId());
            dto.setSupervisorName(entity.getSupervisor().getName());
        }
        dto.setHarvestDate(entity.getHarvestDate());
        dto.setHarvestQuantity(entity.getHarvestQuantity());
        dto.setQualityGrade(entity.getQualityGrade());
        dto.setStorageLocation(entity.getStorageLocation());
        dto.setStatus(entity.getStatus());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getHarvestWorkers() != null) {
            dto.setWorkers(entity.getHarvestWorkers().stream()
                    .filter(hw -> hw.getDeletedAt() == null)
                    .map(harvestWorkerMapper::toResponse)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public void updateEntity(
            HarvestRecord entity,
            HarvestDto.HarvestRequest request,
            Farm farm,
            Season season,
            FruitType fruitType,
            CropVariant cropVariant,
            UnitOfMeasure quantityUom,
            User supervisor) {

        entity.setFarm(farm);
        entity.setSeason(season);
        entity.setFruitType(fruitType);
        entity.setCropVariant(cropVariant);
        entity.setQuantityUom(quantityUom);
        entity.setSupervisor(supervisor);
        entity.setHarvestDate(request.getHarvestDate());
        entity.setHarvestQuantity(request.getHarvestQuantity());
        if (request.getQualityGrade() != null) {
            entity.setQualityGrade(request.getQualityGrade().toUpperCase().trim());
        }
        entity.setStorageLocation(request.getStorageLocation());
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus().toUpperCase().trim());
        }
        entity.setNotes(request.getNotes());
    }
}
