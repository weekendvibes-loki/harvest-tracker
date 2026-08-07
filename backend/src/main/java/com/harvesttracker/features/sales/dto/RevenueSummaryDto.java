package com.harvesttracker.features.sales.dto;

import java.math.BigDecimal;

public class RevenueSummaryDto {

    private RevenueSummaryDto() {
    }

    public static class RevenueSummaryResponse {
        private BigDecimal totalInvoicedAmount;
        private BigDecimal totalCollectedRevenue;
        private BigDecimal totalPendingRevenue;
        private long totalInvoices;
        private long paidInvoices;
        private long unpaidInvoices;
        private long overdueInvoices;

        public BigDecimal getTotalInvoicedAmount() {
            return totalInvoicedAmount;
        }

        public void setTotalInvoicedAmount(BigDecimal totalInvoicedAmount) {
            this.totalInvoicedAmount = totalInvoicedAmount;
        }

        public BigDecimal getTotalCollectedRevenue() {
            return totalCollectedRevenue;
        }

        public void setTotalCollectedRevenue(BigDecimal totalCollectedRevenue) {
            this.totalCollectedRevenue = totalCollectedRevenue;
        }

        public BigDecimal getTotalPendingRevenue() {
            return totalPendingRevenue;
        }

        public void setTotalPendingRevenue(BigDecimal totalPendingRevenue) {
            this.totalPendingRevenue = totalPendingRevenue;
        }

        public long getTotalInvoices() {
            return totalInvoices;
        }

        public void setTotalInvoices(long totalInvoices) {
            this.totalInvoices = totalInvoices;
        }

        public long getPaidInvoices() {
            return paidInvoices;
        }

        public void setPaidInvoices(long paidInvoices) {
            this.paidInvoices = paidInvoices;
        }

        public long getUnpaidInvoices() {
            return unpaidInvoices;
        }

        public void setUnpaidInvoices(long unpaidInvoices) {
            this.unpaidInvoices = unpaidInvoices;
        }

        public long getOverdueInvoices() {
            return overdueInvoices;
        }

        public void setOverdueInvoices(long overdueInvoices) {
            this.overdueInvoices = overdueInvoices;
        }
    }
}
