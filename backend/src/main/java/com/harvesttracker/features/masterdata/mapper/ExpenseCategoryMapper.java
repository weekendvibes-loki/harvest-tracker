package com.harvesttracker.features.masterdata.mapper;

import com.harvesttracker.features.masterdata.domain.ExpenseCategory;
import com.harvesttracker.features.masterdata.dto.ExpenseCategoryDto;
import org.springframework.stereotype.Component;

@Component
public class ExpenseCategoryMapper {

    public ExpenseCategory toEntity(ExpenseCategoryDto.ExpenseCategoryRequest request) {
        if (request == null) {
            return null;
        }
        ExpenseCategory category = new ExpenseCategory();
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setDescription(request.getDescription());
        category.setSortOrder(request.getSortOrder());
        category.setIsActive(request.getIsActive());
        return category;
    }

    public void updateEntity(ExpenseCategory category, ExpenseCategoryDto.ExpenseCategoryRequest request) {
        if (category == null || request == null) {
            return;
        }
        category.setName(request.getName());
        category.setCode(request.getCode());
        category.setDescription(request.getDescription());
        if (request.getSortOrder() != null) {
            category.setSortOrder(request.getSortOrder());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
    }

    public ExpenseCategoryDto.ExpenseCategoryResponse toResponse(ExpenseCategory category) {
        if (category == null) {
            return null;
        }
        return new ExpenseCategoryDto.ExpenseCategoryResponse(
                category.getId(),
                category.getName(),
                category.getCode(),
                category.getDescription(),
                category.getSortOrder(),
                category.getIsActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
