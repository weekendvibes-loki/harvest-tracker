package com.harvesttracker.features.sales.mapper;

import com.harvesttracker.features.sales.domain.Payment;
import com.harvesttracker.features.sales.dto.PaymentDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentDto.PaymentResponse toResponse(Payment entity) {
        if (entity == null) {
            return null;
        }
        PaymentDto.PaymentResponse dto = new PaymentDto.PaymentResponse();
        dto.setId(entity.getId());
        if (entity.getInvoice() != null) {
            dto.setInvoiceId(entity.getInvoice().getId());
            dto.setInvoiceNumber(entity.getInvoice().getInvoiceNumber());
        }
        if (entity.getPaymentMethod() != null) {
            dto.setPaymentMethodId(entity.getPaymentMethod().getId());
            dto.setPaymentMethodName(entity.getPaymentMethod().getName());
        }
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setAmount(entity.getAmount());
        dto.setPaymentStatus(entity.getPaymentStatus());
        dto.setReferenceNumber(entity.getReferenceNumber());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
