package com.harvesttracker.features.sales.specification;

import com.harvesttracker.features.sales.domain.Invoice;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceSpecification {

    private InvoiceSpecification() {
    }

    public static Specification<Invoice> filterInvoices(
            Long customerId,
            Long orderId,
            String invoiceNumber,
            String invoiceStatus,
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

            if (orderId != null) {
                predicates.add(cb.equal(root.get("order").get("id"), orderId));
            }

            if (invoiceNumber != null && !invoiceNumber.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("invoiceNumber")), invoiceNumber.toUpperCase().trim()));
            }

            if (invoiceStatus != null && !invoiceStatus.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("invoiceStatus")), invoiceStatus.toUpperCase().trim()));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dueDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dueDate"), endDate));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate numLike = cb.like(cb.lower(root.get("invoiceNumber")), pattern);
                Predicate custNameLike = cb.like(cb.lower(root.get("customer").get("name")), pattern);
                predicates.add(cb.or(numLike, custNameLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
