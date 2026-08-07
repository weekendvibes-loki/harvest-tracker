package com.harvesttracker.features.harvest;

import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.Season;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.farm.repository.SeasonRepository;
import com.harvesttracker.features.harvest.dto.HarvestDto;
import com.harvesttracker.features.harvest.dto.HarvestSummaryDto;
import com.harvesttracker.features.harvest.dto.HarvestWorkerDto;
import com.harvesttracker.features.harvest.service.HarvestService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HarvestServiceTest {

    @Autowired
    private HarvestService harvestService;

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

    private Farm activeFarm;
    private Farm inactiveFarm;
    private Season validSeason;
    private FruitType fruitType;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded"));

        User owner = userRepository.findByEmailAndDeletedAtIsNull("harvestserviceowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Harvest Service Owner");
                    u.setEmail("harvestserviceowner@harvesttracker.local");
                    u.setPasswordHash("hashed");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        activeFarm = farmRepository.save(new Farm(owner, "Active Service Farm", "OWNED", new BigDecimal("40.000")));

        Farm f = new Farm(owner, "Inactive Service Farm", "OWNED", new BigDecimal("20.000"));
        f.setStatus("INACTIVE");
        f.setIsActive(false);
        inactiveFarm = farmRepository.save(f);

        fruitType = fruitTypeRepository.findByCodeAndDeletedAtIsNull("MANGO")
                .orElseGet(() -> {
                    FruitType ft = new FruitType();
                    ft.setCode("MANGO");
                    ft.setName("Mango");
                    return fruitTypeRepository.save(ft);
                });

        Season s = new Season();
        s.setFarm(activeFarm);
        s.setFruitType(fruitType);
        s.setName("2025 Spring Season");
        s.setYear(2025);
        s.setStartDate(LocalDate.of(2025, 3, 1));
        s.setEndDate(LocalDate.of(2025, 5, 31));
        validSeason = seasonRepository.save(s);
    }

    @Test
    void testCreateHarvest_Success() {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(activeFarm.getId());
        req.setSeasonId(validSeason.getId());
        req.setFruitTypeId(fruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 4, 10));
        req.setHarvestQuantity(new BigDecimal("500.000"));
        req.setQualityGrade("A");

        HarvestDto.HarvestResponse resp = harvestService.createHarvest(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isNotNull();
        assertThat(resp.getHarvestQuantity()).isEqualByComparingTo("500.000");
        assertThat(resp.getQualityGrade()).isEqualTo("A");
    }

    @Test
    void testCreateHarvest_InactiveFarmThrowsException() {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(inactiveFarm.getId());
        req.setSeasonId(validSeason.getId());
        req.setFruitTypeId(fruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 4, 10));
        req.setHarvestQuantity(new BigDecimal("500.000"));

        assertThatThrownBy(() -> harvestService.createHarvest(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactive farm");
    }

    @Test
    void testCreateHarvest_DateOutsideSeasonThrowsException() {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(activeFarm.getId());
        req.setSeasonId(validSeason.getId());
        req.setFruitTypeId(fruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 6, 15)); // Season ends May 31!
        req.setHarvestQuantity(new BigDecimal("500.000"));

        assertThatThrownBy(() -> harvestService.createHarvest(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("outside season date range");
    }

    @Test
    void testAssignWorker_InactiveWorkerThrowsException() {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(activeFarm.getId());
        req.setSeasonId(validSeason.getId());
        req.setFruitTypeId(fruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 4, 10));
        req.setHarvestQuantity(new BigDecimal("500.000"));
        HarvestDto.HarvestResponse harvestResp = harvestService.createHarvest(req);

        Worker worker = new Worker(activeFarm, "Disabled Worker", new BigDecimal("400.00"));
        worker.setStatus("INACTIVE");
        worker.setIsActive(false);
        Worker savedWorker = workerRepository.save(worker);

        HarvestWorkerDto.HarvestWorkerRequest hwReq = new HarvestWorkerDto.HarvestWorkerRequest();
        hwReq.setWorkerId(savedWorker.getId());

        assertThatThrownBy(() -> harvestService.assignWorker(harvestResp.getId(), hwReq))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Inactive worker cannot be assigned");
    }

    @Test
    void testAssignWorker_DuplicateAssignmentThrowsException() {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(activeFarm.getId());
        req.setSeasonId(validSeason.getId());
        req.setFruitTypeId(fruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 4, 10));
        req.setHarvestQuantity(new BigDecimal("500.000"));
        HarvestDto.HarvestResponse harvestResp = harvestService.createHarvest(req);

        Worker worker = workerRepository.save(new Worker(activeFarm, "Active Worker", new BigDecimal("500.00")));

        HarvestWorkerDto.HarvestWorkerRequest hwReq = new HarvestWorkerDto.HarvestWorkerRequest();
        hwReq.setWorkerId(worker.getId());

        harvestService.assignWorker(harvestResp.getId(), hwReq);

        // Assign same worker again!
        assertThatThrownBy(() -> harvestService.assignWorker(harvestResp.getId(), hwReq))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already assigned");
    }

    @Test
    void testStatusTransitionValidation() {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(activeFarm.getId());
        req.setSeasonId(validSeason.getId());
        req.setFruitTypeId(fruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 4, 10));
        req.setHarvestQuantity(new BigDecimal("500.000"));
        HarvestDto.HarvestResponse harvestResp = harvestService.createHarvest(req);

        // DRAFT -> CONFIRMED
        HarvestDto.HarvestResponse step1 = harvestService.updateStatus(harvestResp.getId(), "CONFIRMED");
        assertThat(step1.getStatus()).isEqualTo("CONFIRMED");

        // CONFIRMED -> SOLD
        HarvestDto.HarvestResponse step2 = harvestService.updateStatus(harvestResp.getId(), "SOLD");
        assertThat(step2.getStatus()).isEqualTo("SOLD");

        // SOLD -> DRAFT (Must fail!)
        assertThatThrownBy(() -> harvestService.updateStatus(harvestResp.getId(), "DRAFT"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot change status of a harvest that has already been SOLD");
    }

    @Test
    void testHarvestSummary() {
        HarvestDto.HarvestRequest req = new HarvestDto.HarvestRequest();
        req.setFarmId(activeFarm.getId());
        req.setSeasonId(validSeason.getId());
        req.setFruitTypeId(fruitType.getId());
        req.setHarvestDate(LocalDate.of(2025, 4, 10));
        req.setHarvestQuantity(new BigDecimal("300.000"));
        req.setQualityGrade("A");
        harvestService.createHarvest(req);

        HarvestSummaryDto.HarvestSummaryResponse summary = harvestService.getHarvestSummary();
        assertThat(summary).isNotNull();
        assertThat(summary.getTotalHarvests()).isGreaterThanOrEqualTo(1);
    }
}
