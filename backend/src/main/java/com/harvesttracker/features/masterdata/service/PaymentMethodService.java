package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.PaymentMethodDto;

public interface PaymentMethodService {

    PagedResponse<PaymentMethodDto.PaymentMethodResponse> getAllPaymentMethods(
            int page, int size, String sort, String direction, String search, Boolean isActive);

    PaymentMethodDto.PaymentMethodResponse getPaymentMethodById(Long id);

    PaymentMethodDto.PaymentMethodResponse createPaymentMethod(PaymentMethodDto.PaymentMethodRequest request);

    PaymentMethodDto.PaymentMethodResponse updatePaymentMethod(Long id, PaymentMethodDto.PaymentMethodRequest request);

    PaymentMethodDto.PaymentMethodResponse toggleStatus(Long id, boolean isActive);

    void deletePaymentMethod(Long id);
}
