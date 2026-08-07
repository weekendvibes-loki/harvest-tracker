package com.harvesttracker.features.masterdata.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.masterdata.dto.ExpenseCategoryDto;

public interface ExpenseCategoryService {

    PagedResponse<ExpenseCategoryDto.ExpenseCategoryResponse> getAllExpenseCategories(
            int page, int size, String sort, String direction, String search, Boolean isActive);

    ExpenseCategoryDto.ExpenseCategoryResponse getExpenseCategoryById(Long id);

    ExpenseCategoryDto.ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryDto.ExpenseCategoryRequest request);

    ExpenseCategoryDto.ExpenseCategoryResponse updateExpenseCategory(Long id, ExpenseCategoryDto.ExpenseCategoryRequest request);

    ExpenseCategoryDto.ExpenseCategoryResponse toggleStatus(Long id, boolean isActive);

    void deleteExpenseCategory(Long id);
}
