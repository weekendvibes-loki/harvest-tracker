package com.harvesttracker.features.farm.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.farm.dto.SeasonDto;
import com.harvesttracker.features.farm.mapper.SeasonMapper;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.farm.repository.SeasonRepository;
import com.harvesttracker.features.farm.specification.SeasonSpecification;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Transactional
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;
    private final FarmRepository farmRepository;
    private final FruitTypeRepository fruitTypeRepository;
    private final SeasonMapper seasonMapper;

    public SeasonServiceImpl(
            SeasonRepository seasonRepository,
            FarmRepository farmRepository,
            FruitTypeRepository fruitTypeRepository,
            SeasonMapper seasonMapper) {
        this.seasonRepository = seasonRepository;
        this.farmRepository = farmRepository;
        this.fruitTypeRepository = fruitTypeRepository;
        this.seasonMapper = seasonMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<SeasonDto.SeasonResponse> getAllSeasons(
            int page, int size, String sort, String direction,
            Long farmId, Long fruitTypeId, Integer year, String status,
            String search, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Season> spec = SeasonSpecification.filterSeasons(
                farmId, fruitTypeId, year, status, search, isActive);

        Page<Season> seasonPage = seasonRepository.findAll(spec, pageable);
        Page<SeasonDto.SeasonResponse> dtoPage = seasonPage.map(seasonMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), seasonPage);
    }

    @Override
    @Transactional(readOnly = true)
    public SeasonDto.SeasonResponse getSeasonById(Long id) {
        Season season = seasonRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + id));
        return seasonMapper.toResponse(season);
    }

    @Override
    public SeasonDto.SeasonResponse createSeason(SeasonDto.SeasonRequest request) {
        validateSeasonDates(request);

        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(request.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + request.getFarmId()));

        FruitType fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with id: " + request.getFruitTypeId()));

        String name = request.getName().trim();
        if (seasonRepository.existsByFarmIdAndFruitTypeIdAndYearAndNameIgnoreCaseAndDeletedAtIsNull(
                request.getFarmId(), request.getFruitTypeId(), request.getYear(), name)) {
            throw new DuplicateResourceException("Season with name '" + name + "' already exists for farm " + request.getFarmId() + " and fruit type " + request.getFruitTypeId() + " in year " + request.getYear());
        }

        Season season = new Season();
        season.setFarm(farm);
        season.setFruitType(fruitType);
        season.setName(name);
        season.setYear(request.getYear());
        season.setStartDate(request.getStartDate());
        season.setEndDate(request.getEndDate());
        season.setStatus(request.getStatus() != null ? request.getStatus().toUpperCase().trim() : "PLANNED");
        season.setNotes(request.getNotes());

        Season savedSeason = seasonRepository.save(season);
        return seasonMapper.toResponse(savedSeason);
    }

    @Override
    public SeasonDto.SeasonResponse updateSeason(Long id, SeasonDto.SeasonRequest request) {
        Season season = seasonRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + id));

        validateSeasonDates(request);

        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(request.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + request.getFarmId()));

        FruitType fruitType = fruitTypeRepository.findByIdAndDeletedAtIsNull(request.getFruitTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Fruit type not found with id: " + request.getFruitTypeId()));

        String name = request.getName().trim();
        if (seasonRepository.existsByFarmIdAndFruitTypeIdAndYearAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(
                request.getFarmId(), request.getFruitTypeId(), request.getYear(), name, id)) {
            throw new DuplicateResourceException("Season with name '" + name + "' already exists for farm " + request.getFarmId() + " and fruit type " + request.getFruitTypeId() + " in year " + request.getYear());
        }

        season.setFarm(farm);
        season.setFruitType(fruitType);
        season.setName(name);
        season.setYear(request.getYear());
        season.setStartDate(request.getStartDate());
        season.setEndDate(request.getEndDate());
        if (request.getStatus() != null) {
            season.setStatus(request.getStatus().toUpperCase().trim());
        }
        season.setNotes(request.getNotes());

        Season updatedSeason = seasonRepository.save(season);
        return seasonMapper.toResponse(updatedSeason);
    }

    @Override
    public void deleteSeason(Long id) {
        Season season = seasonRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + id));

        season.setDeletedAt(OffsetDateTime.now());
        season.setIsActive(false);
        seasonRepository.save(season);
    }

    private void validateSeasonDates(SeasonDto.SeasonRequest request) {
        if (request.getStartDate() != null && request.getEndDate() != null) {
            if (!request.getEndDate().isAfter(request.getStartDate())) {
                throw new IllegalArgumentException("Season end date must be strictly after start date");
            }
        }
    }
}
