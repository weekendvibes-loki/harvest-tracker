package com.harvesttracker.features.expense.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.features.expense.dto.*;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseService {

    PagedResponse<ExpenseDto.ExpenseResponse> getAllExpenses(
            int page, int size, String sort, String direction,
            Long farmId, Long harvestRecordId, Long expenseCategoryId, Long paymentMethodId,
            String status, LocalDate startDate, LocalDate endDate, String search, Boolean isActive);

    ExpenseDto.ExpenseResponse getExpenseById(Long id);

    ExpenseDto.ExpenseResponse createExpense(ExpenseDto.ExpenseRequest request);

    ExpenseDto.ExpenseResponse updateExpense(Long id, ExpenseDto.ExpenseRequest request);

    ExpenseDto.ExpenseResponse updateExpenseStatus(Long id, String status);

    void deleteExpense(Long id);

    ExpenseSummaryDto.ExpenseSummaryResponse getExpenseSummary(Long farmId, LocalDate startDate, LocalDate endDate);

    List<MonthlyExpenseDto.MonthlyExpenseResponse> getMonthlyExpenses(Integer year, Long farmId);

    List<CategoryExpenseDto.CategoryExpenseResponse> getExpensesByCategory(Long farmId, LocalDate startDate, LocalDate endDate);

    ProfitLossDto.ProfitLossResponse getFinancialProfitLoss(LocalDate startDate, LocalDate endDate, Long farmId);
}
