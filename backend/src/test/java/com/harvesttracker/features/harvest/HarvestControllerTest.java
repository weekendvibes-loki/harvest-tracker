package com.harvesttracker.features.harvest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.farm.repository.SeasonRepository;
import com.harvesttracker.features.harvest.dto.HarvestDto;
import com.harvesttracker.features.harvest.dto.HarvestWorkerDto;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
import com.harvesttracker.features.worker.domain.Worker;
import com.harvesttracker.features.worker.repository.WorkerRepository;
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
class HarvestControllerTest {

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
    private SeasonRepository seasonRepository;

    @Autowired
    private FruitTypeRepository fruitTypeRepository;

    @Autowired
    private WorkerRepository workerRepository;

    private String adminToken;
    private String userToken;
    private Farm testFarm;
    private Season testSeason;
    private FruitType testFruitType;
    private Worker testWorker;

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

        testFarm = farmRepository.save(new Farm(adminUser, "Controller Harvest Farm", "OWNED", new BigDecimal("50.000")));
        
        testFruitType = fruitTypeRepository.findByCodeAndDeletedAtIsNull("MANGO")
                .orElseGet(() -> {
                    FruitType ft = new FruitType();
                    ft.setCode("MANGO");
                    ft.setName("Mango");
                    return fruitTypeRepository.save(ft);
                });

        Season s = new Season();
        s.setFarm(testFarm);
        s.setFruitType(testFruitType);
        s.setName("2025 Controller Season");
        s.setYear(2025);
        s.setStartDate(LocalDate.of(2025, 1, 1));
        s.setEndDate(LocalDate.of(2025, 12, 31));
        testSeason = seasonRepository.save(s);

        testWorker = workerRepository.save(new Worker(testFarm, "Controller Harvest Worker", new BigDecimal("550.00")));
    }

    @Test
    void testGetAllHarvests_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/harvests"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllHarvests_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/harvests")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testGetHarvestSummary() throws Exception {
        mockMvc.perform(get("/api/v1/harvests/summary")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalHarvests").isNumber());
    }

    @Test
    void testCreateHarvest_ForbiddenForNonAdmin() throws Exception {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(testFarm.getId());
        req.setSeasonId(testSeason.getId());
        req.setFruitTypeId(testFruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 5, 1));
        req.setHarvestQuantity(new BigDecimal("1000.000"));

        mockMvc.perform(post("/api/v1/harvests")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testHarvestFullLifecycle_AdminSuccess() throws Exception {
        // 1. Create Harvest
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(testFarm.getId());
        req.setSeasonId(testSeason.getId());
        req.setFruitTypeId(testFruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 5, 10));
        req.setHarvestQuantity(new BigDecimal("1500.000"));
        req.setQualityGrade("A");
        req.setStorageLocation("Warehouse A");

        String createResult = mockMvc.perform(post("/api/v1/harvests")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.qualityGrade").value("A"))
                .andReturn().getResponse().getContentAsString();

        Long harvestId = objectMapper.readTree(createResult).get("data").get("id").asLong();

        // 2. Get By ID
        mockMvc.perform(get("/api/v1/harvests/" + harvestId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.storageLocation").value("Warehouse A"));

        // 3. Update Harvest
        req.setStorageLocation("Warehouse B - Cold Storage");
        mockMvc.perform(put("/api/v1/harvests/" + harvestId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.storageLocation").value("Warehouse B - Cold Storage"));

        // 4. Update Status (DRAFT -> CONFIRMED)
        mockMvc.perform(patch("/api/v1/harvests/" + harvestId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));

        // 5. Assign Worker
        HarvestWorkerDto.HarvestWorkerRequest hwReq = new HarvestWorkerDto.HarvestWorkerRequest();
        hwReq.setWorkerId(testWorker.getId());
        hwReq.setRoleInHarvest("Harvester Lead");
        hwReq.setHoursWorked(new BigDecimal("8.00"));

        mockMvc.perform(post("/api/v1/harvests/" + harvestId + "/workers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hwReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.roleInHarvest").value("Harvester Lead"));

        // 6. Remove Worker
        mockMvc.perform(delete("/api/v1/harvests/" + harvestId + "/workers/" + testWorker.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 7. Soft Delete Harvest
        mockMvc.perform(delete("/api/v1/harvests/" + harvestId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 8. Get after delete returns 404
        mockMvc.perform(get("/api/v1/harvests/" + harvestId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
