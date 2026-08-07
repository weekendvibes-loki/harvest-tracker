package com.harvesttracker.features.farm.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.farm.dto.SeasonDto;

public interface SeasonService {

    PagedResponse<SeasonDto.SeasonResponse> getAllSeasons(
            int page, int size, String sort, String direction,
            Long farmId, Long fruitTypeId, Integer year, String status,
            String search, Boolean isActive);

    SeasonDto.SeasonResponse getSeasonById(Long id);

    SeasonDto.SeasonResponse createSeason(SeasonDto.SeasonRequest request);

    SeasonDto.SeasonResponse updateSeason(Long id, SeasonDto.SeasonRequest request);

    void deleteSeason(Long id);
}
