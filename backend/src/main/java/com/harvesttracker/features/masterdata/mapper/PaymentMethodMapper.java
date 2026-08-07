package com.harvesttracker.features.masterdata.mapper;

import com.harvesttracker.features.masterdata.domain.PaymentMethod;
import com.harvesttracker.features.masterdata.dto.PaymentMethodDto;
import org.springframework.stereotype.Component;

@Component
public class PaymentMethodMapper {

    public PaymentMethod toEntity(PaymentMethodDto.PaymentMethodRequest request) {
        if (request == null) {
            return null;
        }
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(request.getName());
        paymentMethod.setCode(request.getCode());
        paymentMethod.setSortOrder(request.getSortOrder());
        paymentMethod.setIsActive(request.getIsActive());
        return paymentMethod;
    }

    public void updateEntity(PaymentMethod paymentMethod, PaymentMethodDto.PaymentMethodRequest request) {
        if (paymentMethod == null || request == null) {
            return;
        }
        paymentMethod.setName(request.getName());
        paymentMethod.setCode(request.getCode());
        if (request.getSortOrder() != null) {
            paymentMethod.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            paymentMethod.setIsActive(request.getIsActive());
        }
    }

    public PaymentMethodDto.PaymentMethodResponse toResponse(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return null;
        }
        return new PaymentMethodDto.PaymentMethodResponse(
                paymentMethod.getId(),
                paymentMethod.getName(),
                paymentMethod.getCode(),
                paymentMethod.getSortOrder(),
                paymentMethod.getIsActive(),
                paymentMethod.getCreatedAt(),
                paymentMethod.getUpdatedAt()
        );
    }
}
