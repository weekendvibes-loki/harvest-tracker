package com.harvesttracker.features.sales.mapper;

import com.harvesttracker.features.sales.domain.Order;
import com.harvesttracker.features.sales.dto.OrderDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderMapper(OrderItemMapper orderItemMapper) {
        this.orderItemMapper = orderItemMapper;
    }

    public OrderDto.OrderResponse toResponse(Order entity) {
        if (entity == null) {
            return null;
        }
        OrderDto.OrderResponse dto = new OrderDto.OrderResponse();
        dto.setId(entity.getId());
        if (entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            dto.setCustomerName(entity.getCustomer().getName());
            dto.setCustomerPhone(entity.getCustomer().getPhone());
        }
        dto.setOrderDate(entity.getOrderDate());
        dto.setOrderStatus(entity.getOrderStatus());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getOrderItems() != null) {
            dto.setItems(entity.getOrderItems().stream()
                    .filter(item -> item.getDeletedAt() == null)
                    .map(orderItemMapper::toResponse)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
