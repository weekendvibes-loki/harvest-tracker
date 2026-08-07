package com.harvesttracker.features.harvest.mapper;

import com.harvesttracker.features.harvest.domain.HarvestWorker;
import com.harvesttracker.features.harvest.dto.HarvestWorkerDto;
import org.springframework.stereotype.Component;

@Component
public class HarvestWorkerMapper {

    public HarvestWorkerDto.HarvestWorkerResponse toResponse(HarvestWorker entity) {
        if (entity == null) {
            return null;
        }
        HarvestWorkerDto.HarvestWorkerResponse dto = new HarvestWorkerDto.HarvestWorkerResponse();
        dto.setId(entity.getId());
        if (entity.getHarvestRecord() != null) {
            dto.setHarvestRecordId(entity.getHarvestRecord().getId());
        }
        if (entity.getWorker() != null) {
            dto.setWorkerId(entity.getWorker().getId());
            dto.setWorkerName(entity.getWorker().getName());
        }
        dto.setRoleInHarvest(entity.getRoleInHarvest());
        dto.setHoursWorked(entity.getHoursWorked());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
