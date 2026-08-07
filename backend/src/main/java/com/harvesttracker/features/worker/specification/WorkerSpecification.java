package com.harvesttracker.features.worker.specification;

import com.harvesttracker.features.worker.domain.Worker;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class WorkerSpecification {

    private WorkerSpecification() {
    }

    public static Specification<Worker> filterWorkers(
            String search,
            String name,
            String phone,
            String village,
            Long workerTypeId,
            Long farmId,
            String status,
            Boolean isActive) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase().trim()));
            }

            if (farmId != null) {
                predicates.add(cb.equal(root.get("farm").get("id"), farmId));
            }

            if (workerTypeId != null) {
                predicates.add(cb.equal(root.get("workerType").get("id"), workerTypeId));
            }

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase().trim() + "%"));
            }

            if (phone != null && !phone.isBlank()) {
                predicates.add(cb.like(root.get("phone"), "%" + phone.trim() + "%"));
            }

            if (village != null && !village.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + village.toLowerCase().trim() + "%"));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate phoneLike = cb.like(root.get("phone"), pattern);
                Predicate addressLike = cb.like(cb.lower(root.get("address")), pattern);
                Predicate idCardLike = cb.like(cb.lower(root.get("idCardNumber")), pattern);
                predicates.add(cb.or(nameLike, phoneLike, addressLike, idCardLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
