package com.harvesttracker.features.masterdata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.masterdata.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MasterDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        User adminUser = userRepository.findByEmailAndDeletedAtIsNull("admin@harvesttracker.local")
                .orElseThrow(() -> new IllegalStateException("Admin user not seeded"));

        Role managerRole = roleRepository.findByCodeAndDeletedAtIsNull("MANAGER")
                .orElseThrow(() -> new IllegalStateException("MANAGER role missing"));

        User managerUser = userRepository.findByEmailAndDeletedAtIsNull("manager@harvesttracker.local")
                .orElseGet(() -> {
                    User user = new User(null, managerRole, "Manager User", "manager@harvesttracker.local", "$2a$10$ZDXARz3tMSS.cKcCqzJlo.AGITZCU8A5O0yHlLT/TQ60qejBsHGRu");
                    return userRepository.save(user);
                });

        adminToken = jwtTokenProvider.generateAccessToken(adminUser.getId(), adminUser.getEmail(), "ADMIN", List.of("ROLE_ADMIN"));
        userToken = jwtTokenProvider.generateAccessToken(managerUser.getId(), managerUser.getEmail(), "MANAGER", List.of("ROLE_MANAGER"));
    }

    @Test
    @DisplayName("GET /api/v1/fruit-types - 401 Unauthorized when no token provided")
    void testGetAllFruitTypes_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/fruit-types"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/fruit-types - 200 OK with seeded fruit types")
    void testGetAllFruitTypes_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/fruit-types")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].code", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/fruit-types - 403 Forbidden when non-admin user attempts creation")
    void testCreateFruitType_Forbidden() throws Exception {
        FruitTypeDto.FruitTypeRequest request = new FruitTypeDto.FruitTypeRequest(
                "Dragon Fruit", "DRAGON_FRUIT", "Exotic fruit", "SUMMER", 10, true);

        mockMvc.perform(post("/api/v1/fruit-types")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/fruit-types - 201 Created when Admin creates valid fruit type")
    void testCreateFruitType_AdminSuccess() throws Exception {
        FruitTypeDto.FruitTypeRequest request = new FruitTypeDto.FruitTypeRequest(
                "Guava", "GUAVA", "Tropical guava fruit", "WINTER", 7, true);

        mockMvc.perform(post("/api/v1/fruit-types")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.name", is("Guava")))
                .andExpect(jsonPath("$.data.code", is("GUAVA")));
    }

    @Test
    @DisplayName("POST /api/v1/fruit-types - 409 Conflict when creating duplicate code")
    void testCreateFruitType_DuplicateCode() throws Exception {
        FruitTypeDto.FruitTypeRequest request = new FruitTypeDto.FruitTypeRequest(
                "Duplicate Mango", "MANGO", "Duplicate mango", "SUMMER", 1, true);

        mockMvc.perform(post("/api/v1/fruit-types")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Fruit Type Lifecycle - Create, Update, Status Toggle, Soft Delete")
    void testFruitTypeLifecycle() throws Exception {
        // 1. Create
        FruitTypeDto.FruitTypeRequest createReq = new FruitTypeDto.FruitTypeRequest(
                "Papaya", "PAPAYA", "Tropical papaya", "YEAR_ROUND", 8, true);

        String createResp = mockMvc.perform(post("/api/v1/fruit-types")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createResp).get("data").get("id").asLong();

        // 2. Update
        FruitTypeDto.FruitTypeRequest updateReq = new FruitTypeDto.FruitTypeRequest(
                "Papaya Red", "PAPAYA", "Red lady papaya variety", "YEAR_ROUND", 8, true);

        mockMvc.perform(put("/api/v1/fruit-types/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Papaya Red")));

        // 3. Status Toggle
        mockMvc.perform(patch("/api/v1/fruit-types/" + id + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("isActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isActive", is(false)));

        // 4. Soft Delete
        mockMvc.perform(delete("/api/v1/fruit-types/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 5. Verify 404 after soft delete
        mockMvc.perform(get("/api/v1/fruit-types/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/units-of-measure - Filter by measureType=WEIGHT")
    void testGetUnitsOfMeasure_Filtered() throws Exception {
        mockMvc.perform(get("/api/v1/units-of-measure")
                        .header("Authorization", "Bearer " + userToken)
                        .param("measureType", "WEIGHT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].measureType", is("WEIGHT")));
    }

    @Test
    @DisplayName("POST /api/v1/units-of-measure - 201 Created for new UOM")
    void testCreateUnitOfMeasure_Success() throws Exception {
        UnitOfMeasureDto.UnitOfMeasureRequest request = new UnitOfMeasureDto.UnitOfMeasureRequest(
                "Litre", "LTR", "VOLUME", 1, true);

        mockMvc.perform(post("/api/v1/units-of-measure")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code", is("LTR")))
                .andExpect(jsonPath("$.data.measureType", is("VOLUME")));
    }

    @Test
    @DisplayName("GET /api/v1/worker-types - 200 OK list worker types")
    void testGetAllWorkerTypes() throws Exception {
        mockMvc.perform(get("/api/v1/worker-types")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/v1/payment-methods - 200 OK list payment methods")
    void testGetAllPaymentMethods() throws Exception {
        mockMvc.perform(get("/api/v1/payment-methods")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("GET /api/v1/expense-categories - 200 OK list expense categories")
    void testGetAllExpenseCategories() throws Exception {
        mockMvc.perform(get("/api/v1/expense-categories")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }
}
