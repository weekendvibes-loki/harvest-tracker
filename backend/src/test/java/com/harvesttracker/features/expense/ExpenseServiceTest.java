package com.harvesttracker.features.expense;

import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.expense.dto.*;
import com.harvesttracker.features.expense.service.ExpenseService;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.masterdata.domain.ExpenseCategory;
import com.harvesttracker.features.masterdata.repository.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseServiceTest {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Farm testFarm;
    private ExpenseCategory activeCategory;
    private ExpenseCategory inactiveCategory;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded"));

        User owner = userRepository.findByEmailAndDeletedAtIsNull("expenseserviceowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Expense Service Owner");
                    u.setEmail("expenseserviceowner@harvesttracker.local");
                    u.setPasswordHash("hashed");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        testFarm = farmRepository.save(new Farm(owner, "Service Test Farm", "OWNED", new BigDecimal("100.000")));

        activeCategory = expenseCategoryRepository.findByCodeIgnoreCaseAndDeletedAtIsNull("SVC_FUEL")
                .orElseGet(() -> expenseCategoryRepository.save(new ExpenseCategory("Irrigation Fuel", "SVC_FUEL", "Diesel for water pumps", 1)));

        inactiveCategory = expenseCategoryRepository.findByCodeIgnoreCaseAndDeletedAtIsNull("SVC_OLD")
                .orElseGet(() -> {
                    ExpenseCategory cat2 = new ExpenseCategory("Deprecated Utility", "SVC_OLD", "Deprecated category", 99);
                    cat2.setIsActive(false);
                    return expenseCategoryRepository.save(cat2);
                });
    }

    @Test
    void testCreateExpense_Success() {
        ExpenseDto.ExpenseRequest req = new ExpenseDto.ExpenseRequest();
        req.setFarmId(testFarm.getId());
        req.setExpenseCategoryId(activeCategory.getId());
        req.setExpenseDate(LocalDate.now());
        req.setAmount(new BigDecimal("4500.00"));
        req.setDescription("Diesel purchase for pump #2");

        ExpenseDto.ExpenseResponse resp = expenseService.createExpense(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isNotNull();
        assertThat(resp.getAmount()).isEqualByComparingTo("4500.00");
        assertThat(resp.getExpenseCategoryName()).isEqualTo("Irrigation Fuel");
    }

    @Test
    void testCreateExpense_InactiveCategoryThrowsException() {
        ExpenseDto.ExpenseRequest req = new ExpenseDto.ExpenseRequest();
        req.setFarmId(testFarm.getId());
        req.setExpenseCategoryId(inactiveCategory.getId()); // Inactive!
        req.setExpenseDate(LocalDate.now());
        req.setAmount(new BigDecimal("1000.00"));

        assertThatThrownBy(() -> expenseService.createExpense(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot assign inactive expense category");
    }

    @Test
    void testCreateExpense_ZeroOrNegativeAmountThrowsException() {
        ExpenseDto.ExpenseRequest req = new ExpenseDto.ExpenseRequest();
        req.setFarmId(testFarm.getId());
        req.setExpenseCategoryId(activeCategory.getId());
        req.setExpenseDate(LocalDate.now());
        req.setAmount(new BigDecimal("0.00")); // Invalid amount!

        assertThatThrownBy(() -> expenseService.createExpense(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void testExpenseStatusUpdate() {
        ExpenseDto.ExpenseRequest req = new ExpenseDto.ExpenseRequest();
        req.setFarmId(testFarm.getId());
        req.setExpenseCategoryId(activeCategory.getId());
        req.setExpenseDate(LocalDate.now());
        req.setAmount(new BigDecimal("2000.00"));

        ExpenseDto.ExpenseResponse resp = expenseService.createExpense(req);
        assertThat(resp.getStatus()).isEqualTo("RECORDED");

        ExpenseDto.ExpenseResponse updated = expenseService.updateExpenseStatus(resp.getId(), "APPROVED");
        assertThat(updated.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void testExpenseAnalyticsAndSummaries() {
        ExpenseDto.ExpenseRequest req1 = new ExpenseDto.ExpenseRequest();
        req1.setFarmId(testFarm.getId());
        req1.setExpenseCategoryId(activeCategory.getId());
        req1.setExpenseDate(LocalDate.now());
        req1.setAmount(new BigDecimal("3000.00"));
        expenseService.createExpense(req1);

        ExpenseSummaryDto.ExpenseSummaryResponse summary = expenseService.getExpenseSummary(testFarm.getId(), null, null);
        assertThat(summary).isNotNull();
        assertThat(summary.getTotalExpensesCount()).isGreaterThanOrEqualTo(1);

        List<MonthlyExpenseDto.MonthlyExpenseResponse> monthly = expenseService.getMonthlyExpenses(LocalDate.now().getYear(), testFarm.getId());
        assertThat(monthly).hasSize(12);

        List<CategoryExpenseDto.CategoryExpenseResponse> byCategory = expenseService.getExpensesByCategory(testFarm.getId(), null, null);
        assertThat(byCategory).isNotEmpty();

        ProfitLossDto.ProfitLossResponse profitLoss = expenseService.getFinancialProfitLoss(null, null, testFarm.getId());
        assertThat(profitLoss).isNotNull();
    }
}
