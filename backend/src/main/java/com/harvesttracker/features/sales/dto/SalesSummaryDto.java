package com.harvesttracker.features.sales.dto;

import java.math.BigDecimal;

public class SalesSummaryDto {

    private SalesSummaryDto() {
    }

    public static class SalesSummaryResponse {
        private long totalCustomers;
        private long totalOrders;
        private BigDecimal totalOrderValue;
        private long draftOrders;
        private long confirmedOrders;
        private long invoicedOrders;
        private long paidOrders;
        private long cancelledOrders;

        public long getTotalCustomers() {
            return totalCustomers;
        }

        public void setTotalCustomers(long totalCustomers) {
            this.totalCustomers = totalCustomers;
        }

        public long getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(long totalOrders) {
            this.totalOrders = totalOrders;
        }

        public BigDecimal getTotalOrderValue() {
            return totalOrderValue;
        }

        public void setTotalOrderValue(BigDecimal totalOrderValue) {
            this.totalOrderValue = totalOrderValue;
        }

        public long getDraftOrders() {
            return draftOrders;
        }

        public void setDraftOrders(long draftOrders) {
            this.draftOrders = draftOrders;
        }

        public long getConfirmedOrders() {
            return confirmedOrders;
        }

        public void setConfirmedOrders(long confirmedOrders) {
            this.confirmedOrders = confirmedOrders;
        }

        public long getInvoicedOrders() {
            return invoicedOrders;
        }

        public void setInvoicedOrders(long invoicedOrders) {
            this.invoicedOrders = invoicedOrders;
        }

        public long getPaidOrders() {
            return paidOrders;
        }

        public void setPaidOrders(long paidOrders) {
            this.paidOrders = paidOrders;
        }

        public long getCancelledOrders() {
            return cancelledOrders;
        }

        public void setCancelledOrders(long cancelledOrders) {
            this.cancelledOrders = cancelledOrders;
        }
    }
}
