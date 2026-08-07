package com.harvesttracker.features.worker.mapper;

import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import com.harvesttracker.features.masterdata.domain.WorkerType;
import com.harvesttracker.features.worker.domain.Worker;
import com.harvesttracker.features.worker.dto.WorkerDto;
import org.springframework.stereotype.Component;

@Component
public class WorkerMapper {

    public WorkerDto.WorkerResponse toResponse(Worker entity) {
        if (entity == null) {
            return null;
        }
        WorkerDto.WorkerResponse dto = new WorkerDto.WorkerResponse();
        dto.setId(entity.getId());
        if (entity.getFarm() != null) {
            dto.setFarmId(entity.getFarm().getId());
            dto.setFarmName(entity.getFarm().getName());
        }
        if (entity.getWorkerType() != null) {
            dto.setWorkerTypeId(entity.getWorkerType().getId());
            dto.setWorkerTypeName(entity.getWorkerType().getName());
        }
        if (entity.getWageUom() != null) {
            dto.setWageUomId(entity.getWageUom().getId());
            dto.setWageUomCode(entity.getWageUom().getCode());
        }
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        dto.setIdCardNumber(entity.getIdCardNumber());
        dto.setDailyWageRate(entity.getDailyWageRate());
        dto.setStatus(entity.getStatus());
        dto.setJoiningDate(entity.getJoiningDate());
        dto.setAddress(entity.getAddress());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public void updateEntity(Worker entity, WorkerDto.WorkerRequest request, Farm farm, WorkerType workerType, UnitOfMeasure wageUom) {
        entity.setFarm(farm);
        entity.setWorkerType(workerType);
        entity.setWageUom(wageUom);
        entity.setName(request.getName().trim());
        entity.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        entity.setIdCardNumber(request.getIdCardNumber() != null ? request.getIdCardNumber().trim() : null);
        entity.setDailyWageRate(request.getDailyWageRate());
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus().toUpperCase().trim());
        }
        entity.setJoiningDate(request.getJoiningDate());
        entity.setAddress(request.getAddress());
        entity.setNotes(request.getNotes());
    }
}
