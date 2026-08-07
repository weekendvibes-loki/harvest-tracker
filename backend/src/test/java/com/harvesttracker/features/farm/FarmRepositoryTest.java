package com.harvesttracker.features.farm;

import com.harvesttracker.features.auth.domain.Role;
import com.harvesttracker.features.auth.domain.User;
import com.harvesttracker.features.auth.repository.RoleRepository;
import com.harvesttracker.features.auth.repository.UserRepository;
import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.repository.FarmRepository;
import com.harvesttracker.features.farm.specification.FarmSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FarmRepositoryTest {

    @Autowired
    private FarmRepository farmRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role adminRole = roleRepository.findByCodeAndDeletedAtIsNull("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not seeded by Flyway"));

        testUser = userRepository.findByEmailAndDeletedAtIsNull("farmowner@harvesttracker.local")
                .orElseGet(() -> {
                    User u = new User();
                    u.setName("Farm Owner");
                    u.setEmail("farmowner@harvesttracker.local");
                    u.setPasswordHash("hashed_password");
                    u.setRole(adminRole);
                    return userRepository.save(u);
                });
    }

    @Test
    void testSaveAndFindFarm() {
        Farm farm = new Farm(testUser, "Green Valley Orchards", "OWNED", new BigDecimal("50.500"));
        farm.setAddress("Village Khed, District Pune, Maharashtra");
        farm.setGpsLatitude(new BigDecimal("18.5204300"));
        farm.setGpsLongitude(new BigDecimal("73.8567400"));

        Farm saved = farmRepository.save(farm);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Green Valley Orchards");

        Optional<Farm> found = farmRepository.findByIdAndDeletedAtIsNull(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Green Valley Orchards");
    }

    @Test
    void testExistsByNameIgnoreCase() {
        Farm farm = new Farm(testUser, "Sunshine Mango Estate", "OWNED", new BigDecimal("25.000"));
        farmRepository.save(farm);

        boolean exists = farmRepository.existsByNameIgnoreCaseAndDeletedAtIsNull("sunshine mango estate");
        assertThat(exists).isTrue();

        boolean notExists = farmRepository.existsByNameIgnoreCaseAndDeletedAtIsNull("Non Existing Farm");
        assertThat(notExists).isFalse();
    }

    @Test
    void testFarmSpecificationSearch() {
        Farm f1 = new Farm(testUser, "Alphonso Paradise", "OWNED", new BigDecimal("10.000"));
        f1.setAddress("Ratnagiri, Maharashtra");
        farmRepository.save(f1);

        Farm f2 = new Farm(testUser, "Kesar Grove", "LEASED", new BigDecimal("15.000"));
        f2.setAddress("Junagadh, Gujarat");
        f2.setLeaseStartDate(java.time.LocalDate.of(2024, 1, 1));
        farmRepository.save(f2);

        Specification<Farm> spec = FarmSpecification.filterFarms("Ratnagiri", null, null, null, null, null, null, null, null, true, null);
        Page<Farm> page = farmRepository.findAll(spec, PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getName()).isEqualTo("Alphonso Paradise");
    }

    @Test
    void testSoftDeleteFarm() {
        Farm farm = new Farm(testUser, "Temporary Farm", "OWNED", new BigDecimal("5.000"));
        Farm saved = farmRepository.save(farm);

        saved.setDeletedAt(OffsetDateTime.now());
        saved.setIsActive(false);
        farmRepository.save(saved);

        Optional<Farm> found = farmRepository.findByIdAndDeletedAtIsNull(saved.getId());
        assertThat(found).isEmpty();
    }
}
