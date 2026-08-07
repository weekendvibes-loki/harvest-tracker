package com.harvesttracker.features.farm.specification;

import com.harvesttracker.features.farm.domain.Season;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SeasonSpecification {

    private SeasonSpecification() {
    }

    public static Specification<Season> filterSeasons(
            Long farmId,
            Long fruitTypeId,
            Integer year,
            String status,
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

            if (fruitTypeId != null) {
                predicates.add(cb.equal(root.get("fruitType").get("id"), fruitTypeId));
            }

            if (year != null) {
                predicates.add(cb.equal(root.get("year"), year));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase().trim()));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate farmNameLike = cb.like(cb.lower(root.get("farm").get("name")), pattern);
                predicates.add(cb.or(nameLike, farmNameLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
