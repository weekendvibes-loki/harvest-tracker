package com.harvesttracker.features.sales.specification;

import com.harvesttracker.features.sales.domain.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    private OrderSpecification() {
    }

    public static Specification<Order> filterOrders(
            Long customerId,
            String orderStatus,
            Long cropVariantId,
            Long farmId,
            LocalDate startDate,
            LocalDate endDate,
            String search,
            Boolean isActive) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }

            if (orderStatus != null && !orderStatus.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("orderStatus")), orderStatus.toUpperCase().trim()));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("orderDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("orderDate"), endDate));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate customerNameLike = cb.like(cb.lower(root.get("customer").get("name")), pattern);
                Predicate notesLike = cb.like(cb.lower(root.get("notes")), pattern);
                predicates.add(cb.or(customerNameLike, notesLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
