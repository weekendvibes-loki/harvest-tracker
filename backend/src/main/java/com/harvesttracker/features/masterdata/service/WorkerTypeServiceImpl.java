package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.masterdata.domain.WorkerType;
import com.harvesttracker.features.masterdata.dto.WorkerTypeDto;
import com.harvesttracker.features.masterdata.mapper.WorkerTypeMapper;
import com.harvesttracker.features.masterdata.repository.WorkerTypeRepository;
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
public class WorkerTypeServiceImpl implements WorkerTypeService {

    private final WorkerTypeRepository repository;
    private final WorkerTypeMapper mapper;

    public WorkerTypeServiceImpl(WorkerTypeRepository repository, WorkerTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public PagedResponse<WorkerTypeDto.WorkerTypeResponse> getAllWorkerTypes(
            int page, int size, String sort, String direction, String search, Boolean isActive) {

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : "sortOrder";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Specification<WorkerType> spec = MasterDataSpecification.workerTypeSpec(search, isActive);
        Page<WorkerType> pageResult = repository.findAll(spec, pageable);

        List<WorkerTypeDto.WorkerTypeResponse> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedResponse.of(content, pageResult);
    }

    @Override
    public WorkerTypeDto.WorkerTypeResponse getWorkerTypeById(Long id) {
        WorkerType workerType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker type not found with ID: " + id));
        return mapper.toResponse(workerType);
    }

    @Override
    @Transactional
    public WorkerTypeDto.WorkerTypeResponse createWorkerType(WorkerTypeDto.WorkerTypeRequest request) {
        validateUnique(request.getName(), request.getCode(), null);

        WorkerType workerType = mapper.toEntity(request);
        WorkerType saved = repository.save(workerType);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public WorkerTypeDto.WorkerTypeResponse updateWorkerType(Long id, WorkerTypeDto.WorkerTypeRequest request) {
        WorkerType workerType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker type not found with ID: " + id));

        validateUnique(request.getName(), request.getCode(), id);

        mapper.updateEntity(workerType, request);
        WorkerType updated = repository.save(workerType);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public WorkerTypeDto.WorkerTypeResponse toggleStatus(Long id, boolean isActive) {
        WorkerType workerType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker type not found with ID: " + id));

        workerType.setIsActive(isActive);
        WorkerType updated = repository.save(workerType);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteWorkerType(Long id) {
        WorkerType workerType = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Worker type not found with ID: " + id));

        workerType.setDeletedAt(OffsetDateTime.now());
        workerType.setIsActive(false);
        repository.save(workerType);
    }

    private void validateUnique(String name, String code, Long id) {
        if (id == null) {
            if (repository.existsByNameIgnoreCaseAndDeletedAtIsNull(name)) {
                throw new DuplicateResourceException("Worker type with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndDeletedAtIsNull(code)) {
                throw new DuplicateResourceException("Worker type with code '" + code + "' already exists");
            }
        } else {
            if (repository.existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(name, id)) {
                throw new DuplicateResourceException("Worker type with name '" + name + "' already exists");
            }
            if (repository.existsByCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(code, id)) {
                throw new DuplicateResourceException("Worker type with code '" + code + "' already exists");
            }
        }
    }
}
