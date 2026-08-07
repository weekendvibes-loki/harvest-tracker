package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.masterdata.domain.PaymentMethod;
import com.harvesttracker.features.masterdata.dto.PaymentMethodDto;
import com.harvesttracker.features.masterdata.mapper.PaymentMethodMapper;
import com.harvesttracker.features.masterdata.repository.PaymentMethodRepository;
import com.harvesttracker.features.masterdata.repository.spec.MasterDataSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository repository;
    private final PaymentMethodMapper mapper;

    public PaymentMethodServiceImpl(PaymentMethodRepository repository, PaymentMethodMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PagedResponse<PaymentMethodDto.PaymentMethodResponse> getAllPaymentMethods(
            int page, int size, String sort, String direction, String search, Boolean isActive) {

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : "sortOrder";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Specification<PaymentMethod> spec = MasterDataSpecification.paymentMethodSpec(search, isActive);
        Page<PaymentMethod> pageResult = repository.findAll(spec, pageable);

        List<PaymentMethodDto.PaymentMethodResponse> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedResponse.of(content, pageResult);
    }

    @Override
    public PaymentMethodDto.PaymentMethodResponse getPaymentMethodById(Long id) {
        PaymentMethod paymentMethod = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + id));
        return mapper.toResponse(paymentMethod);
    }

    @Override
    @Transactional
    public PaymentMethodDto.PaymentMethodResponse createPaymentMethod(PaymentMethodDto.PaymentMethodRequest request) {
        validateUnique(request.getName(), request.getCode(), null);

        PaymentMethod paymentMethod = mapper.toEntity(request);
        PaymentMethod saved = repository.save(paymentMethod);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public PaymentMethodDto.PaymentMethodResponse updatePaymentMethod(Long id, PaymentMethodDto.PaymentMethodRequest request) {
        PaymentMethod paymentMethod = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + id));

        validateUnique(request.getName(), request.getCode(), id);

        mapper.updateEntity(paymentMethod, request);
        PaymentMethod updated = repository.save(paymentMethod);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public PaymentMethodDto.PaymentMethodResponse toggleStatus(Long id, boolean isActive) {
        PaymentMethod paymentMethod = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + id));

        paymentMethod.setIsActive(isActive);
        PaymentMethod updated = repository.save(paymentMethod);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deletePaymentMethod(Long id) {
        PaymentMethod paymentMethod = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with ID: " + id));

        paymentMethod.setDeletedAt(OffsetDateTime.now());
        paymentMethod.setIsActive(false);
        repository.save(paymentMethod);
    }

    private void validateUnique(String name, String code, Long id) {
        if (id == null) {
            if (repository.existsByNameIgnoreCaseAndDeletedAtIsNull(name)) {
                throw new DuplicateResourceException("Payment method with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndDeletedAtIsNull(code)) {
                throw new DuplicateResourceException("Payment method with code '" + code + "' already exists");
            }
        } else {
            if (repository.existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(name, id)) {
                throw new DuplicateResourceException("Payment method with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(code, id)) {
                throw new DuplicateResourceException("Payment method with code '" + code + "' already exists");
            }
        }
    }
}
