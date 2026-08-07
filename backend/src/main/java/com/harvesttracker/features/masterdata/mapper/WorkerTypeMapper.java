package com.harvesttracker.features.masterdata.mapper;

import com.harvesttracker.features.masterdata.domain.WorkerType;
import com.harvesttracker.features.masterdata.dto.WorkerTypeDto;
import org.springframework.stereotype.Component;

@Component
public class WorkerTypeMapper {

    public WorkerType toEntity(WorkerTypeDto.WorkerTypeRequest request) {
        if (request == null) {
            return null;
        }
        WorkerType workerType = new WorkerType();
        workerType.setName(request.getName());
        workerType.setCode(request.getCode());
        workerType.setDescription(request.getDescription());
        workerType.setSortOrder(request.getSortOrder());
        workerType.setIsActive(request.getIsActive());
        return workerType;
    }

    public void updateEntity(WorkerType workerType, WorkerTypeDto.WorkerTypeRequest request) {
        if (workerType == null || request == null) {
            return;
        }
        workerType.setName(request.getName());
        workerType.setCode(request.getCode());
        workerType.setDescription(request.getDescription());
        if (request.getSortOrder() != null) {
            workerType.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            workerType.setIsActive(request.getIsActive());
        }
    }

    public WorkerTypeDto.WorkerTypeResponse toResponse(WorkerType workerType) {
        if (workerType == null) {
            return null;
        }
        return new WorkerTypeDto.WorkerTypeResponse(
                workerType.getId(),
                workerType.getName(),
                workerType.getCode(),
                workerType.getDescription(),
                workerType.getSortOrder(),
                workerType.getIsActive(),
                workerType.getCreatedAt(),
                workerType.getUpdatedAt()
        );
    }
}
