package com.harvesttracker.features.harvest.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.harvest.dto.HarvestDto;
import com.harvesttracker.features.harvest.dto.HarvestSummaryDto;
import com.harvesttracker.features.harvest.dto.HarvestWorkerDto;

import java.time.LocalDate;

public interface HarvestService {

    PagedResponse<HarvestDto.HarvestResponse> getAllHarvests(
            int page, int size, String sort, String direction,
            Long farmId, Long seasonId, Long fruitTypeId, Long cropVariantId,
            String qualityGrade, String status, LocalDate startDate, LocalDate endDate,
            Long createdBy, Long supervisorId, String search, Boolean isActive);

    HarvestDto.HarvestResponse getHarvestById(Long id);

    HarvestDto.HarvestResponse createHarvest(HarvestDto.HarvestRequest request);

    HarvestDto.HarvestResponse updateHarvest(Long id, HarvestDto.HarvestRequest request);

    HarvestDto.HarvestResponse updateStatus(Long id, String status);

    void deleteHarvest(Long id);

    HarvestWorkerDto.HarvestWorkerResponse assignWorker(Long harvestId, HarvestWorkerDto.HarvestWorkerRequest request);

    void removeWorker(Long harvestId, Long workerId);

    HarvestSummaryDto.HarvestSummaryResponse getHarvestSummary();
}
