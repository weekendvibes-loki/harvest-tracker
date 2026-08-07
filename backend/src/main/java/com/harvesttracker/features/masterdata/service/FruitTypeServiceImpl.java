package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.dto.FruitTypeDto;
import com.harvesttracker.features.masterdata.mapper.FruitTypeMapper;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
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
public class FruitTypeServiceImpl implements FruitTypeService {

    private final FruitTypeRepository repository;
    private final FruitTypeMapper mapper;

    public FruitTypeServiceImpl(FruitTypeRepository repository, FruitTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PagedResponse<FruitTypeDto.FruitTypeResponse> getAllFruitTypes(
            int page, int size, String sort, String direction, String search, Boolean isActive) {

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : "sortOrder";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Specification<FruitType> spec = MasterDataSpecification.fruitTypeSpec(search, isActive);
        Page<FruitType> fruitTypePage = repository.findAll(spec, pageable);

        List<FruitTypeDto.FruitTypeResponse> content = fruitTypePage.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedResponse.of(content, fruitTypePage);
    }

    @Override
    public FruitTypeDto.FruitTypeResponse getFruitTypeById(Long id) {
        FruitType fruitType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with ID: " + id));
        return mapper.toResponse(fruitType);
    }

    @Override
    @Transactional
    public FruitTypeDto.FruitTypeResponse createFruitType(FruitTypeDto.FruitTypeRequest request) {
        validateUnique(request.getName(), request.getCode(), null);

        FruitType fruitType = mapper.toEntity(request);
        FruitType saved = repository.save(fruitType);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public FruitTypeDto.FruitTypeResponse updateFruitType(Long id, FruitTypeDto.FruitTypeRequest request) {
        FruitType fruitType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with ID: " + id));

        validateUnique(request.getName(), request.getCode(), id);

        mapper.updateEntity(fruitType, request);
        FruitType updated = repository.save(fruitType);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public FruitTypeDto.FruitTypeResponse toggleStatus(Long id, boolean isActive) {
        FruitType fruitType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with ID: " + id));

        fruitType.setIsActive(isActive);
        FruitType updated = repository.save(fruitType);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteFruitType(Long id) {
        FruitType fruitType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with ID: " + id));

        fruitType.setDeletedAt(OffsetDateTime.now());
        fruitType.setIsActive(false);
        repository.save(fruitType);
    }

    private void validateUnique(String name, String code, Long id) {
        if (id == null) {
            if (repository.existsByNameIgnoreCaseAndDeletedAtIsNull(name)) {
                throw new DuplicateResourceException("Fruit type with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndDeletedAtIsNull(code)) {
                throw new DuplicateResourceException("Fruit type with code '" + code + "' already exists");
            }
        } else {
            if (repository.existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(name, id)) {
                throw new DuplicateResourceException("Fruit type with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(code, id)) {
                throw new DuplicateResourceException("Fruit type with code '" + code + "' already exists");
            }
        }
    }
}
