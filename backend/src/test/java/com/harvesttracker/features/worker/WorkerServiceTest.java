package com.harvesttracker.features.worker;

import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.worker.dto.WorkerAttendanceDto;
import com.harvesttracker.features.worker.dto.WorkerDto;
import com.harvesttracker.features.worker.dto.WorkerPaymentDto;
import com.harvesttracker.features.worker.service.WorkerService;
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
class WorkerServiceTest {

    @Autowired
    private WorkerService workerService;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Farm testFarm;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded by Flyway"));

        User owner = userRepository.findByEmailAndDeletedAtIsNull("workerserviceowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Worker Service Owner");
                    u.setEmail("workerserviceowner@harvesttracker.local");
                    u.setPasswordHash("hashed");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        testFarm = farmRepository.save(new Farm(owner, "Worker Service Farm", "OWNED", new BigDecimal("50.000")));
    }

    @Test
    void testCreateWorker_Success() {
        WorkerDto.WorkerRequest req = new WorkerDto.WorkerRequest();
        req.setFarmId(testFarm.getId());
        req.setName("Mahesh Yadav");
        req.setPhone("+919123456789");
        req.setDailyWageRate(new BigDecimal("450.00"));

        WorkerDto.WorkerResponse resp = workerService.createWorker(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isNotNull();
        assertThat(resp.getName()).isEqualTo("Mahesh Yadav");
        assertThat(resp.getFarmName()).isEqualTo("Worker Service Farm");
    }

    @Test
    void testCreateWorker_DuplicatePhoneThrowsException() {
        WorkerDto.WorkerRequest req1 = new WorkerDto.WorkerRequest();
        req1.setFarmId(testFarm.getId());
        req1.setName("Worker One");
        req1.setPhone("+919999988888");
        req1.setDailyWageRate(new BigDecimal("400.00"));
        workerService.createWorker(req1);

        WorkerDto.WorkerRequest req2 = new WorkerDto.WorkerRequest();
        req2.setFarmId(testFarm.getId());
        req2.setName("Worker Two");
        req2.setPhone("+919999988888");
        req2.setDailyWageRate(new BigDecimal("450.00"));

        assertThatThrownBy(() -> workerService.createWorker(req2))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testRecordAttendance_InactiveWorkerThrowsException() {
        WorkerDto.WorkerRequest req = new WorkerDto.WorkerRequest();
        req.setFarmId(testFarm.getId());
        req.setName("Inactive Worker");
        req.setDailyWageRate(new BigDecimal("500.00"));
        WorkerDto.WorkerResponse workerResp = workerService.createWorker(req);

        // Deactivate worker
        workerService.toggleStatus(workerResp.getId(), false);

        WorkerAttendanceDto.AttendanceRequest attReq = new WorkerAttendanceDto.AttendanceRequest();
        attReq.setAttendanceDate(LocalDate.now());
        attReq.setIsPresent(true);

        assertThatThrownBy(() -> workerService.recordAttendance(workerResp.getId(), attReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Inactive workers cannot receive attendance");
    }

    @Test
    void testRecordAttendance_DuplicateDateThrowsException() {
        WorkerDto.WorkerRequest req = new WorkerDto.WorkerRequest();
        req.setFarmId(testFarm.getId());
        req.setName("Active Worker");
        req.setDailyWageRate(new BigDecimal("500.00"));
        WorkerDto.WorkerResponse workerResp = workerService.createWorker(req);

        LocalDate today = LocalDate.now();
        WorkerAttendanceDto.AttendanceRequest attReq = new WorkerAttendanceDto.AttendanceRequest();
        attReq.setAttendanceDate(today);
        attReq.setIsPresent(true);

        workerService.recordAttendance(workerResp.getId(), attReq);

        // Record duplicate on same date
        assertThatThrownBy(() -> workerService.recordAttendance(workerResp.getId(), attReq))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already recorded");
    }

    @Test
    void testRecordPayment_InvalidPeriodThrowsException() {
        WorkerDto.WorkerRequest req = new WorkerDto.WorkerRequest();
        req.setFarmId(testFarm.getId());
        req.setName("Payment Worker");
        req.setDailyWageRate(new BigDecimal("600.00"));
        WorkerDto.WorkerResponse workerResp = workerService.createWorker(req);

        WorkerPaymentDto.PaymentRequest payReq = new WorkerPaymentDto.PaymentRequest();
        payReq.setPeriodStart(LocalDate.of(2025, 3, 10));
        payReq.setPeriodEnd(LocalDate.of(2025, 3, 5)); // End before start!
        payReq.setDailyWageRate(new BigDecimal("600.00"));
        payReq.setAmount(new BigDecimal("3000.00"));

        assertThatThrownBy(() -> workerService.recordPayment(workerResp.getId(), payReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("end date must be on or after start date");
    }
}
