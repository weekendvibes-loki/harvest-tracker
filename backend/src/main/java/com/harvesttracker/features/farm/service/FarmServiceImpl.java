package com.harvesttracker.features.farm.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.FarmFruitType;
import com.harvesttracker.features.farm.dto.FarmDto;
import com.harvesttracker.features.farm.dto.FarmFruitTypeDto;
import com.harvesttracker.features.farm.mapper.FarmFruitTypeMapper;
import com.harvesttracker.features.farm.mapper.FarmMapper;
import com.harvesttracker.features.farm.repository.FarmFruitTypeRepository;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.farm.specification.FarmSpecification;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
import com.harvesttracker.features.masterdata.repository.UnitOfMeasureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final FarmFruitTypeRepository farmFruitTypeRepository;
    private final UserRepository userRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final FruitTypeRepository fruitTypeRepository;
    private final FarmMapper farmMapper;
    private final FarmFruitTypeMapper farmFruitTypeMapper;

    public FarmServiceImpl(
            FarmRepository farmRepository,
            FarmFruitTypeRepository farmFruitTypeRepository,
            UserRepository userRepository,
            UnitOfMeasureRepository unitOfMeasureRepository,
            FruitTypeRepository fruitTypeRepository,
            FarmMapper farmMapper,
            FarmFruitTypeMapper farmFruitTypeMapper) {
        this.farmRepository = farmRepository;
        this.farmFruitTypeRepository = farmFruitTypeRepository;
        this.userRepository = userRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.fruitTypeRepository = fruitTypeRepository;
        this.farmMapper = farmMapper;
        this.farmFruitTypeMapper = farmFruitTypeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<FarmDto.FarmResponse> getAllFarms(
            int page, int size, String sort, String direction,
            String search, String name, String village, String district, String state,
            String ownerName, Long ownerId, String ownershipType, String status,
            Boolean isActive, Long fruitTypeId) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Farm> spec = FarmSpecification.filterFarms(
                search, name, village, district, state, ownerName, ownerId,
                ownershipType, status, isActive, fruitTypeId);

        Page<Farm> farmPage = farmRepository.findAll(spec, pageable);
        Page<FarmDto.FarmResponse> dtoPage = farmPage.map(farmMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), farmPage);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmDto.FarmResponse getFarmById(Long id) {
        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + id));
        return farmMapper.toResponse(farm);
    }

    @Override
    public FarmDto.FarmResponse createFarm(FarmDto.FarmRequest request) {
        validateFarmRequest(request, null);

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found with id: " + request.getOwnerId()));

        UnitOfMeasure landUom = null;
        if (request.getLandUomId() != null) {
            landUom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getLandUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with id: " + request.getLandUomId()));
        }

        Farm farm = new Farm();
        farmMapper.updateEntity(farm, request, owner, landUom);

        Farm savedFarm = farmRepository.save(farm);
        return farmMapper.toResponse(savedFarm);
    }

    @Override
    public FarmDto.FarmResponse updateFarm(Long id, FarmDto.FarmRequest request) {
        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + id));

        validateFarmRequest(request, id);

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner user not found with id: " + request.getOwnerId()));

        UnitOfMeasure landUom = null;
        if (request.getLandUomId() != null) {
            landUom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getLandUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit of measure not found with id: " + request.getLandUomId()));
        }

        farmMapper.updateEntity(farm, request, owner, landUom);

        Farm updatedFarm = farmRepository.save(farm);
        return farmMapper.toResponse(updatedFarm);
    }

    @Override
    public FarmDto.FarmResponse toggleStatus(Long id, boolean isActive) {
        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + id));

        farm.setIsActive(isActive);
        farm.setStatus(isActive ? "ACTIVE" : "INACTIVE");
        Farm updatedFarm = farmRepository.save(farm);
        return farmMapper.toResponse(updatedFarm);
    }

    @Override
    public void deleteFarm(Long id) {
        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + id));

        OffsetDateTime now = OffsetDateTime.now();
        farm.setDeletedAt(now);
        farm.setIsActive(false);
        farm.setStatus("INACTIVE");

        if (farm.getFarmFruitTypes() != null) {
            farm.getFarmFruitTypes().forEach(fft -> {
                fft.setDeletedAt(now);
                fft.setIsActive(false);
            });
        }

        farmRepository.save(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmFruitTypeDto.FarmFruitTypeResponse> getFarmFruitTypes(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Farm not found with id: " + farmId);
        }
        return farmFruitTypeRepository.findByFarmIdAndDeletedAtIsNull(farmId).stream()
                .map(farmFruitTypeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FarmFruitTypeDto.FarmFruitTypeResponse addFruitTypeToFarm(Long farmId, FarmFruitTypeDto.FarmFruitTypeRequest request) {
        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        FruitType fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with id: " + request.getFruitTypeId()));

        if (farmFruitTypeRepository.existsByFarmIdAndFruitTypeIdAndDeletedAtIsNull(farmId, request.getFruitTypeId())) {
            throw new DuplicateResourceException("Fruit type with id " + request.getFruitTypeId() + " is already associated with farm " + farmId);
        }

        FarmFruitType farmFruitType = new FarmFruitType();
        farmFruitType.setFarm(farm);
        farmFruitType.setFruitType(fruitType);
        farmFruitType.setIsPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : false);
        farmFruitType.setFirstPlantedDate(request.getFirstPlantedDate());

        FarmFruitType saved = farmFruitTypeRepository.save(farmFruitType);
        return farmFruitTypeMapper.toResponse(saved);
    }

    @Override
    public void removeFruitTypeFromFarm(Long farmId, Long fruitTypeId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Farm not found with id: " + farmId);
        }

        FarmFruitType farmFruitType = farmFruitTypeRepository.findByFarmIdAndFruitTypeIdAndDeletedAtIsNull(farmId, fruitTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type association not found for farm: " + farmId + " and fruitTypeId: " + fruitTypeId));

        farmFruitType.setDeletedAt(OffsetDateTime.now());
        farmFruitType.setIsActive(false);
        farmFruitTypeRepository.save(farmFruitType);
    }

    private void validateFarmRequest(FarmDto.FarmRequest request, Long existingId) {
        String name = request.getName().trim();

        if (existingId == null) {
            if (farmRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(name)) {
                throw new DuplicateResourceException("Farm with name '" + name + "' already exists");
            }
        } else {
            if (farmRepository.existsByNameIgnoreCaseAndIdNotAndDeletedAtIsNull(name, existingId)) {
                throw new DuplicateResourceException("Farm with name '" + name + "' already exists");
            }
        }

        if ("LEASED".equalsIgnoreCase(request.getOwnershipType()) && request.getLeaseStartDate() == null) {
            throw new IllegalArgumentException("Lease start date is required for LEASED farms");
        }

        if (request.getLeaseStartDate() != null && request.getLeaseEndDate() != null) {
            if (request.getLeaseEndDate().isBefore(request.getLeaseStartDate())) {
                throw new IllegalArgumentException("Lease end date must be after lease start date");
            }
        }
    }
}
