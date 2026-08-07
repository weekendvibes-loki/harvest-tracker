package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.CropVariantDto;

public interface CropVariantService {

    PagedResponse<CropVariantDto.CropVariantResponse> getAllCropVariants(
            int page, int size, String sort, String direction, Long fruitTypeId, String search, Boolean isActive);

    CropVariantDto.CropVariantResponse getCropVariantById(Long id);

    CropVariantDto.CropVariantResponse createCropVariant(CropVariantDto.CropVariantRequest request);

    CropVariantDto.CropVariantResponse updateCropVariant(Long id, CropVariantDto.CropVariantRequest request);

    CropVariantDto.CropVariantResponse toggleStatus(Long id, boolean isActive);

    void deleteCropVariant(Long id);
}
