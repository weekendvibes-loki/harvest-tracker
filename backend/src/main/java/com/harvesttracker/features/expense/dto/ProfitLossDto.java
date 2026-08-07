package com.harvesttracker.features.expense.dto;

import java.math.BigDecimal;

public class ProfitLossDto {

    private ProfitLossDto() {
    }

    public static class ProfitLossResponse {
        private BigDecimal totalRevenue;
        private BigDecimal totalExpenses;
        private BigDecimal directOperationalExpenses;
        private BigDecimal grossProfit;
        private BigDecimal netProfit;
        private BigDecimal profitMarginPercentage;

        public BigDecimal getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(BigDecimal totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public BigDecimal getTotalExpenses() {
            return totalExpenses;
        }

        public void setTotalExpenses(BigDecimal totalExpenses) {
            this.totalExpenses = totalExpenses;
        }

        public BigDecimal getDirectOperationalExpenses() {
            return directOperationalExpenses;
        }

        public void setDirectOperationalExpenses(BigDecimal directOperationalExpenses) {
            this.directOperationalExpenses = directOperationalExpenses;
        }

        public BigDecimal getGrossProfit() {
            return grossProfit;
        }

        public void setGrossProfit(BigDecimal grossProfit) {
            this.grossProfit = grossProfit;
        }

        public BigDecimal getNetProfit() {
            return netProfit;
        }

        public void setNetProfit(BigDecimal netProfit) {
            this.netProfit = netProfit;
        }

        public BigDecimal getProfitMarginPercentage() {
            return profitMarginPercentage;
        }

        public void setProfitMarginPercentage(BigDecimal profitMarginPercentage) {
            this.profitMarginPercentage = profitMarginPercentage;
        }
    }
}
