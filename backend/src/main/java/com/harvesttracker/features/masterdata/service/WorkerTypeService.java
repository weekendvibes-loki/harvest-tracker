package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.WorkerTypeDto;

public interface WorkerTypeService {

    PagedResponse<WorkerTypeDto.WorkerTypeResponse> getAllWorkerTypes(
            int page, int size, String sort, String direction, String search, Boolean isActive);

    WorkerTypeDto.WorkerTypeResponse getWorkerTypeById(Long id);

    WorkerTypeDto.WorkerTypeResponse createWorkerType(WorkerTypeDto.WorkerTypeRequest request);

    WorkerTypeDto.WorkerTypeResponse updateWorkerType(Long id, WorkerTypeDto.WorkerTypeRequest request);

    WorkerTypeDto.WorkerTypeResponse toggleStatus(Long id, boolean isActive);

    void deleteWorkerType(Long id);
}
