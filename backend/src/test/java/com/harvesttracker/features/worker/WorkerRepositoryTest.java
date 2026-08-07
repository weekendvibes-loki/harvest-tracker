package com.harvesttracker.features.worker;

import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.worker.domain.Worker;
import com.harvesttracker.features.worker.domain.WorkerAttendance;
import com.harvesttracker.features.worker.domain.WorkerPayment;
import com.harvesttracker.features.worker.repository.WorkerAttendanceRepository;
import com.harvesttracker.features.worker.repository.WorkerPaymentRepository;
import com.harvesttracker.features.worker.repository.WorkerRepository;
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
class WorkerRepositoryTest {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private WorkerAttendanceRepository attendanceRepository;

    @Autowired
    private WorkerPaymentRepository paymentRepository;

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

        User owner = userRepository.findByEmailAndDeletedAtIsNull("workerowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Worker Owner");
                    u.setEmail("workerowner@harvesttracker.local");
                    u.setPasswordHash("hashed_password");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        testFarm = farmRepository.save(new Farm(owner, "Worker Test Farm", "OWNED", new BigDecimal("40.000")));
    }

    @Test
    void testSaveAndFindWorker() {
        Worker worker = new Worker(testFarm, "Ramesh Kumar", new BigDecimal("500.00"));
        worker.setPhone("+919876543210");
        worker.setIdCardNumber("AADHAAR12345678");

        Worker saved = workerRepository.save(worker);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Ramesh Kumar");

        Optional<Worker> found = workerRepository.findByIdAndDeletedAtIsNull(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getPhone()).isEqualTo("+919876543210");
    }

    @Test
    void testAttendanceSaveAndDuplicateCheck() {
        Worker worker = workerRepository.save(new Worker(testFarm, "Suresh Patil", new BigDecimal("600.00")));
        LocalDate date = LocalDate.of(2025, 2, 1);

        WorkerAttendance attendance = new WorkerAttendance(worker, date, true);
        attendance.setHoursWorked(new BigDecimal("8.00"));
        attendanceRepository.save(attendance);

        boolean exists = attendanceRepository.existsByWorkerIdAndAttendanceDateAndDeletedAtIsNull(worker.getId(), date);
        assertThat(exists).isTrue();
    }

    @Test
    void testPaymentSave() {
        Worker worker = workerRepository.save(new Worker(testFarm, "Ganesh Shinde", new BigDecimal("550.00")));
        LocalDate start = LocalDate.of(2025, 2, 1);
        LocalDate end = LocalDate.of(2025, 2, 7);

        WorkerPayment payment = new WorkerPayment(worker, start, end, new BigDecimal("3850.00"));
        payment.setTotalDaysWorked(7);
        payment.setDailyWageRate(new BigDecimal("550.00"));
        payment.setPaymentStatus("PAID");

        WorkerPayment saved = paymentRepository.save(payment);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAmount()).isEqualByComparingTo("3850.00");
    }
}
