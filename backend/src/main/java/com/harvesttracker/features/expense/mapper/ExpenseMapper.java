package com.harvesttracker.features.expense.mapper;

import com.harvesttracker.features.expense.domain.Expense;
import com.harvesttracker.features.expense.dto.ExpenseDto;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.harvest.domain.HarvestRecord;
import com.harvesttracker.features.masterdata.domain.ExpenseCategory;
import com.harvesttracker.features.masterdata.domain.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public class ExpenseMapper {

    public ExpenseDto.ExpenseResponse toResponse(Expense entity) {
        if (entity == null) {
            return null;
        }
        ExpenseDto.ExpenseResponse dto = new ExpenseDto.ExpenseResponse();
        dto.setId(entity.getId());

        if (entity.getFarm() != null) {
            dto.setFarmId(entity.getFarm().getId());
            dto.setFarmName(entity.getFarm().getName());
        }

        if (entity.getHarvestRecord() != null) {
            dto.setHarvestRecordId(entity.getHarvestRecord().getId());
        }

        if (entity.getExpenseCategory() != null) {
            dto.setExpenseCategoryId(entity.getExpenseCategory().getId());
            dto.setExpenseCategoryName(entity.getExpenseCategory().getName());
            dto.setExpenseCategoryCode(entity.getExpenseCategory().getCode());
        }

        if (entity.getPaymentMethod() != null) {
            dto.setPaymentMethodId(entity.getPaymentMethod().getId());
            dto.setPaymentMethodName(entity.getPaymentMethod().getName());
        }

        dto.setExpenseDate(entity.getExpenseDate());
        dto.setAmount(entity.getAmount());
        dto.setStatus(entity.getStatus());
        dto.setDescription(entity.getDescription());
        dto.setNotes(entity.getNotes());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    public void updateEntity(
            Expense entity,
            ExpenseDto.ExpenseRequest request,
            Farm farm,
            HarvestRecord harvestRecord,
            ExpenseCategory category,
            PaymentMethod paymentMethod) {

        entity.setFarm(farm);
        entity.setHarvestRecord(harvestRecord);
        entity.setExpenseCategory(category);
        entity.setPaymentMethod(paymentMethod);
        entity.setExpenseDate(request.getExpenseDate());
        entity.setAmount(request.getAmount());

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus().toUpperCase().trim());
        }
        entity.setDescription(request.getDescription());
        entity.setNotes(request.getNotes());
    }
}
