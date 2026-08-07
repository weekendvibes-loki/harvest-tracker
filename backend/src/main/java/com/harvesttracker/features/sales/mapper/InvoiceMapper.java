package com.harvesttracker.features.sales.mapper;

import com.harvesttracker.features.sales.domain.Invoice;
import com.harvesttracker.features.sales.dto.InvoiceDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class InvoiceMapper {

    private final PaymentMapper paymentMapper;

    public InvoiceMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }

    public InvoiceDto.InvoiceResponse toResponse(Invoice entity) {
        if (entity == null) {
            return null;
        }
        InvoiceDto.InvoiceResponse dto = new InvoiceDto.InvoiceResponse();
        dto.setId(entity.getId());
        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            dto.setCustomerName(entity.getCustomer().getName());
        }
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setIssuedAt(entity.getIssuedAt());
        dto.setDueDate(entity.getDueDate());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setInvoiceStatus(entity.getInvoiceStatus());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getPayments() != null) {
            dto.setPayments(entity.getPayments().stream()
                    .filter(p -> p.getDeletedAt() == null)
                    .map(paymentMapper::toResponse)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
