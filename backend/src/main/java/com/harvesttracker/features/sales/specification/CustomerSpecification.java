package com.harvesttracker.features.sales.specification;

import com.harvesttracker.features.sales.domain.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecification {

    private CustomerSpecification() {
    }

    public static Specification<Customer> filterCustomers(
            String search,
            String name,
            String phone,
            String email,
            String customerType,
            String status,
            Boolean isActive) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (customerType != null && !customerType.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("customerType")), customerType.toUpperCase().trim()));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase().trim()));
            }

            if (name != null && !name.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase().trim() + "%"));
            }

            if (phone != null && !phone.isBlank()) {
                predicates.add(cb.like(root.get("phone"), "%" + phone.trim() + "%"));
            }

            if (email != null && !email.isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase().trim() + "%"));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate phoneLike = cb.like(root.get("phone"), pattern);
                Predicate emailLike = cb.like(cb.lower(root.get("email")), pattern);
                Predicate addressLike = cb.like(cb.lower(root.get("address")), pattern);
                predicates.add(cb.or(nameLike, phoneLike, emailLike, addressLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
