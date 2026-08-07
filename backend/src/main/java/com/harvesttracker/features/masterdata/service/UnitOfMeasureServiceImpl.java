package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import com.harvesttracker.features.masterdata.dto.UnitOfMeasureDto;
import com.harvesttracker.features.masterdata.mapper.UnitOfMeasureMapper;
import com.harvesttracker.features.masterdata.repository.UnitOfMeasureRepository;
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
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureRepository repository;
    private final UnitOfMeasureMapper mapper;

    public UnitOfMeasureServiceImpl(UnitOfMeasureRepository repository, UnitOfMeasureMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PagedResponse<UnitOfMeasureDto.UnitOfMeasureResponse> getAllUnitsOfMeasure(
            int page, int size, String sort, String direction, String measureType, String search, Boolean isActive) {

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : "sortOrder";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Specification<UnitOfMeasure> spec = MasterDataSpecification.unitOfMeasureSpec(measureType, search, isActive);
        Page<UnitOfMeasure> pageResult = repository.findAll(spec, pageable);

        List<UnitOfMeasureDto.UnitOfMeasureResponse> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedResponse.of(content, pageResult);
    }

    @Override
    public UnitOfMeasureDto.UnitOfMeasureResponse getUnitOfMeasureById(Long id) {
        UnitOfMeasure uom = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with ID: " + id));
        return mapper.toResponse(uom);
    }

    @Override
    @Transactional
    public UnitOfMeasureDto.UnitOfMeasureResponse createUnitOfMeasure(UnitOfMeasureDto.UnitOfMeasureRequest request) {
        validateUnique(request.getName(), request.getCode(), null);

        UnitOfMeasure uom = mapper.toEntity(request);
        UnitOfMeasure saved = repository.save(uom);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public UnitOfMeasureDto.UnitOfMeasureResponse updateUnitOfMeasure(Long id, UnitOfMeasureDto.UnitOfMeasureRequest request) {
        UnitOfMeasure uom = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with ID: " + id));

        validateUnique(request.getName(), request.getCode(), id);

        mapper.updateEntity(uom, request);
        UnitOfMeasure updated = repository.save(uom);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public UnitOfMeasureDto.UnitOfMeasureResponse toggleStatus(Long id, boolean isActive) {
        UnitOfMeasure uom = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with ID: " + id));

        uom.setIsActive(isActive);
        UnitOfMeasure updated = repository.save(uom);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUnitOfMeasure(Long id) {
        UnitOfMeasure uom = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with ID: " + id));

        uom.setDeletedAt(OffsetDateTime.now());
        uom.setIsActive(false);
        repository.save(uom);
    }

    private void validateUnique(String name, String code, Long id) {
        if (id == null) {
            if (repository.existsByNameIgnoreCaseAndDeletedAtIsNull(name)) {
                throw new DuplicateResourceException("Unit of measure with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndDeletedAtIsNull(code)) {
                throw new DuplicateResourceException("Unit of measure with code '" + code + "' already exists");
            }
        } else {
            if (repository.existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(name, id)) {
                throw new DuplicateResourceException("Unit of measure with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(code, id)) {
                throw new DuplicateResourceException("Unit of measure with code '" + code + "' already exists");
            }
        }
    }
}
