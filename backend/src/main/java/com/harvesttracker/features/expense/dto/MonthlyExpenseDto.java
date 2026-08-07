package com.harvesttracker.features.expense.dto;

import java.math.BigDecimal;

public class MonthlyExpenseDto {

    private MonthlyExpenseDto() {
    }

    public static class MonthlyExpenseResponse {
        private int year;
        private int month;
        private String monthName;
        private BigDecimal totalAmount;
        private long expenseCount;

        public MonthlyExpenseResponse() {
        }

        public MonthlyExpenseResponse(int year, int month, String monthName, BigDecimal totalAmount, long expenseCount) {
            this.year = year;
            this.month = month;
            this.monthName = monthName;
            this.totalAmount = totalAmount;
            this.expenseCount = expenseCount;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public String getMonthName() {
            return monthName;
        }

        public void setMonthName(String monthName) {
            this.monthName = monthName;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public long getExpenseCount() {
            return expenseCount;
        }

        public void setExpenseCount(long expenseCount) {
            this.expenseCount = expenseCount;
        }
    }
}
