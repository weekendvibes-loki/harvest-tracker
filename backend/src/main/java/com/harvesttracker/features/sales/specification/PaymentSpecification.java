package com.harvesttracker.features.sales.specification;

import com.harvesttracker.features.sales.domain.Payment;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentSpecification {

    private PaymentSpecification() {
    }

    public static Specification<Payment> filterPayments(
            Long invoiceId,
            Long paymentMethodId,
            String paymentStatus,
            String referenceNumber,
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

            if (invoiceId != null) {
                predicates.add(cb.equal(root.get("invoice").get("id"), invoiceId));
            }

            if (paymentMethodId != null) {
                predicates.add(cb.equal(root.get("paymentMethod").get("id"), paymentMethodId));
            }

            if (paymentStatus != null && !paymentStatus.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("paymentStatus")), paymentStatus.toUpperCase().trim()));
            }

            if (referenceNumber != null && !referenceNumber.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("referenceNumber")), "%" + referenceNumber.toLowerCase().trim() + "%"));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("paymentDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("paymentDate"), endDate));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate refLike = cb.like(cb.lower(root.get("referenceNumber")), pattern);
                Predicate notesLike = cb.like(cb.lower(root.get("notes")), pattern);
                predicates.add(cb.or(refLike, notesLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
