package com.harvesttracker.features.expense.service;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.expense.domain.Expense;
import com.harvesttracker.features.expense.dto.*;
import com.harvesttracker.features.expense.mapper.ExpenseMapper;
import com.harvesttracker.features.expense.repository.ExpenseRepository;
import com.harvesttracker.features.expense.specification.ExpenseSpecification;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.harvest.domain.HarvestRecord;
import com.harvesttracker.features.harvest.repository.HarvestRecordRepository;
import com.harvesttracker.features.masterdata.domain.ExpenseCategory;
import com.harvesttracker.features.masterdata.domain.PaymentMethod;
import com.harvesttracker.features.masterdata.repository.ExpenseCategoryRepository;
import com.harvesttracker.features.masterdata.repository.PaymentMethodRepository;
import com.harvesttracker.features.sales.domain.Order;
import com.harvesttracker.features.sales.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final FarmRepository farmRepository;
    private final HarvestRecordRepository harvestRecordRepository;
    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final OrderRepository orderRepository;
    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(
            ExpenseRepository expenseRepository,
            FarmRepository farmRepository,
            HarvestRecordRepository harvestRecordRepository,
            ExpenseCategoryRepository expenseCategoryRepository,
            PaymentMethodRepository paymentMethodRepository,
            OrderRepository orderRepository,
            ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.farmRepository = farmRepository;
        this.harvestRecordRepository = harvestRecordRepository;
        this.expenseCategoryRepository = expenseCategoryRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.orderRepository = orderRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExpenseDto.ExpenseResponse> getAllExpenses(
            int page, int size, String sort, String direction,
            Long farmId, Long harvestRecordId, Long expenseCategoryId, Long paymentMethodId,
            String status, LocalDate startDate, LocalDate endDate, String search, Boolean isActive) {

        Sort sortObj = direction.equalsIgnoreCase("DESC") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        Pageable pageable = PageRequest.of(page, size, sortObj);

        Specification<Expense> spec = ExpenseSpecification.filterExpenses(
                farmId, harvestRecordId, expenseCategoryId, paymentMethodId, status, startDate, endDate, search, isActive);

        Page<Expense> expensePage = expenseRepository.findAll(spec, pageable);
        Page<ExpenseDto.ExpenseResponse> dtoPage = expensePage.map(expenseMapper::toResponse);

        return PagedResponse.of(dtoPage.getContent(), expensePage);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseDto.ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense record not found with id: " + id));
        return expenseMapper.toResponse(expense);
    }

    @Override
    public ExpenseDto.ExpenseResponse createExpense(ExpenseDto.ExpenseRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero");
        }

        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(request.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + request.getFarmId()));

        if (!farm.getIsActive()) {
            throw new IllegalArgumentException("Cannot record expense for inactive farm: " + farm.getName());
        }

        ExpenseCategory category = expenseCategoryRepository.findByIdAndDeletedAtIsNull(request.getExpenseCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found with id: " + request.getExpenseCategoryId()));

        if (!category.getIsActive()) {
            throw new IllegalArgumentException("Cannot assign inactive expense category: " + category.getName());
        }

        HarvestRecord harvestRecord = null;
        if (request.getHarvestRecordId() != null) {
            harvestRecord = harvestRecordRepository.findByIdAndDeletedAtIsNull(request.getHarvestRecordId())
                    .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + request.getHarvestRecordId()));
        }

        PaymentMethod paymentMethod = null;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findByIdAndDeletedAtIsNull(request.getPaymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + request.getPaymentMethodId()));
        }

        Expense expense = new Expense();
        expenseMapper.updateEntity(expense, request, farm, harvestRecord, category, paymentMethod);

        Expense saved = expenseRepository.save(expense);
        return expenseMapper.toResponse(saved);
    }

    @Override
    public ExpenseDto.ExpenseResponse updateExpense(Long id, ExpenseDto.ExpenseRequest request) {
        Expense expense = expenseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense record not found with id: " + id));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero");
        }

        Farm farm = farmRepository.findByIdAndDeletedAtIsNull(request.getFarmId())
                .orElseThrow(() -> new ResourceNotFoundException("Farm not found with id: " + request.getFarmId()));

        ExpenseCategory category = expenseCategoryRepository.findByIdAndDeletedAtIsNull(request.getExpenseCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Expense category not found with id: " + request.getExpenseCategoryId()));

        if (!category.getIsActive()) {
            throw new IllegalArgumentException("Cannot assign inactive expense category: " + category.getName());
        }

        HarvestRecord harvestRecord = null;
        if (request.getHarvestRecordId() != null) {
            harvestRecord = harvestRecordRepository.findByIdAndDeletedAtIsNull(request.getHarvestRecordId())
                    .orElseThrow(() -> new ResourceNotFoundException("Harvest record not found with id: " + request.getHarvestRecordId()));
        }

        PaymentMethod paymentMethod = null;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findByIdAndDeletedAtIsNull(request.getPaymentMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found with id: " + request.getPaymentMethodId()));
        }

        expenseMapper.updateEntity(expense, request, farm, harvestRecord, category, paymentMethod);

        Expense updated = expenseRepository.save(expense);
        return expenseMapper.toResponse(updated);
    }

    @Override
    public ExpenseDto.ExpenseResponse updateExpenseStatus(Long id, String status) {
        Expense expense = expenseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense record not found with id: " + id));

        String targetStatus = status.toUpperCase().trim();
        if (!List.of("RECORDED", "APPROVED", "REJECTED").contains(targetStatus)) {
            throw new IllegalArgumentException("Invalid expense status: " + status + ". Allowed values: RECORDED, APPROVED, REJECTED");
        }

        expense.setStatus(targetStatus);
        Expense updated = expenseRepository.save(expense);
        return expenseMapper.toResponse(updated);
    }

    @Override
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense record not found with id: " + id));

        expense.setDeletedAt(OffsetDateTime.now());
        expense.setIsActive(false);
        expenseRepository.save(expense);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenseSummaryDto.ExpenseSummaryResponse getExpenseSummary(Long farmId, LocalDate startDate, LocalDate endDate) {
        Specification<Expense> spec = ExpenseSpecification.filterExpenses(
                farmId, null, null, null, null, startDate, endDate, null, true);

        List<Expense> expenses = expenseRepository.findAll(spec);

        ExpenseSummaryDto.ExpenseSummaryResponse summary = new ExpenseSummaryDto.ExpenseSummaryResponse();
        summary.setTotalExpensesCount(expenses.size());

        BigDecimal totalAmount = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalExpenseAmount(totalAmount);

        BigDecimal recorded = expenses.stream()
                .filter(e -> "RECORDED".equalsIgnoreCase(e.getStatus()))
                .map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalRecordedAmount(recorded);

        BigDecimal approved = expenses.stream()
                .filter(e -> "APPROVED".equalsIgnoreCase(e.getStatus()))
                .map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalApprovedAmount(approved);

        BigDecimal rejected = expenses.stream()
                .filter(e -> "REJECTED".equalsIgnoreCase(e.getStatus()))
                .map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        summary.setTotalRejectedAmount(rejected);

        long uniqueFarms = expenses.stream().map(e -> e.getFarm().getId()).distinct().count();
        summary.setTotalFarmsCount(uniqueFarms);

        long uniqueCategories = expenses.stream().map(e -> e.getExpenseCategory().getId()).distinct().count();
        summary.setTotalCategoriesCount(uniqueCategories);

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyExpenseDto.MonthlyExpenseResponse> getMonthlyExpenses(Integer targetYear, Long farmId) {
        int year = targetYear != null ? targetYear : LocalDate.now().getYear();

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        Specification<Expense> spec = ExpenseSpecification.filterExpenses(
                farmId, null, null, null, null, startDate, endDate, null, true);

        List<Expense> expenses = expenseRepository.findAll(spec);

        Map<Integer, List<Expense>> monthlyGroup = expenses.stream()
                .collect(Collectors.groupingBy(e -> e.getExpenseDate().getMonthValue()));

        List<MonthlyExpenseDto.MonthlyExpenseResponse> result = new ArrayList<>();

        for (int m = 1; m <= 12; m++) {
            List<Expense> monthExpenses = monthlyGroup.getOrDefault(m, List.of());
            BigDecimal total = monthExpenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            String monthName = Month.of(m).getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            result.add(new MonthlyExpenseDto.MonthlyExpenseResponse(year, m, monthName, total, monthExpenses.size()));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryExpenseDto.CategoryExpenseResponse> getExpensesByCategory(Long farmId, LocalDate startDate, LocalDate endDate) {
        Specification<Expense> spec = ExpenseSpecification.filterExpenses(
                farmId, null, null, null, null, startDate, endDate, null, true);

        List<Expense> expenses = expenseRepository.findAll(spec);
        BigDecimal grandTotal = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<ExpenseCategory, List<Expense>> categoryGroup = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getExpenseCategory));

        List<CategoryExpenseDto.CategoryExpenseResponse> result = new ArrayList<>();

        for (Map.Entry<ExpenseCategory, List<Expense>> entry : categoryGroup.entrySet()) {
            ExpenseCategory cat = entry.getKey();
            List<Expense> catExpenses = entry.getValue();
            BigDecimal catTotal = catExpenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal percentage = BigDecimal.ZERO;
            if (grandTotal.compareTo(BigDecimal.ZERO) > 0) {
                percentage = catTotal.multiply(new BigDecimal("100"))
                        .divide(grandTotal, 2, RoundingMode.HALF_UP);
            }

            result.add(new CategoryExpenseDto.CategoryExpenseResponse(
                    cat.getId(), cat.getName(), cat.getCode(), catTotal, percentage, catExpenses.size()));
        }

        result.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfitLossDto.ProfitLossResponse getFinancialProfitLoss(LocalDate startDate, LocalDate endDate, Long farmId) {
        // Calculate Revenue from sales orders (excluding cancelled)
        List<Order> orders = orderRepository.findAll((root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));
            predicates.add(cb.notEqual(cb.upper(root.get("orderStatus")), "CANCELLED"));
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        });

        BigDecimal totalRevenue = orders.stream().map(Order::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate Expenses
        Specification<Expense> spec = ExpenseSpecification.filterExpenses(
                farmId, null, null, null, null, startDate, endDate, null, true);
        List<Expense> expenses = expenseRepository.findAll(spec);

        BigDecimal totalExpenses = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        // Direct operational expenses (expenses tied directly to a harvest record)
        BigDecimal directExpenses = expenses.stream()
                .filter(e -> e.getHarvestRecord() != null)
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grossProfit = totalRevenue.subtract(directExpenses);
        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        BigDecimal profitMarginPct = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            profitMarginPct = netProfit.multiply(new BigDecimal("100"))
                    .divide(totalRevenue, 2, RoundingMode.HALF_UP);
        }

        ProfitLossDto.ProfitLossResponse pl = new ProfitLossDto.ProfitLossResponse();
        pl.setTotalRevenue(totalRevenue);
        pl.setTotalExpenses(totalExpenses);
        pl.setDirectOperationalExpenses(directExpenses);
        pl.setGrossProfit(grossProfit);
        pl.setNetProfit(netProfit);
        pl.setProfitMarginPercentage(profitMarginPct);

        return pl;
    }
}
