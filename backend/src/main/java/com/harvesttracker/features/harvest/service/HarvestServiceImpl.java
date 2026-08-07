package com.harvesttracker.features.harvest.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.farm.repository.SeasonRepository;
import com.harvesttracker.features.harvest.domain.HarvestRecord;
import com.harvesttracker.features.harvest.domain.HarvestWorker;
import com.harvesttracker.features.harvest.dto.HarvestDto;
import com.harvesttracker.features.harvest.dto.HarvestSummaryDto;
import com.harvesttracker.features.harvest.dto.HarvestWorkerDto;
import com.harvesttracker.features.harvest.mapper.HarvestMapper;
import com.harvesttracker.features.harvest.mapper.HarvestWorkerMapper;
import com.harvesttracker.features.harvest.repository.HarvestRecordRepository;
import com.harvesttracker.features.harvest.repository.HarvestWorkerRepository;
import com.harvesttracker.features.harvest.specification.HarvestSpecification;
import com.harvesttracker.features.masterdata.domain.CropVariant;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import com.harvesttracker.features.masterdata.repository.CropVariantRepository;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
import com.harvesttracker.features.masterdata.repository.UnitOfMeasureRepository;
import com.harvesttracker.features.worker.domain.Worker;
import com.harvesttracker.features.worker.repository.WorkerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class HarvestServiceImpl implements HarvestService {

    private final HarvestRecordRepository harvestRecordRepository;
    private final HarvestWorkerRepository harvestWorkerRepository;
    private final FarmRepository farmRepository;
    private final SeasonRepository seasonRepository;
    private final FruitTypeRepository fruitTypeRepository;
    private final CropVariantRepository cropVariantRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final HarvestMapper harvestMapper;
    private final HarvestWorkerMapper harvestWorkerMapper;

    public HarvestServiceImpl(
            HarvestRecordRepository harvestRecordRepository,
            HarvestWorkerRepository harvestWorkerRepository,
            FarmRepository farmRepository,
            SeasonRepository seasonRepository,
            FruitTypeRepository fruitTypeRepository,
            CropVariantRepository cropVariantRepository,
            UnitOfMeasureRepository unitOfMeasureRepository,
            UserRepository userRepository,
            WorkerRepository workerRepository,
            HarvestMapper harvestMapper,
            HarvestWorkerMapper harvestWorkerMapper) {
        this.harvestRecordRepository = harvestRecordRepository;
        this.harvestWorkerRepository = harvestWorkerRepository;
        this.farmRepository = farmRepository;
        this.seasonRepository = seasonRepository;
        this.fruitTypeRepository = fruitTypeRepository;
        this.cropVariantRepository = cropVariantRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.userRepository = userRepository;
        this.workerRepository = workerRepository;
        this.harvestMapper = harvestMapper;
        this.harvestWorkerMapper = harvestWorkerMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<HarvestDto.HarvestResponse> getAllHarvests(
            int page, int size, String sort, String direction,
            Long farmId, Long seasonId, Long fruitTypeId, Long cropVariantId,
            String qualityGrade, String status, LocalDate startDate, LocalDate endDate,
            Long createdBy, Long supervisorId, String search, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<HarvestRecord> spec = HarvestSpecification.filterHarvests(
                farmId, seasonId, fruitTypeId, cropVariantId, qualityGrade, status,
                startDate, endDate, createdBy, supervisorId, search, isActive);

        Page<HarvestRecord> harvestPage = harvestRecordRepository.findAll(spec, pageable);
        Page<HarvestDto.HarvestResponse> dtoPage = harvestPage.map(harvestMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), harvestPage);
    }

    @Override
    @Transactional(readOnly = true)
    public HarvestDto.HarvestResponse getHarvestById(Long id) {
        HarvestRecord harvest = harvestRecordRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + id));
        return harvestMapper.toResponse(harvest);
    }

    @Override
    public HarvestDto.HarvestResponse createHarvest(HarvestDto.HarvestRequest request) {
        Farm farm = validateAndGetFarm(request.getFarmId());
        Season season = validateAndGetSeason(request.getSeasonId(), request.getHarvestDate());
        FruitType fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with id: " + request.getFruitTypeId()));

        CropVariant cropVariant = null;
        if (request.getCropVariantId() != null) {
            cropVariant = cropVariantRepository.findByIdAndDeletedAtIsNull(request.getCropVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with id: " + request.getCropVariantId()));
        }

        UnitOfMeasure quantityUom = null;
        if (request.getQuantityUomId() != null) {
            quantityUom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getQuantityUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quantity unit of measure not found with id: " + request.getQuantityUomId()));
        }

        User supervisor = null;
        if (request.getSupervisorId() != null) {
            supervisor = userRepository.findByIdAndDeletedAtIsNull(request.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor user not found with id: " + request.getSupervisorId()));
        }

        HarvestRecord harvest = new HarvestRecord();
        harvestMapper.updateEntity(harvest, request, farm, season, fruitType, cropVariant, quantityUom, supervisor);

        HarvestRecord saved = harvestRecordRepository.save(harvest);
        return harvestMapper.toResponse(saved);
    }

    @Override
    public HarvestDto.HarvestResponse updateHarvest(Long id, HarvestDto.HarvestRequest request) {
        HarvestRecord harvest = harvestRecordRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + id));

        Farm farm = validateAndGetFarm(request.getFarmId());
        Season season = validateAndGetSeason(request.getSeasonId(), request.getHarvestDate());
        FruitType fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with id: " + request.getFruitTypeId()));

        CropVariant cropVariant = null;
        if (request.getCropVariantId() != null) {
            cropVariant = cropVariantRepository.findByIdAndDeletedAtIsNull(request.getCropVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Crop variant not found with id: " + request.getCropVariantId()));
        }

        UnitOfMeasure quantityUom = null;
        if (request.getQuantityUomId() != null) {
            quantityUom = unitOfMeasureRepository.findByIdAndDeletedAtIsNull(request.getQuantityUomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quantity unit of measure not found with id: " + request.getQuantityUomId()));
        }

        User supervisor = null;
        if (request.getSupervisorId() != null) {
            supervisor = userRepository.findByIdAndDeletedAtIsNull(request.getSupervisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supervisor user not found with id: " + request.getSupervisorId()));
        }

        harvestMapper.updateEntity(harvest, request, farm, season, fruitType, cropVariant, quantityUom, supervisor);

        HarvestRecord updated = harvestRecordRepository.save(harvest);
        return harvestMapper.toResponse(updated);
    }

    @Override
    public HarvestDto.HarvestResponse updateStatus(Long id, String newStatus) {
        HarvestRecord harvest = harvestRecordRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + id));

        String targetStatus = newStatus.toUpperCase().trim();
        validateStatusTransition(harvest.getStatus(), targetStatus);

        harvest.setStatus(targetStatus);
        HarvestRecord updated = harvestRecordRepository.save(harvest);
        return harvestMapper.toResponse(updated);
    }

    @Override
    public void deleteHarvest(Long id) {
        HarvestRecord harvest = harvestRecordRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + id));

        harvest.setDeletedAt(OffsetDateTime.now());
        harvest.setIsActive(false);
        harvestRecordRepository.save(harvest);
    }

    @Override
    public HarvestWorkerDto.HarvestWorkerResponse assignWorker(Long harvestId, HarvestWorkerDto.HarvestWorkerRequest request) {
        HarvestRecord harvest = harvestRecordRepository.findByIdAndDeletedAtIsNull(harvestId)
                .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + harvestId));

        Worker worker = workerRepository.findByIdAndDeletedAtIsNull(request.getWorkerId())
                .orElseThrow(() -> new ResourceNotFoundException("Worker not found with id: " + request.getWorkerId()));

        if (!worker.getIsActive() || "INACTIVE".equalsIgnoreCase(worker.getStatus())) {
            throw new IllegalArgumentException("Inactive worker cannot be assigned to harvest batch");
        }

        if (harvestWorkerRepository.existsByHarvestRecordIdAndWorkerIdAndDeletedAtIsNull(harvestId, request.getWorkerId())) {
            throw new DuplicateResourceException("Worker " + request.getWorkerId() + " is already assigned to harvest " + harvestId);
        }

        HarvestWorker harvestWorker = new HarvestWorker(harvest, worker, request.getRoleInHarvest(), request.getHoursWorked());
        HarvestWorker saved = harvestWorkerRepository.save(harvestWorker);

        return harvestWorkerMapper.toResponse(saved);
    }

    @Override
    public void removeWorker(Long harvestId, Long workerId) {
        if (!harvestRecordRepository.existsById(harvestId)) {
            throw new ResourceNotFoundException("Harvest record not found with id: " + harvestId);
        }

        HarvestWorker harvestWorker = harvestWorkerRepository.findByHarvestRecordIdAndWorkerIdAndDeletedAtIsNull(harvestId, workerId)
                .orElseThrow(() -> new ResourceNotFoundException("Worker assignment not found for worker " + workerId + " on harvest " + harvestId));

        harvestWorker.setDeletedAt(OffsetDateTime.now());
        harvestWorker.setIsActive(false);
        harvestWorkerRepository.save(harvestWorker);
    }

    @Override
    @Transactional(readOnly = true)
    public HarvestSummaryDto.HarvestSummaryResponse getHarvestSummary() {
        List<HarvestRecord> allHarvests = harvestRecordRepository.findAll((root, query, cb) -> cb.isNull(root.get("deletedAt")));

        HarvestSummaryDto.HarvestSummaryResponse summary = new HarvestSummaryDto.HarvestSummaryResponse();
        summary.setTotalHarvests(allHarvests.size());

        BigDecimal totalQty = allHarvests.stream()
                .map(HarvestRecord::getHarvestQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalQuantity(totalQty);

        summary.setDraftCount(allHarvests.stream().filter(h -> "DRAFT".equalsIgnoreCase(h.getStatus())).count());
        summary.setConfirmedCount(allHarvests.stream().filter(h -> "CONFIRMED".equalsIgnoreCase(h.getStatus())).count());
        summary.setStoredCount(allHarvests.stream().filter(h -> "STORED".equalsIgnoreCase(h.getStatus())).count());
        summary.setSoldCount(allHarvests.stream().filter(h -> "SOLD".equalsIgnoreCase(h.getStatus())).count());

        summary.setGradeACount(allHarvests.stream().filter(h -> "A".equalsIgnoreCase(h.getQualityGrade())).count());
        summary.setGradeBCount(allHarvests.stream().filter(h -> "B".equalsIgnoreCase(h.getQualityGrade())).count());
        summary.setGradeCCount(allHarvests.stream().filter(h -> "C".equalsIgnoreCase(h.getQualityGrade())).count());
        summary.setGradeRejectCount(allHarvests.stream().filter(h -> "REJECT".equalsIgnoreCase(h.getQualityGrade())).count());

        return summary;
    }

    private Farm validateAndGetFarm(Long farmId) {
        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + farmId));

        if (!farm.getIsActive() || !"ACTIVE".equalsIgnoreCase(farm.getStatus())) {
            throw new IllegalArgumentException("Cannot create or update harvest for an inactive farm");
        }
        return farm;
    }

    private Season validateAndGetSeason(Long seasonId, LocalDate harvestDate) {
        Season season = seasonRepository.findByIdAndDeletedAtIsNull(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + seasonId));

        if (harvestDate.isBefore(season.getStartDate()) || harvestDate.isAfter(season.getEndDate())) {
            throw new IllegalArgumentException("Harvest date " + harvestDate + " is outside season date range (" +
                    season.getStartDate() + " to " + season.getEndDate() + ")");
        }
        return season;
    }

    private void validateStatusTransition(String currentStatus, String targetStatus) {
        if (currentStatus.equalsIgnoreCase(targetStatus)) {
            return;
        }

        if ("SOLD".equalsIgnoreCase(currentStatus)) {
            throw new IllegalArgumentException("Cannot change status of a harvest that has already been SOLD");
        }

        if ("DRAFT".equalsIgnoreCase(currentStatus)) {
            if (!List.of("CONFIRMED", "STORED", "SOLD").contains(targetStatus)) {
                throw new IllegalArgumentException("Invalid status transition from DRAFT to " + targetStatus);
            }
        } else if ("CONFIRMED".equalsIgnoreCase(currentStatus)) {
            if (!List.of("STORED", "SOLD").contains(targetStatus)) {
                throw new IllegalArgumentException("Invalid status transition from CONFIRMED to " + targetStatus);
            }
        } else if ("STORED".equalsIgnoreCase(currentStatus)) {
            if (!"SOLD".equals(targetStatus)) {
                throw new IllegalArgumentException("Invalid status transition from STORED to " + targetStatus);
            }
        }
    }
}
