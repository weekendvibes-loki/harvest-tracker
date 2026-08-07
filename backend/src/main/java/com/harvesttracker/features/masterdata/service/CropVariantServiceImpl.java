package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.masterdata.domain.CropVariant;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.dto.CropVariantDto;
import com.harvesttracker.features.masterdata.mapper.CropVariantMapper;
import com.harvesttracker.features.masterdata.repository.CropVariantRepository;
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
public class CropVariantServiceImpl implements CropVariantService {

    private final CropVariantRepository repository;
    private final FruitTypeRepository fruitTypeRepository;
    private final CropVariantMapper mapper;

    public CropVariantServiceImpl(CropVariantRepository repository, FruitTypeRepository fruitTypeRepository, CropVariantMapper mapper) {
        this.repository = repository;
        this.fruitTypeRepository = fruitTypeRepository;
        this.mapper = mapper;
    }

    @Override
    public PagedResponse<CropVariantDto.CropVariantResponse> getAllCropVariants(
            int page, int size, String sort, String direction, Long fruitTypeId, String search, Boolean isActive) {

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortProperty = (sort != null && !sort.trim().isEmpty()) ? sort.trim() : "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortProperty));

        Specification<CropVariant> spec = MasterDataSpecification.cropVariantSpec(fruitTypeId, search, isActive);
        Page<CropVariant> pageResult = repository.findAll(spec, pageable);

        List<CropVariantDto.CropVariantResponse> content = pageResult.getContent().stream()
                .map(mapper::toResponse)
                .toList();

        return PagedResponse.of(content, pageResult);
    }

    @Override
    public CropVariantDto.CropVariantResponse getCropVariantById(Long id) {
        CropVariant cropVariant = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with ID: " + id));
        return mapper.toResponse(cropVariant);
    }

    @Override
    @Transactional
    public CropVariantDto.CropVariantResponse createCropVariant(CropVariantDto.CropVariantRequest request) {
        FruitType fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with ID: " + request.getFruitTypeId()));

        validateUnique(request.getFruitTypeId(), request.getName(), request.getCode(), null);

        CropVariant cropVariant = mapper.toEntity(request, fruitType);
        CropVariant saved = repository.save(cropVariant);
        return mapper.toResponse(saved);
    }

    @Override
    @Transactional
    public CropVariantDto.CropVariantResponse updateCropVariant(Long id, CropVariantDto.CropVariantRequest request) {
        CropVariant cropVariant = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with ID: " + id));

        FruitType fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with ID: " + request.getFruitTypeId()));

        validateUnique(request.getFruitTypeId(), request.getName(), request.getCode(), id);

        mapper.updateEntity(cropVariant, request, fruitType);
        CropVariant updated = repository.save(cropVariant);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public CropVariantDto.CropVariantResponse toggleStatus(Long id, boolean isActive) {
        CropVariant cropVariant = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with ID: " + id));

        cropVariant.setIsActive(isActive);
        CropVariant updated = repository.save(cropVariant);
        return mapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCropVariant(Long id) {
        CropVariant cropVariant = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with ID: " + id));

        cropVariant.setDeletedAt(OffsetDateTime.now());
        cropVariant.setIsActive(false);
        repository.save(cropVariant);
    }

    private void validateUnique(Long fruitTypeId, String name, String code, Long id) {
        if (id == null) {
            if (repository.existsByFruitTypeIdAndNameIgnoreCaseAndDeletedAtIsNull(fruitTypeId, name)) {
                throw new DuplicateResourceException("Crop variant with name '" + name + "' already exists for this fruit type");
            }
            if (repository.existsByFruitTypeIdAndCodeIgnoreCaseAndDeletedAtIsNull(fruitTypeId, code)) {
                throw new DuplicateResourceException("Crop variant with code '" + code + "' already exists for this fruit type");
            }
        } else {
            if (repository.existsByFruitTypeIdAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(fruitTypeId, name, id)) {
                throw new DuplicateResourceException("Crop variant with name '" + name + "' already exists for this fruit type");
            }
            if (repository.existsByFruitTypeIdAndCodeIgnoreCaseAndIdNotAndDeletedAtIsNull(fruitTypeId, code, id)) {
                throw new DuplicateResourceException("Crop variant with code '" + code + "' already exists for this fruit type");
            }
        }
    }
}
