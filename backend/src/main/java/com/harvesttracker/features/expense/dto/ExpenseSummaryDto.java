package com.harvesttracker.features.expense.dto;

import java.math.BigDecimal;

public class ExpenseSummaryDto {

    private ExpenseSummaryDto() {
    }

    public static class ExpenseSummaryResponse {
        private long totalExpensesCount;
        private BigDecimal totalExpenseAmount;
        private BigDecimal totalRecordedAmount;
        private BigDecimal totalApprovedAmount;
        private BigDecimal totalRejectedAmount;
        private long totalFarmsCount;
        private long totalCategoriesCount;

        public long getTotalExpensesCount() {
            return totalExpensesCount;
        }

        public void setTotalExpensesCount(long totalExpensesCount) {
            this.totalExpensesCount = totalExpensesCount;
        }

        public BigDecimal getTotalExpenseAmount() {
            return totalExpenseAmount;
        }

        public void setTotalExpenseAmount(BigDecimal totalExpenseAmount) {
            this.totalExpenseAmount = totalExpenseAmount;
        }

        public BigDecimal getTotalRecordedAmount() {
            return totalRecordedAmount;
        }

        public void setTotalRecordedAmount(BigDecimal totalRecordedAmount) {
            this.totalRecordedAmount = totalRecordedAmount;
        }

        public BigDecimal getTotalApprovedAmount() {
            return totalApprovedAmount;
        }

        public void setTotalApprovedAmount(BigDecimal totalApprovedAmount) {
            this.totalApprovedAmount = totalApprovedAmount;
        }

        public BigDecimal getTotalRejectedAmount() {
            return totalRejectedAmount;
        }

        public void setTotalRejectedAmount(BigDecimal totalRejectedAmount) {
            this.totalRejectedAmount = totalRejectedAmount;
        }

        public long getTotalFarmsCount() {
            return totalFarmsCount;
        }

        public void setTotalFarmsCount(long totalFarmsCount) {
            this.totalFarmsCount = totalFarmsCount;
        }

        public long getTotalCategoriesCount() {
            return totalCategoriesCount;
        }

        public void setTotalCategoriesCount(long totalCategoriesCount) {
            this.totalCategoriesCount = totalCategoriesCount;
        }
    }
}
