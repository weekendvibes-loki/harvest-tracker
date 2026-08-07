package com.harvesttracker.features.expense;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.expense.dto.ExpenseDto;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.masterdata.domain.ExpenseCategory;
import com.harvesttracker.features.masterdata.repository.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private ExpenseCategoryRepository expenseCategoryRepository;

    private String adminToken;
    private String userToken;
    private Farm testFarm;
    private ExpenseCategory testCategory;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded"));

        Role managerRole = roleRepository.findByCodeAndDeletedAtIsNull("MANAGER")
                .orElseThrow(() -> new IllegalStateException("MANAGER role not seeded"));

        User adminUser = userRepository.findByEmailAndDeletedAtIsNull("admin@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("System Admin");
                    u.setEmail("admin@harvesttracker.local");
                    u.setPasswordHash("hashed_password");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        User normalUser = userRepository.findByEmailAndDeletedAtIsNull("manager@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Farm Manager");
                    u.setEmail("manager@harvesttracker.local");
                    u.setPasswordHash("hashed_password");
                    u.setRole(managerRole);
                    return userRepository.save(u);
                });

        adminToken = jwtTokenProvider.generateAccessToken(
                adminUser.getId(), adminUser.getEmail(), "ADMIN", List.of("ROLE_ADMIN"));

        userToken = jwtTokenProvider.generateAccessToken(
                normalUser.getId(), normalUser.getEmail(), "MANAGER", List.of("ROLE_MANAGER"));

        testFarm = farmRepository.save(new Farm(adminUser, "Controller Test Farm", "OWNED", new BigDecimal("15.000")));
        testCategory = expenseCategoryRepository.findByCodeIgnoreCaseAndDeletedAtIsNull("CTRL_PEST")
                .orElseGet(() -> expenseCategoryRepository.save(new ExpenseCategory("Controller Pesticides", "CTRL_PEST", "Plant protection chemicals", 2)));
    }

    @Test
    void testGetAllExpenses_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/expenses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllExpenses_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/expenses")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testExpenseLifecycle_AdminSuccess() throws Exception {
        // 1. Create Expense
        ExpenseDto.ExpenseRequest req = new ExpenseDto.ExpenseRequest();
        req.setFarmId(testFarm.getId());
        req.setExpenseCategoryId(testCategory.getId());
        req.setExpenseDate(LocalDate.now());
        req.setAmount(new BigDecimal("7500.00"));
        req.setDescription("Fungicide spray for mango trees");

        String result = mockMvc.perform(post("/api/v1/expenses")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(7500.00))
                .andExpect(jsonPath("$.data.status").value("RECORDED"))
                .andReturn().getResponse().getContentAsString();

        Long expenseId = objectMapper.readTree(result).get("data").get("id").asLong();

        // 2. Update Expense Status to APPROVED
        mockMvc.perform(patch("/api/v1/expenses/" + expenseId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        // 3. Get Expense Summary
        mockMvc.perform(get("/api/v1/expenses/summary")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalExpensesCount").isNumber());

        // 4. Get Monthly Expenses
        mockMvc.perform(get("/api/v1/expenses/monthly")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        // 5. Get Expenses By Category
        mockMvc.perform(get("/api/v1/expenses/by-category")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());

        // 6. Get Profit & Loss
        mockMvc.perform(get("/api/v1/financial/profit-loss")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalExpenses").isNumber());

        // 7. Soft Delete Expense (ADMIN only)
        mockMvc.perform(delete("/api/v1/expenses/" + expenseId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
