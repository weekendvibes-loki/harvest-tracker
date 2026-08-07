package com.harvesttracker.features.auth.repository;

import com.harvesttracker.features.auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCodeAndDeletedAtIsNull(String code);
}
