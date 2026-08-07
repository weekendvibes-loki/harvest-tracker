package com.harvesttracker.features.farm.specification;

import com.harvesttracker.features.farm.domain.Farm;
import com.harvesttracker.features.farm.domain.FarmFruitType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FarmSpecification {

    private FarmSpecification() {
    }

    public static Specification<Farm> filterFarms(
            String search,
            String name,
            String village,
            String district,
            String state,
            String ownerName,
            Long ownerId,
            String ownershipType,
            String status,
            Boolean isActive,
            Long fruitTypeId) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Exclude soft-deleted records
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase().trim()));
            }

            if (ownershipType != null && !ownershipType.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("ownershipType")), ownershipType.toUpperCase().trim()));
            }

            if (ownerId != null) {
                predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            }

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase().trim() + "%"));
            }

            if (ownerName != null && !ownerName.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("owner").get("name")), "%" + ownerName.toLowerCase().trim() + "%"));
            }

            // Location filters searching in address field
            if (village != null && !village.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + village.toLowerCase().trim() + "%"));
            }
            if (district != null && !district.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + district.toLowerCase().trim() + "%"));
            }
            if (state != null && !state.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + state.toLowerCase().trim() + "%"));
            }

            // Free text search across name, address, owner name
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate addressLike = cb.like(cb.lower(root.get("address")), pattern);
                Predicate ownerLike = cb.like(cb.lower(root.get("owner").get("name")), pattern);
                predicates.add(cb.or(nameLike, addressLike, ownerLike));
            }

            // Filter by cultivated fruit type
            if (fruitTypeId != null) {
                Join<Farm, FarmFruitType> fruitTypesJoin = root.join("farmFruitTypes", JoinType.INNER);
                predicates.add(cb.equal(fruitTypesJoin.get("fruitType").get("id"), fruitTypeId));
                predicates.add(cb.isNull(fruitTypesJoin.get("deletedAt")));
            }

            // Ensure distinct results when joining
            if (query != null) {
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
