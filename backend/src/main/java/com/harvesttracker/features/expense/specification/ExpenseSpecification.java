package com.harvesttracker.features.expense.specification;

import com.harvesttracker.features.expense.domain.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSpecification {

    private ExpenseSpecification() {
    }

    public static Specification<Expense> filterExpenses(
            Long farmId,
            Long harvestRecordId,
            Long expenseCategoryId,
            Long paymentMethodId,
            String status,
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

            if (farmId != null) {
                predicates.add(cb.equal(root.get("farm").get("id"), farmId));
            }

            if (harvestRecordId != null) {
                predicates.add(cb.equal(root.get("harvestRecord").get("id"), harvestRecordId));
            }

            if (expenseCategoryId != null) {
                predicates.add(cb.equal(root.get("expenseCategory").get("id"), expenseCategoryId));
            }

            if (paymentMethodId != null) {
                predicates.add(cb.equal(root.get("paymentMethod").get("id"), paymentMethodId));
            }

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("status")), status.toUpperCase().trim()));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), endDate));
            }

            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase().trim() + "%";
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                Predicate notesLike = cb.like(cb.lower(root.get("notes")), pattern);
                Predicate categoryLike = cb.like(cb.lower(root.get("expenseCategory").get("name")), pattern);
                Predicate farmLike = cb.like(cb.lower(root.get("farm").get("name")), pattern);
                predicates.add(cb.or(descLike, notesLike, categoryLike, farmLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
