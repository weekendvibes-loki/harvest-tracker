package com.harvesttracker.features.harvest;

import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.farm.repository.SeasonRepository;
import com.harvesttracker.features.harvest.domain.HarvestQualityCheck;
import com.harvesttracker.features.harvest.domain.HarvestRecord;
import com.harvesttracker.features.harvest.domain.HarvestWorker;
import com.harvesttracker.features.harvest.repository.HarvestQualityCheckRepository;
import com.harvesttracker.features.harvest.repository.HarvestRecordRepository;
import com.harvesttracker.features.harvest.repository.HarvestWorkerRepository;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
import com.harvesttracker.features.worker.domain.Worker;
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
class HarvestRepositoryTest {

    @Autowired
    private HarvestRecordRepository harvestRecordRepository;

    @Autowired
    private HarvestWorkerRepository harvestWorkerRepository;

    @Autowired
    private HarvestQualityCheckRepository qualityCheckRepository;

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private SeasonRepository seasonRepository;

    @Autowired
    private FruitTypeRepository fruitTypeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private WorkerRepository workerRepository;

    private Farm testFarm;
    private Season testSeason;
    private FruitType testFruitType;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded"));

        User owner = userRepository.findByEmailAndDeletedAtIsNull("harvestowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Harvest Owner");
                    u.setEmail("harvestowner@harvesttracker.local");
                    u.setPasswordHash("hashed");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        testFarm = farmRepository.save(new Farm(owner, "Harvest Test Farm", "OWNED", new BigDecimal("35.000")));
        
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
        s.setName("2025 Summer Harvest");
        s.setYear(2025);
        s.setStartDate(LocalDate.of(2025, 1, 1));
        s.setEndDate(LocalDate.of(2025, 6, 30));
        testSeason = seasonRepository.save(s);
    }

    @Test
    void testSaveAndFindHarvestRecord() {
        HarvestRecord harvest = new HarvestRecord(testFarm, testSeason, testFruitType, LocalDate.of(2025, 4, 15), new BigDecimal("1250.500"));
        harvest.setQualityGrade("A");
        harvest.setStatus("CONFIRMED");
        harvest.setStorageLocation("Cold Storage Warehouse 1");

        HarvestRecord saved = harvestRecordRepository.save(harvest);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getHarvestQuantity()).isEqualByComparingTo("1250.500");

        Optional<HarvestRecord> found = harvestRecordRepository.findByIdAndDeletedAtIsNull(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getQualityGrade()).isEqualTo("A");
    }

    @Test
    void testAssignHarvestWorker() {
        HarvestRecord harvest = harvestRecordRepository.save(
                new HarvestRecord(testFarm, testSeason, testFruitType, LocalDate.of(2025, 4, 20), new BigDecimal("800.000")));

        Worker worker = workerRepository.save(new Worker(testFarm, "Harvest Worker 1", new BigDecimal("500.00")));

        HarvestWorker hw = new HarvestWorker(harvest, worker, "Picker", new BigDecimal("8.00"));
        HarvestWorker savedHW = harvestWorkerRepository.save(hw);

        assertThat(savedHW.getId()).isNotNull();
        assertThat(harvestWorkerRepository.existsByHarvestRecordIdAndWorkerIdAndDeletedAtIsNull(harvest.getId(), worker.getId())).isTrue();
    }

    @Test
    void testQualityCheckSave() {
        HarvestRecord harvest = harvestRecordRepository.save(
                new HarvestRecord(testFarm, testSeason, testFruitType, LocalDate.of(2025, 4, 22), new BigDecimal("950.000")));

        HarvestQualityCheck qc = new HarvestQualityCheck();
        qc.setHarvestRecord(harvest);
        qc.setQualityGrade("A");
        qc.setDefectPercentage(new BigDecimal("1.50"));
        qc.setIsApproved(true);

        HarvestQualityCheck savedQC = qualityCheckRepository.save(qc);
        assertThat(savedQC.getId()).isNotNull();
        assertThat(savedQC.getIsApproved()).isTrue();
    }
}
