package com.harvesttracker.features.farm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.dto.SeasonDto;
import com.harvesttracker.features.farm.repository.FarmRepository;
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
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SeasonControllerTest {

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
    private FruitTypeRepository fruitTypeRepository;

    private String adminToken;
    private String userToken;
    private Farm testFarm;
    private FruitType testFruitType;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded by Flyway"));

        Role managerRole = roleRepository.findByCodeAndDeletedAtIsNull("MANAGER")
                .orElseThrow(() -> new IllegalStateException("MANAGER role not seeded by Flyway"));

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

        testFruitType = fruitTypeRepository.findByCodeAndDeletedAtIsNull("MANGO_SEASON")
                .orElseGet(() -> {
                    FruitType ft = new FruitType();
                    ft.setName("Mango Season");
                    ft.setCode("MANGO_SEASON");
                    return fruitTypeRepository.save(ft);
                });

        testFarm = farmRepository.save(new Farm(adminUser, "Season Test Farm", "OWNED", new BigDecimal("30.000")));
    }

    @Test
    void testGetAllSeasons_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/seasons")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testCreateSeason_InvalidDates_BadRequest() throws Exception {
        SeasonDto.SeasonRequest req = new SeasonDto.SeasonRequest();
        req.setFarmId(testFarm.getId());
        req.setFruitTypeId(testFruitType.getId());
        req.setName("Invalid Dates Season");
        req.setYear(2025);
        req.setStartDate(LocalDate.of(2025, 6, 1));
        req.setEndDate(LocalDate.of(2025, 5, 1)); // End before start!

        mockMvc.perform(post("/api/v1/seasons")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSeasonLifecycle_AdminSuccess() throws Exception {
        // 1. Create Season
        SeasonDto.SeasonRequest req = new SeasonDto.SeasonRequest();
        req.setFarmId(testFarm.getId());
        req.setFruitTypeId(testFruitType.getId());
        req.setName("Mango Harvest 2025");
        req.setYear(2025);
        req.setStartDate(LocalDate.of(2025, 1, 15));
        req.setEndDate(LocalDate.of(2025, 5, 31));
        req.setStatus("PLANNED");

        String result = mockMvc.perform(post("/api/v1/seasons")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Mango Harvest 2025"))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andReturn().getResponse().getContentAsString();

        Long seasonId = objectMapper.readTree(result).get("data").get("id").asLong();

        // 2. Get By ID
        mockMvc.perform(get("/api/v1/seasons/" + seasonId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Mango Harvest 2025"));

        // 3. Update Season
        req.setStatus("ACTIVE");
        mockMvc.perform(put("/api/v1/seasons/" + seasonId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        // 4. Delete Season
        mockMvc.perform(delete("/api/v1/seasons/" + seasonId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 5. Get after delete should return 404
        mockMvc.perform(get("/api/v1/seasons/" + seasonId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
