package com.harvesttracker.features.farm;

import com.harvesttracker.common.dto.PagedResponse;
import com.harvesttracker.common.exception.DuplicateResourceException;
import com.harvesttracker.common.exception.ResourceNotFoundException;
import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.dto.FarmDto;
import com.harvesttracker.features.farm.dto.FarmFruitTypeDto;
import com.harvesttracker.features.farm.service.FarmService;
import com.harvesttracker.features.masterdata.domain.FruitType;
import com.harvesttracker.features.masterdata.repository.FruitTypeRepository;
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
class FarmServiceTest {

    @Autowired
    private FarmService farmService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FruitTypeRepository fruitTypeRepository;

    private User owner;
    private FruitType fruitType;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded by Flyway"));

        owner = userRepository.findByEmailAndDeletedAtIsNull("servicetestowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Service Test Owner");
                    u.setEmail("servicetestowner@harvesttracker.local");
                    u.setPasswordHash("hashed");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });

        fruitType = fruitTypeRepository.findByCodeAndDeletedAtIsNull("MANGO_TEST")
                .orElseGet(() -> {
                    FruitType ft = new FruitType();
                    ft.setName("Mango Test");
                    ft.setCode("MANGO_TEST");
                    return fruitTypeRepository.save(ft);
                });
    }

    @Test
    void testCreateFarm_Success() {
        FarmDto.FarmRequest req = new FarmDto.FarmRequest();
        req.setOwnerId(owner.getId());
        req.setName("Royal Mango Estate");
        req.setOwnershipType("OWNED");
        req.setLandSize(new BigDecimal("100.000"));

        FarmDto.FarmResponse resp = farmService.createFarm(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isNotNull();
        assertThat(resp.getName()).isEqualTo("Royal Mango Estate");
        assertThat(resp.getOwnerName()).isEqualTo("Service Test Owner");
    }

    @Test
    void testCreateFarm_DuplicateNameThrowsException() {
        FarmDto.FarmRequest req1 = new FarmDto.FarmRequest();
        req1.setOwnerId(owner.getId());
        req1.setName("Unique Farm Name");
        farmService.createFarm(req1);

        FarmDto.FarmRequest req2 = new FarmDto.FarmRequest();
        req2.setOwnerId(owner.getId());
        req2.setName("unique farm name");

        assertThatThrownBy(() -> farmService.createFarm(req2))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void testCreateFarm_LeasedWithoutStartDateThrowsException() {
        FarmDto.FarmRequest req = new FarmDto.FarmRequest();
        req.setOwnerId(owner.getId());
        req.setName("Leased Farm Test");
        req.setOwnershipType("LEASED");

        assertThatThrownBy(() -> farmService.createFarm(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Lease start date is required");
    }

    @Test
    void testAddFruitTypeToFarm_SuccessAndDuplicateCheck() {
        FarmDto.FarmRequest farmReq = new FarmDto.FarmRequest();
        farmReq.setOwnerId(owner.getId());
        farmReq.setName("Fruit Association Farm");
        FarmDto.FarmResponse farmResp = farmService.createFarm(farmReq);

        FarmFruitTypeDto.FarmFruitTypeRequest ftReq = new FarmFruitTypeDto.FarmFruitTypeRequest();
        ftReq.setFruitTypeId(fruitType.getId());
        ftReq.setIsPrimary(true);
        ftReq.setFirstPlantedDate(LocalDate.of(2020, 1, 1));

        FarmFruitTypeDto.FarmFruitTypeResponse ftResp = farmService.addFruitTypeToFarm(farmResp.getId(), ftReq);

        assertThat(ftResp).isNotNull();
        assertThat(ftResp.getFruitTypeName()).isEqualTo("Mango Test");

        List<FarmFruitTypeDto.FarmFruitTypeResponse> list = farmService.getFarmFruitTypes(farmResp.getId());
        assertThat(list).hasSize(1);

        // Expect duplicate exception if adding same fruit type again
        assertThatThrownBy(() -> farmService.addFruitTypeToFarm(farmResp.getId(), ftReq))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already associated");
    }
}
