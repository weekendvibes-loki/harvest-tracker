package com.harvesttracker.features.expense;

import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.expense.domain.Expense;
import com.harvesttracker.features.expense.repository.ExpenseRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Farm testFarm;
    private ExpenseCategory testCategory;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded"));

        User owner = userRepository.findByEmailAndDeletedAtIsNull("expenserepoowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Expense Repo Owner");
                    u.setEmail("expenserepoowner@harvesttracker.local");
                    u.setPasswordHash("hashed");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        testFarm = farmRepository.save(new Farm(owner, "Expense Test Farm", "OWNED", new BigDecimal("50.000")));
        testCategory = expenseCategoryRepository.findByCodeIgnoreCaseAndDeletedAtIsNull("REPO_FERT")
                .orElseGet(() -> expenseCategoryRepository.save(new ExpenseCategory("Repo Fertilizers", "REPO_FERT", "Chemical and organic fertilizers", 1)));
    }

    @Test
    void testSaveAndFindExpense() {
        Expense expense = new Expense(testFarm, testCategory, LocalDate.now(), new BigDecimal("12500.00"));
        expense.setDescription("NPK Fertilizer 50bags");
        expense.setStatus("APPROVED");

        Expense saved = expenseRepository.save(expense);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAmount()).isEqualByComparingTo("12500.00");
        assertThat(saved.getStatus()).isEqualTo("APPROVED");

        Optional<Expense> found = expenseRepository.findByIdAndDeletedAtIsNull(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getFarm().getName()).isEqualTo("Expense Test Farm");
    }
}
