package com.harvesttracker.features.sales.mapper;

import com.harvesttracker.features.sales.domain.OrderItem;
import com.harvesttracker.features.sales.dto.OrderItemDto;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemDto.OrderItemResponse toResponse(OrderItem entity) {
        if (entity == null) {
            return null;
        }
        OrderItemDto.OrderItemResponse dto = new OrderItemDto.OrderItemResponse();
        dto.setId(entity.getId());
        if (entity.getOrder() != null) {
            dto.setOrderId(entity.getOrder().getId());
        }
        if (entity.getHarvestRecord() != null) {
            dto.setHarvestRecordId(entity.getHarvestRecord().getId());
        }
        if (entity.getFruitType() != null) {
            dto.setFruitTypeId(entity.getFruitType().getId());
            dto.setFruitTypeName(entity.getFruitType().getName());
        }
        if (entity.getCropVariant() != null) {
            dto.setCropVariantId(entity.getCropVariant().getId());
            dto.setCropVariantName(entity.getCropVariant().getName());
        }
        if (entity.getQuantityUom() != null) {
            dto.setQuantityUomId(entity.getQuantityUom().getId());
            dto.setQuantityUomCode(entity.getQuantityUom().getCode());
        }
        dto.setQuantity(entity.getQuantity());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setLineTotal(entity.getLineTotal());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
