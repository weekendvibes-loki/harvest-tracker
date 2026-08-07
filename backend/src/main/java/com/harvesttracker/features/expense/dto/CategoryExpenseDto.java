package com.harvesttracker.features.expense.dto;

import java.math.BigDecimal;

public class CategoryExpenseDto {

    private CategoryExpenseDto() {
    }

    public static class CategoryExpenseResponse {
        private Long categoryId;
        private String categoryName;
        private String categoryCode;
        private BigDecimal totalAmount;
        private BigDecimal percentage;
        private long expenseCount;

        public CategoryExpenseResponse() {
        }

        public CategoryExpenseResponse(Long categoryId, String categoryName, String categoryCode, BigDecimal totalAmount, BigDecimal percentage, long expenseCount) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.categoryCode = categoryCode;
            this.totalAmount = totalAmount;
            this.percentage = percentage;
            this.expenseCount = expenseCount;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryCode() {
            return categoryCode;
        }

        public void setCategoryCode(String categoryCode) {
            this.categoryCode = categoryCode;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }

        public long getExpenseCount() {
            return expenseCount;
        }

        public void setExpenseCount(long expenseCount) {
            this.expenseCount = expenseCount;
        }
    }
}
