package com.harvesttracker.features.masterdata.mapper;

import com.harvesttracker.features.masterdata.domain.CropVariant;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.dto.CropVariantDto;
import org.springframework.stereotype.Component;

@Component
public class CropVariantMapper {

    public CropVariant toEntity(CropVariantDto.CropVariantRequest request, FruitType fruitType) {
        if (request == null) {
            return null;
        }
        CropVariant cropVariant = new CropVariant();
        cropVariant.setFruitType(fruitType);
        cropVariant.setName(request.getName());
        cropVariant.setCode(request.getCode());
        cropVariant.setDescription(request.getDescription());
        cropVariant.setIsActive(request.getIsActive());
        return cropVariant;
    }

    public void updateEntity(CropVariant cropVariant, CropVariantDto.CropVariantRequest request, FruitType fruitType) {
        if (cropVariant == null || request == null) {
            return;
        }
        if (fruitType != null) {
            cropVariant.setFruitType(fruitType);
        }
        cropVariant.setName(request.getName());
        cropVariant.setCode(request.getCode());
        cropVariant.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            cropVariant.setIsActive(request.getIsActive());
        }
    }

    public CropVariantDto.CropVariantResponse toResponse(CropVariant cropVariant) {
        if (cropVariant == null) {
            return null;
        }
        FruitType fruitType = cropVariant.getFruitType();
        return new CropVariantDto.CropVariantResponse(
                cropVariant.getId(),
                fruitType != null ? fruitType.getId() : null,
                fruitType != null ? fruitType.getName() : null,
                fruitType != null ? fruitType.getCode() : null,
                cropVariant.getName(),
                cropVariant.getCode(),
                cropVariant.getDescription(),
                cropVariant.getIsActive(),
                cropVariant.getCreatedAt(),
                cropVariant.getUpdatedAt()
        );
    }
}
