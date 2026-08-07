package com.harvesttracker.features.harvest.specification;

import com.harvesttracker.features.harvest.domain.HarvestRecord;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HarvestSpecification {

    private HarvestSpecification() {
    }

    public static Specification<HarvestRecord> filterHarvests(
            Long farmId,
            Long seasonId,
            Long fruitTypeId,
            Long cropVariantId,
            String qualityGrade,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            Long createdBy,
            Long supervisorId,
            String search,
            Boolean isActive) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (farmId != null) {
                predicates.add(cb.equal(root.get("farm").get("id"), farmId));
            }

            if (seasonId != null) {
                predicates.add(cb.equal(root.get("season").get("id"), seasonId));
            }

            if (fruitTypeId != null) {
                predicates.add(cb.equal(root.get("fruitType").get("id"), fruitTypeId));
            }

            if (cropVariantId != null) {
                predicates.add(cb.equal(root.get("cropVariant").get("id"), cropVariantId));
            }

            if (qualityGrade != null && !qualityGrade.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("qualityGrade")), qualityGrade.toUpperCase().trim()));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase().trim()));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("harvestDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("harvestDate"), endDate));
            }

            if (createdBy != null) {
                predicates.add(cb.equal(root.get("createdBy"), createdBy));
            }

            if (supervisorId != null) {
                predicates.add(cb.equal(root.get("supervisor").get("id"), supervisorId));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate farmNameLike = cb.like(cb.lower(root.get("farm").get("name")), pattern);
                Predicate fruitTypeNameLike = cb.like(cb.lower(root.get("fruitType").get("name")), pattern);
                Predicate storageLike = cb.like(cb.lower(root.get("storageLocation")), pattern);
                Predicate notesLike = cb.like(cb.lower(root.get("notes")), pattern);
                predicates.add(cb.or(farmNameLike, fruitTypeNameLike, storageLike, notesLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
