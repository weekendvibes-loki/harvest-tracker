package com.harvesttracker.features.worker.mapper;

import com.harvesttracker.features.worker.domain.WorkerPayment;
import com.harvesttracker.features.worker.dto.WorkerPaymentDto;
import org.springframework.stereotype.Component;

@Component
public class WorkerPaymentMapper {

    public WorkerPaymentDto.PaymentResponse toResponse(WorkerPayment entity) {
        if (entity == null) {
            return null;
        }
        WorkerPaymentDto.PaymentResponse dto = new WorkerPaymentDto.PaymentResponse();
        dto.setId(entity.getId());
        if (entity.getWorker() != null) {
            dto.setWorkerId(entity.getWorker().getId());
            dto.setWorkerName(entity.getWorker().getName());
        }
        if (entity.getPaymentMethod() != null) {
            dto.setPaymentMethodId(entity.getPaymentMethod().getId());
            dto.setPaymentMethodName(entity.getPaymentMethod().getName());
        }
        dto.setPeriodStart(entity.getPeriodStart());
        dto.setPeriodEnd(entity.getPeriodEnd());
        dto.setTotalDaysWorked(entity.getTotalDaysWorked());
        dto.setDailyWageRate(entity.getDailyWageRate());
        dto.setAmount(entity.getAmount());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setPaidDate(entity.getPaidDate());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
