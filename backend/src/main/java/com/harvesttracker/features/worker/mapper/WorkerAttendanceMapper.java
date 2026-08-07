package com.harvesttracker.features.worker.mapper;

import com.harvesttracker.features.worker.domain.WorkerAttendance;
import com.harvesttracker.features.worker.dto.WorkerAttendanceDto;
import org.springframework.stereotype.Component;

@Component
public class WorkerAttendanceMapper {

    public WorkerAttendanceDto.AttendanceResponse toResponse(WorkerAttendance entity) {
        if (entity == null) {
            return null;
        }
        WorkerAttendanceDto.AttendanceResponse dto = new WorkerAttendanceDto.AttendanceResponse();
        dto.setId(entity.getId());
        if (entity.getWorker() != null) {
            dto.setWorkerId(entity.getWorker().getId());
            dto.setWorkerName(entity.getWorker().getName());
        }
        dto.setHarvestRecordId(entity.getHarvestRecordId());
        dto.setAttendanceDate(entity.getAttendanceDate());
        dto.setIsPresent(entity.getIsPresent());
        dto.setHoursWorked(entity.getHoursWorked());
        dto.setRemarks(entity.getRemarks());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
