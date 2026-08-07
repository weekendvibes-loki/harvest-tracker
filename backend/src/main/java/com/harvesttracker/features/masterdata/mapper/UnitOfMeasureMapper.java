package com.harvesttracker.features.masterdata.mapper;

import com.harvesttracker.features.masterdata.domain.UnitOfMeasure;
import com.harvesttracker.features.masterdata.dto.UnitOfMeasureDto;
import org.springframework.stereotype.Component;

@Component
public class UnitOfMeasureMapper {

    public UnitOfMeasure toEntity(UnitOfMeasureDto.UnitOfMeasureRequest request) {
        if (request == null) {
            return null;
        }
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setName(request.getName());
        uom.setCode(request.getCode());
        uom.setMeasureType(request.getMeasureType());
        uom.setSortOrder(request.getSortOrder());
        uom.setIsActive(request.getIsActive());
        return uom;
    }

    public void updateEntity(UnitOfMeasure uom, UnitOfMeasureDto.UnitOfMeasureRequest request) {
        if (uom == null || request == null) {
            return;
        }
        uom.setName(request.getName());
        uom.setCode(request.getCode());
        uom.setMeasureType(request.getMeasureType());
        if (request.getSortOrder() != null) {
            uom.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            uom.setIsActive(request.getIsActive());
        }
    }

    public UnitOfMeasureDto.UnitOfMeasureResponse toResponse(UnitOfMeasure uom) {
        if (uom == null) {
            return null;
        }
        return new UnitOfMeasureDto.UnitOfMeasureResponse(
                uom.getId(),
                uom.getName(),
                uom.getCode(),
                uom.getMeasureType(),
                uom.getSortOrder(),
                uom.getIsActive(),
                uom.getCreatedAt(),
                uom.getUpdatedAt()
        );
    }
}
