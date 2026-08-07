package com.harvesttracker.features.sales.mapper;

import com.harvesttracker.features.sales.domain.Customer;
import com.harvesttracker.features.sales.dto.CustomerDto;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public CustomerDto.CustomerResponse toResponse(Customer entity) {
        if (entity == null) {
            return null;
        }
        CustomerDto.CustomerResponse dto = new CustomerDto.CustomerResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setCustomerType(entity.getCustomerType());
        dto.setStatus(entity.getStatus());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public void updateEntity(Customer entity, CustomerDto.CustomerRequest request) {
        entity.setName(request.getName().trim());
        entity.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        entity.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        entity.setAddress(request.getAddress());
        if (request.getCustomerType() != null) {
            entity.setCustomerType(request.getCustomerType().toUpperCase().trim());
        }
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus().toUpperCase().trim());
        }
        entity.setNotes(request.getNotes());
    }
}
