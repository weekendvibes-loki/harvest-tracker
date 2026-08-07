package com.harvesttracker.features.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harvesttracker.common.security.JwtTokenProvider;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.worker.dto.WorkerAttendanceDto;
import com.harvesttracker.features.worker.dto.WorkerDto;
import com.harvesttracker.features.worker.dto.WorkerPaymentDto;
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
class WorkerControllerTest {

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

    private String adminToken;
    private String userToken;
    private Farm testFarm;

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

        testFarm = farmRepository.save(new Farm(adminUser, "Controller Worker Farm", "OWNED", new BigDecimal("60.000")));
    }

    @Test
    void testGetAllWorkers_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/workers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetAllWorkers_Authenticated() throws Exception {
        mockMvc.perform(get("/api/v1/workers")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void testCreateWorker_ForbiddenForNonAdmin() throws Exception {
        WorkerDto.WorkerRequest req = new WorkerDto.WorkerRequest();
        req.setFarmId(testFarm.getId());
        req.setName("Forbidden Worker");
        req.setDailyWageRate(new BigDecimal("400.00"));

        mockMvc.perform(post("/api/v1/workers")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testWorkerFullLifecycle_AdminSuccess() throws Exception {
        // 1. Create Worker
        WorkerDto.WorkerRequest req = new WorkerDto.WorkerRequest();
        req.setFarmId(testFarm.getId());
        req.setName("Karan Singh");
        req.setPhone("+919876111222");
        req.setDailyWageRate(new BigDecimal("550.00"));

        String createResult = mockMvc.perform(post("/api/v1/workers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Karan Singh"))
                .andReturn().getResponse().getContentAsString();

        Long workerId = objectMapper.readTree(createResult).get("data").get("id").asLong();

        // 2. Get By ID
        mockMvc.perform(get("/api/v1/workers/" + workerId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Karan Singh"));

        // 3. Search
        mockMvc.perform(get("/api/v1/workers/search")
                        .header("Authorization", "Bearer " + userToken)
                        .param("search", "Karan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));

        // 4. Update Worker
        req.setName("Karan Singh Updated");
        mockMvc.perform(put("/api/v1/workers/" + workerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Karan Singh Updated"));

        // 5. Record Attendance
        WorkerAttendanceDto.AttendanceRequest attReq = new WorkerAttendanceDto.AttendanceRequest();
        attReq.setAttendanceDate(LocalDate.of(2025, 3, 1));
        attReq.setIsPresent(true);
        attReq.setHoursWorked(new BigDecimal("8.00"));

        String attResult = mockMvc.perform(post("/api/v1/workers/" + workerId + "/attendance")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.isPresent").value(true))
                .andReturn().getResponse().getContentAsString();

        Long attId = objectMapper.readTree(attResult).get("data").get("id").asLong();

        // 6. Get Attendance List
        mockMvc.perform(get("/api/v1/workers/" + workerId + "/attendance")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // 7. Update Attendance
        attReq.setRemarks("Full day harvest work");
        mockMvc.perform(put("/api/v1/workers/" + workerId + "/attendance/" + attId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(attReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.remarks").value("Full day harvest work"));

        // 8. Record Payment
        WorkerPaymentDto.PaymentRequest payReq = new WorkerPaymentDto.PaymentRequest();
        payReq.setPeriodStart(LocalDate.of(2025, 3, 1));
        payReq.setPeriodEnd(LocalDate.of(2025, 3, 7));
        payReq.setTotalDaysWorked(7);
        payReq.setDailyWageRate(new BigDecimal("550.00"));
        payReq.setAmount(new BigDecimal("3850.00"));
        payReq.setPaymentStatus("PAID");

        mockMvc.perform(post("/api/v1/workers/" + workerId + "/payments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.paymentStatus").value("PAID"));

        // 9. Get Payments List
        mockMvc.perform(get("/api/v1/workers/" + workerId + "/payments")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        // 10. Delete Attendance
        mockMvc.perform(delete("/api/v1/workers/" + workerId + "/attendance/" + attId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 11. Soft Delete Worker
        mockMvc.perform(delete("/api/v1/workers/" + workerId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        // 12. Get after delete returns 404
        mockMvc.perform(get("/api/v1/workers/" + workerId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }
}
