package com.harvesttracker.features.farm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.dto.FarmDto;
import com.harvesttracker.features.farm.dto.FarmFruitTypeDto;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FarmControllerTest {

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
    private FruitTypeRepository fruitTypeRepository;

    private String adminToken;
    private String userToken;
    private User adminUser;
    private FruitType testFruitType;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded by Flyway"));

        Role managerRole = roleRepository.findByCodeAndDeletedAtIsNull("MANAGER")
                .orElseThrow(() -> new IllegalStateException("MANAGER role not seeded by Flyway"));

        adminUser = userRepository.findByEmailAndDeletedAtIsNull("admin@harvesttracker.local")
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

        testFruitType = fruitTypeRepository.findByCodeAndDeletedAtIsNull("MANGO_CTRL")
                .orElseGet(() -> {
                    FruitType ft = new FruitType();
                    ft.setName("Mango Ctrl");
                    ft.setCode("MANGO_CTRL");
                    return fruitTypeRepository.save(ft);
                });
    }

    @Test
    void testGetAllFarms_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/farms"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllFarms_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/farms")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testCreateFarm_ForbiddenForNonAdmin() throws Exception {
        FarmDto.FarmRequest req = new FarmDto.FarmRequest();
        req.setOwnerId(adminUser.getId());
        req.setName("Forbidden Farm");

        mockMvc.perform(post("/api/v1/farms")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFarmLifecycle_AdminSuccess() throws Exception {
        // 1. Create
        FarmDto.FarmRequest createReq = new FarmDto.FarmRequest();
        createReq.setOwnerId(adminUser.getId());
        createReq.setName("Controller Lifecycle Farm");
        createReq.setOwnershipType("OWNED");
        createReq.setLandSize(new BigDecimal("75.250"));
        createReq.setGpsLatitude(new BigDecimal("19.0760000"));
        createReq.setGpsLongitude(new BigDecimal("72.8777000"));
        createReq.setAddress("Village Nashik, Maharashtra");

        String createResult = mockMvc.perform(post("/api/v1/farms")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Controller Lifecycle Farm"))
                .andReturn().getResponse().getContentAsString();

        Long farmId = objectMapper.readTree(createResult).get("data").get("id").asLong();

        // 2. Get By ID
        mockMvc.perform(get("/api/v1/farms/" + farmId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Controller Lifecycle Farm"));

        // 3. Search
        mockMvc.perform(get("/api/v1/farms/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("search", "Nashik"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));

        // 4. Update
        createReq.setName("Controller Lifecycle Farm Updated");
        mockMvc.perform(put("/api/v1/farms/" + farmId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Controller Lifecycle Farm Updated"));

        // 5. Add Fruit Type
        FarmFruitTypeDto.FarmFruitTypeRequest ftReq = new FarmFruitTypeDto.FarmFruitTypeRequest();
        ftReq.setFruitTypeId(testFruitType.getId());
        ftReq.setIsPrimary(true);

        mockMvc.perform(post("/api/v1/farms/" + farmId + "/fruit-types")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ftReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.fruitTypeId").value(testFruitType.getId()));

        // 6. Get Fruit Types
        mockMvc.perform(get("/api/v1/farms/" + farmId + "/fruit-types")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // 7. Toggle Status
        mockMvc.perform(patch("/api/v1/farms/" + farmId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("isActive", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.isActive").value(false));

        // 8. Delete
        mockMvc.perform(delete("/api/v1/farms/" + farmId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 9. Get after delete should return 404
        mockMvc.perform(get("/api/v1/farms/" + farmId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }
}
