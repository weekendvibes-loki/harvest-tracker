package com.harvesttracker.features.masterdata.repository.spec;

import com.harvesttracker.features.masterdata.domain.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MasterDataSpecification {

    public static Specification<FruitType> fruitTypeSpec(String search, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate codeLike = cb.like(cb.lower(root.get("code")), searchPattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameLike, codeLike, descLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<CropVariant> cropVariantSpec(Long fruitTypeId, String search, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (fruitTypeId != null) {
                predicates.add(cb.equal(root.get("fruitType").get("id"), fruitTypeId));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate codeLike = cb.like(cb.lower(root.get("code")), searchPattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameLike, codeLike, descLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<UnitOfMeasure> unitOfMeasureSpec(String measureType, String search, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (measureType != null && !measureType.trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("measureType")), measureType.trim().toUpperCase()));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate codeLike = cb.like(cb.lower(root.get("code")), searchPattern);
                predicates.add(cb.or(nameLike, codeLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<WorkerType> workerTypeSpec(String search, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate codeLike = cb.like(cb.lower(root.get("code")), searchPattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameLike, codeLike, descLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<PaymentMethod> paymentMethodSpec(String search, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate codeLike = cb.like(cb.lower(root.get("code")), searchPattern);
                predicates.add(cb.or(nameLike, codeLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ExpenseCategory> expenseCategorySpec(String search, Boolean isActive) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            if (search != null && !search.trim().isEmpty()) {
                String searchPattern = "%" + search.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), searchPattern);
                Predicate codeLike = cb.like(cb.lower(root.get("code")), searchPattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), searchPattern);
                predicates.add(cb.or(nameLike, codeLike, descLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
